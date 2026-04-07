package com.example.wave.services;

import com.example.wave.DTOs.views.cardItemsViews.CardItemListView;
import com.example.wave.DTOs.views.cardItemsViews.CardItemView;
import com.example.wave.debug.UserScore;
import com.example.wave.entities.*;
import com.example.wave.repositories.*;
import com.example.wave.services.spotify.SpotifyCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final UserTrackPreferenceRepository userTrackPreferenceRepository;
    private final UserArtistPreferenceRepository userArtistPreferenceRepository;
    private final UserAccountRepository userAccountRepository;
    private final SpotifyCatalogService spotifyCatalogService;

    public List<UserAccount> getRecommendationList(int id){
        return  getRecommendationListAndValues(id).stream().map(e -> e.userAccount).toList();
    }
    public List<UserScore> getRecommendationListAndValues(int mainId) {
        List<UserTrackPreference> userTrackPreferences = userTrackPreferenceRepository.findAll();

        record Edge(int to, boolean fav) {}
        record EdgeBfs(int to, int steps, int score, boolean prevFav) {}

        Map<Integer, List<Edge>> adjList = new HashMap<>();

        // build adjacency list
        for (UserTrackPreference preference : userTrackPreferences) {
            int userId = preference.getUser().getId().intValue();
            int trackId = -preference.getTrack().getId().intValue();
            boolean fav = preference.getPreferenceType() == PreferenceType.FAVORITE;

            adjList.computeIfAbsent(userId, k -> new ArrayList<>());
            adjList.computeIfAbsent(trackId, k -> new ArrayList<>());

            adjList.get(userId).add(new Edge(trackId, fav));
            adjList.get(trackId).add(new Edge(userId, fav));
        }

        List<Edge> startEdges = adjList.get(mainId);
        if (startEdges == null || startEdges.isEmpty()) return List.of();

        Queue<EdgeBfs> queue = new ArrayDeque<>();
        Set<Long> usedEdges = new HashSet<>();
        Map<Integer, Integer> found = new HashMap<>();

        // add base edges
        for (Edge edge : startEdges) {
            // main user's favourite preference should have more significance so 5000 instead of 3000
            int baseScore = edge.fav ? 5000 : 1200;
            queue.add(new EdgeBfs(edge.to, 1, baseScore, edge.fav));
            usedEdges.add(edgeKey(mainId, edge.to));
        }

        // BFS queue
        while (!queue.isEmpty()) {
            EdgeBfs cur = queue.poll();

            boolean person = cur.to > 0;
            if (person && cur.to != mainId) found.merge(cur.to, cur.score, Integer::sum);

            if (cur.steps >= 6) continue;

            for (Edge next : adjList.getOrDefault(cur.to, List.of())) {
                long key = edgeKey(cur.to, next.to);
                if (!usedEdges.add(key)) continue;

                int edgeScore = next.fav ? 3000 : 1200;
                // both have same favourite track multiplier, direct double favourite relation should be most significant
                int favBonus = (!person && cur.prevFav && next.fav) ? cur.steps == 1 ? 5 : 3 : 1;

                int depthPenalty = (cur.steps + 1) * (cur.steps + 1) * 2;

                int nextScore = (cur.score + edgeScore * favBonus) / depthPenalty;
                if (nextScore <= 0) continue;

                queue.add(new EdgeBfs(next.to, cur.steps + 1, nextScore, next.fav));
            }
        }

        // Add contribution for same artists (only direct, without graph)
        List<UserArtistPreference> userArtistPreferences = userArtistPreferenceRepository.findAll();

        Map<Integer, Set<Long>> userArtists = new HashMap<>();

        for (UserArtistPreference preference : userArtistPreferences) {
            int userId = preference.getUser().getId().intValue();
            long artistId = preference.getArtist().getId();

            userArtists
                    .computeIfAbsent(userId, k -> new HashSet<>())
                    .add(artistId);
        }

        Set<Long> mainArtists = userArtists.getOrDefault(mainId, Set.of());

        for (Map.Entry<Integer, Set<Long>> entry : userArtists.entrySet()) {
            int userId = entry.getKey();
            if (userId == mainId) continue;

            int commonArtists = 0;
            for (Long artistId : entry.getValue()) {
                if (mainArtists.contains(artistId)) {
                    commonArtists++;
                }
            }

            if (commonArtists > 0) {
                found.merge(userId, commonArtists * 1000, Integer::sum);
            }
        }

        return found.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(entry -> new UserScore(userAccountRepository.findById((long) entry.getKey()).orElseThrow(), entry.getValue()))
                .toList();
    }

    private long edgeKey(int a, int b) {
        long x = a;
        long y = b;
        if (x > y) {
            long tmp = x;
            x = y;
            y = tmp;
        }
        return (x << 32) ^ (y & 0xffffffffL);
    }

    public List<CardItemListView> generateCardSimilarities(Long mainId, Long otherId){

        List<TrackR> mainTracksR = userTrackPreferenceRepository.findAllByUser_Id(mainId).stream().map(e -> new TrackR(e.getTrack(), e.getPreferenceType())).toList();
        List<TrackR> otherTracks = userTrackPreferenceRepository.findAllByUser_Id(otherId).stream().map(e -> new TrackR(e.getTrack(), e.getPreferenceType())).toList();

        List<Artist> mainArtists = userArtistPreferenceRepository.findAllByUser_Id(mainId).stream().map(UserArtistPreference::getArtist).toList();
        List<Artist> otherArtists = userArtistPreferenceRepository.findAllByUser_Id(otherId).stream().map(UserArtistPreference::getArtist).toList();

        List<CardItemView> similarFavourites = new ArrayList<>();
        List<CardItemView> similarLikes = new ArrayList<>();
        List<CardItemView> similarAuthors = new ArrayList<>();

        HashSet<Track> otherFavourites = new HashSet<>();
        HashSet<Track> otherLikes = new HashSet<>();

        for(TrackR otherTrack : otherTracks) {
            if (otherTrack.preferenceType == PreferenceType.FAVORITE) otherFavourites.add(otherTrack.track);
            otherLikes.add(otherTrack.track);
        }

        mainTracksR = sortWithSlightRandom(mainTracksR);

        for(TrackR mainTrackR : mainTracksR){
            Track mainTrack = mainTrackR.track;
            if(otherLikes.contains(mainTrack){

                if(otherFavourites.contains(mainTrack) && mainTrackR.preferenceType == PreferenceType.FAVORITE)
                    similarFavourites.add(
                            new CardItemView(mainTrack.getTitle(), mainTrack.getArtist(), mainTrack.get)
                    )
                }
            }
        }
    }
    record TrackR(Track track, PreferenceType preferenceType) {}
    record ItemWithOrder(TrackR item, double sortKey) {}

    List<TrackR> sortWithSlightRandom(List<TrackR> items) {
        Random random = new Random();

        return items.stream()
                .map(item -> {
                    int popularity = item.track.getPopularity();
                    double noise = random.nextDouble(-1.0, 1.0) *
                                   random.nextDouble(-1.0, 1.0) *
                                   random.nextDouble(-1.0, 1.0) *
                                   random.nextDouble(-1.0, 1.0) *
                                   random.nextDouble(-1.0, 1.0) * 100;
                    return new ItemWithOrder(item, popularity + noise);
                })
                .sorted(Comparator.comparingDouble(ItemWithOrder::sortKey))
                .map(ItemWithOrder::item)
                .toList();
    }
}

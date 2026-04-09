package com.example.wave.services;

import com.example.wave.DTOs.views.cardItemsViews.CardItemListView;
import com.example.wave.DTOs.views.cardItemsViews.CardItemView;
import com.example.wave.DTOs.views.cardItemsViews.ItemType;
import com.example.wave.other.UserScore;
import com.example.wave.entities.*;
import com.example.wave.repositories.*;
import com.example.wave.services.spotify.SpotifyCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final UserTrackPreferenceRepository userTrackPreferenceRepository;
    private final UserArtistPreferenceRepository userArtistPreferenceRepository;
    private final UserAccountRepository userAccountRepository;
    private final TrackRepository trackRepository;
    private final SpotifyCatalogService spotifyCatalogService;
    @Cacheable("userRecommendations")
    public List<UserAccount> getRecommendationList(int id){
        return  getRecommendationListAndValues(id).stream().map(UserScore::userAccount).toList();
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
            int popularity = trackRepository.findById((long) -edge.to).orElseThrow().getPopularity();
            double popularityMultiplier = 25.0 / (popularity + 25.0);
            queue.add(new EdgeBfs(edge.to, 1, (int) (baseScore * popularityMultiplier), edge.fav));
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

                int trackId = person ? next.to : cur.to;
                int popularity = trackRepository.findById((long) -trackId).orElseThrow().getPopularity();
                double popularityMultiplier = 25.0 / (popularity + 25.0);

                int depthPenalty = (cur.steps + 1) * (cur.steps + 1) * 2;

                int nextScore = (cur.score + (int)(edgeScore * popularityMultiplier * favBonus)) / depthPenalty;
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

        List<Integer> scores = new ArrayList<>(found.values());
        double med = median(scores);
        double mad = mad(scores, med);

        // robust scale
        double scale = Math.max(1.0, mad * 1.4826);

        // randomness strength
        double temperature = 0.30;

        Random random = new Random();

        return found.entrySet().stream()
                .map(entry -> {
                    double z = (entry.getValue() - med) / scale;
                    double noisyKey = z + temperature * gumbel(random);
                    return new RankedUser(entry.getKey(), entry.getValue(), noisyKey);
                })
                .sorted(Comparator.comparingDouble(RankedUser::sortKey).reversed())
                .map(item -> new UserScore(
                        userAccountRepository.findById((long) item.userId()).orElseThrow(),
                        item.score()
                ))
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

    record RankedUser(int userId, int score, double sortKey) {}

    private static double median(List<Integer> values) {
        if (values.isEmpty()) return 0.0;

        List<Integer> sorted = new ArrayList<>(values);
        sorted.sort(Integer::compareTo);

        int n = sorted.size();
        if ((n & 1) == 1) {
            return sorted.get(n / 2);
        }
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }

    private static double mad(List<Integer> values, double median) {
        if (values.isEmpty()) return 1.0;

        List<Double> deviations = new ArrayList<>(values.size());
        for (int value : values) {
            deviations.add(Math.abs(value - median));
        }
        deviations.sort(Double::compareTo);

        int n = deviations.size();
        double mad;
        if ((n & 1) == 1) {
            mad = deviations.get(n / 2);
        } else {
            mad = (deviations.get(n / 2 - 1) + deviations.get(n / 2)) / 2.0;
        }

        return mad;
    }

    private static double gumbel(Random random) {
        double u = random.nextDouble();
        u = Math.max(u, 1e-12);
        return -Math.log(-Math.log(u));
    }
    @Cacheable("cardSimilarities")
    public List<CardItemListView> generateCardSimilarities(Long mainId, Long otherId){
        List<TrackR> mainTracksR = userTrackPreferenceRepository.findAllByUser_Id(mainId).stream().map(e -> new TrackR(e.getTrack(), e.getPreferenceType())).toList();
        List<TrackR> otherTracks = userTrackPreferenceRepository.findAllByUser_Id(otherId).stream().map(e -> new TrackR(e.getTrack(), e.getPreferenceType())).toList();

        List<Artist> mainArtists = userArtistPreferenceRepository.findAllByUser_Id(mainId).stream().map(UserArtistPreference::getArtist).toList();
        HashSet<Artist> otherArtists = userArtistPreferenceRepository.findAllByUser_Id(otherId).stream().map(UserArtistPreference::getArtist).collect(Collectors.toCollection(HashSet::new));

        List<CardItemView> similarFavourites = new ArrayList<>();
        List<CardItemView> similarLikes = new ArrayList<>();
        List<CardItemView> similarArtists = new ArrayList<>();

        HashSet<Track> otherFavourites = new HashSet<>();
        HashSet<Track> otherLikes = new HashSet<>();

        for(TrackR otherTrack : otherTracks) {
            if (otherTrack.preferenceType() == PreferenceType.FAVORITE) otherFavourites.add(otherTrack.track());
            otherLikes.add(otherTrack.track());
        }

        mainTracksR = sortWithSlightRandom(mainTracksR);

        for(TrackR mainTrackR : mainTracksR){
            Track mainTrack = mainTrackR.track();
            if(otherLikes.contains(mainTrack)) {
                String imageUrl = spotifyCatalogService.getTrackBySpotifyId(mainTrack.getExternalId()).imageUrl();
                if (otherFavourites.contains(mainTrack) && mainTrackR.preferenceType == PreferenceType.FAVORITE)
                    similarFavourites.add(
                            new CardItemView(mainTrack.getTitle(),
                                    mainTrack.getArtist().getName(),
                                    imageUrl)
                    );
                else similarLikes.add(
                        new CardItemView(mainTrack.getTitle(),
                                mainTrack.getArtist().getName(),
                                imageUrl)

                );
            }

            if(similarFavourites.size() > 4 && similarLikes.size() > 4) break;
        }

        for(Artist artist : mainArtists){
            if(otherArtists.contains(artist)) similarArtists.add(
                    new CardItemView(
                            artist.getName(),
                            " ",
                            spotifyCatalogService.getArtistBySpotifyId(artist.getExternalId()).imageUrl())
            );
            if(similarArtists.size() > 4) break;
        }

        CardItemListView similarFavouritesCILV = new CardItemListView(ItemType.FAV, similarFavourites.stream().limit(5).toList());
        CardItemListView similarLikesCILV = new CardItemListView(ItemType.LIK, similarLikes.stream().limit(5).toList());
        CardItemListView similarArtistsCILV = new CardItemListView(ItemType.ART, similarArtists.stream().limit(5).toList());

        List<CardItemListView> result = new ArrayList<>();
        if(similarFavouritesCILV.itemList().size() > 0) result.add(similarFavouritesCILV);
        if(similarArtistsCILV.itemList().size() > 2 * similarLikesCILV.itemList().size()){
            result.add(similarArtistsCILV);
            result.add(similarLikesCILV);
        }
        else {
            result.add(similarLikesCILV);
            result.add(similarArtistsCILV);
        }

        return result;
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

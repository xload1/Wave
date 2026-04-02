package com.example.wave.services;

import com.example.wave.entities.PreferenceType;
import com.example.wave.entities.UserAccount;
import com.example.wave.entities.UserTrackPreference;
import com.example.wave.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final UserTrackPreferenceRepository userTrackPreferenceRepository;
    private final UserArtistPreferenceRepository userArtistPreferenceRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final TrackRepository trackRepository;
    private final UserAccountRepository userAccountRepository;

    public List<UserAccount> getRecommendationList(int main_id){
        List<UserTrackPreference> userTrackPreferences = userTrackPreferenceRepository.findAll();

        int userCount = (int) userAccountRepository.count();
        int trackCount = (int) trackRepository.count();

        //create basic adjacency list
        record Edge(int to, boolean fav, int pop) {}
        HashMap<Integer, List<Edge>> adjList = new HashMap<>();

        for(UserTrackPreference preference : userTrackPreferences){
            Integer uId = preference.getUser().getId().intValue();
            Integer tId = preference.getTrack().getId().intValue() + userCount + 1;

            adjList.computeIfAbsent(uId, k -> new ArrayList<Edge>());
            adjList.computeIfAbsent(tId, k -> new ArrayList<Edge>());

            adjList.get(uId).add(new Edge(tId, preference.getPreferenceType().equals(PreferenceType.FAVORITE), preference.getTrack().getPopularity()));
            adjList.get(tId).add(new Edge(uId, preference.getPreferenceType().equals(PreferenceType.FAVORITE), preference.getTrack().getPopularity()));
        }

        int MAXID = userCount + trackCount + 1;
        boolean visited[][] = new boolean[MAXID][MAXID];

        record EdgeBFS(int to, int steps, int curScore, boolean prevFav) {}

        Queue<EdgeBFS> queue = new ArrayDeque<>();

        //BFS base
        for(Edge e : adjList.get(main_id)) {
            int popScaled = e.pop;
            queue.add(new EdgeBFS(e.to, 1, popScaled*100, e.fav));
            visited[main_id][e.to] = visited[e.to][main_id] = true;
        }

        HashMap<Integer, Integer> found = new HashMap<>();

        while(!queue.isEmpty()){
            EdgeBFS cur = queue.poll();

            boolean person = cur.to <= userCount;
            if(found.containsKey(cur.to)) found.put(cur.to, found.get(cur.to) + cur.curScore);
            else found.put(cur.to, cur.curScore);

            if(cur.steps > 7) continue;

            for(Edge neighbour : adjList.get(cur.to)){
                int popScaled = neighbour.pop;
                int favBonus = !person && cur.prevFav && neighbour.fav ? 4 : 1;
                int stepMul = cur.steps * 10;

                int score = cur.curScore + (100 * favBonus / stepMul) * popScaled;

                queue.add(new EdgeBFS(neighbour.to, cur.steps + 1, score, neighbour.fav));
            }
        }

        return found.values().stream().sorted(Integer::compareTo).map( e -> userAccountRepository.findById(e.longValue()).orElseThrow()).toList();
    }
}

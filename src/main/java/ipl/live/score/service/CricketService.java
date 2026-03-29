package ipl.live.score.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipl.live.score.client.CricketApiClient;
import ipl.live.score.model.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CricketService {

    private final CricketApiClient apiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable("liveScore")
    public List<MatchResponse> getLiveScore() throws Exception {

        String response = apiClient.fetchLiveMatches();
        System.out.println(response);

        JsonNode root = objectMapper.readTree(response);
        JsonNode matches = root.get("data");

        List<MatchResponse> result = new ArrayList<>();

        if (matches == null) {
            return List.of(new MatchResponse("No Match", "-", "-"));
        }

        List<JsonNode> liveMatches = new ArrayList<>();
        List<JsonNode> upcomingMatches = new ArrayList<>();
        List<JsonNode> recentMatches = new ArrayList<>();

        for (JsonNode match : matches) {

            boolean started = match.get("matchStarted").asBoolean();
            boolean ended = match.get("matchEnded").asBoolean();

            if (started && !ended) {
                liveMatches.add(match);
            } else if (!started) {
                upcomingMatches.add(match);
            } else {
                recentMatches.add(match);
            }
        }

        // ✅ Priority: LIVE → UPCOMING → RECENT
        addMatches(result, liveMatches, 3);
        if (result.size() < 3) addMatches(result, upcomingMatches, 3 - result.size());
        if (result.size() < 3) addMatches(result, recentMatches, 3 - result.size());

        return result;
    }

    private void addMatches(List<MatchResponse> result, List<JsonNode> matches, int limit) {
        for (JsonNode match : matches) {
            if (result.size() >= 3) break;
            result.add(buildResponse(match));
        }
    }

    private MatchResponse buildResponse(JsonNode match) {

        JsonNode teams = match.get("teams");

        String team1 = teams.get(0).asText();
        String team2 = teams.get(1).asText();

        String matchName = team1 + " vs " + team2;
        String status = match.get("status").asText();

        String score = "Match not started";

        JsonNode scores = match.get("score");

        if (scores != null && scores.size() > 0) {

            StringBuilder scoreBuilder = new StringBuilder();

            for (JsonNode inning : scores) {
                scoreBuilder.append(inning.get("inning").asText())
                        .append(": ")
                        .append(inning.get("r").asInt()).append("/")
                        .append(inning.get("w").asInt())
                        .append(" (").append(inning.get("o").asDouble()).append(")")
                        .append(" | ");
            }

            score = scoreBuilder.toString();
        }

        return new MatchResponse(matchName, status, score);
    }
}
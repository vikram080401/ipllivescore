package ipl.live.score.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipl.live.score.client.CricketApiClient;
import ipl.live.score.model.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CricketService {

    private final CricketApiClient apiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable("liveScore")
    public MatchResponse getLiveScore() throws Exception {

        String response = apiClient.fetchLiveMatches();
        System.out.println(response); // debug

        JsonNode root = objectMapper.readTree(response);
        JsonNode matches = root.get("data");

        if (matches == null || matches.size() == 0) {
            return new MatchResponse("No Match Available", "-", "-");
        }

        JsonNode fallbackMatch = null;

        for (JsonNode match : matches) {

            boolean started = match.get("matchStarted").asBoolean();
            boolean ended = match.get("matchEnded").asBoolean();

            // ✅ PRIORITY 1 → LIVE MATCH
            if (started && !ended) {
                return buildResponse(match);
            }

            // ✅ PRIORITY 2 → RECENT / UPCOMING
            if (fallbackMatch == null) {
                fallbackMatch = match;
            }
        }

        // ✅ If no live match, return fallback
        return buildResponse(fallbackMatch);
    }

    // 🔥 Helper method
    private MatchResponse buildResponse(JsonNode match) {

        JsonNode teams = match.get("teams");

        String team1 = teams.get(0).asText();
        String team2 = teams.get(1).asText();

        String matchName = team1 + " vs " + team2;
        String status = match.get("status").asText();

        String score = "Match not started";

        JsonNode scores = match.get("score");

        if (scores != null && scores.size() > 0) {

            // 👉 Show BOTH innings if available
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
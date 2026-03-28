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

        JsonNode root = objectMapper.readTree(response);
        JsonNode matches = root.get("data");

        MatchResponse upcomingMatch = null;
        String upcomingMatchTime = null;

        for (JsonNode match : matches) {

            // ✅ 🔥 BEST IPL FILTER (series name based)
            boolean isIplMatch = match.get("name")
                    .asText()
                    .toLowerCase()
                    .contains("indian premier league");

            if (!isIplMatch) continue;

            boolean started = match.get("matchStarted").asBoolean();
            boolean ended = match.get("matchEnded").asBoolean();

            // ✅ 1. LIVE match
            if (started && !ended) {
                return buildResponse(match);
            }

            // ✅ 2. UPCOMING match (pick earliest)
            if (!started) {
                String matchTime = match.get("dateTimeGMT").asText();

                if (upcomingMatch == null || upcomingMatchTime == null ||
                        matchTime.compareTo(upcomingMatchTime) < 0) {

                    upcomingMatch = buildResponse(match);
                    upcomingMatchTime = matchTime;
                }
            }
        }

        // ✅ Return upcoming if no live
        if (upcomingMatch != null) {
            return upcomingMatch;
        }

        return new MatchResponse("No IPL Match", "-", "-");
    }

    // 🔥 Helper method
    private MatchResponse buildResponse(JsonNode match) {

        JsonNode teams = match.get("teams");

        String team1 = teams.get(0).asText();
        String team2 = teams.get(1).asText();

        String matchName = team1 + " vs " + team2;
        String status = match.get("status").asText();

        String score = "-";

        JsonNode scores = match.get("score");

        if (scores != null && scores.size() > 0) {
            JsonNode lastInning = scores.get(scores.size() - 1);

            score = lastInning.get("r").asInt() + "/" +
                    lastInning.get("w").asInt() + " (" +
                    lastInning.get("o").asDouble() + ")";
        }

        return new MatchResponse(matchName, status, score);
    }
}
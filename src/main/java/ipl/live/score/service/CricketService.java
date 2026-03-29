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
        System.out.println(response);
        JsonNode root = objectMapper.readTree(response);

        // Cricbuzz structure
        JsonNode typeMatches = root.get("typeMatches");

        if (typeMatches == null) {
            return new MatchResponse("No IPL Match", "-", "-");
        }

        for (JsonNode typeMatch : typeMatches) {

            JsonNode seriesMatches = typeMatch.get("seriesMatches");

            if (seriesMatches == null) continue;

            for (JsonNode seriesWrapper : seriesMatches) {

                JsonNode series = seriesWrapper.get("seriesAdWrapper");

                if (series == null) continue;

                String seriesName = series.get("seriesName").asText().toLowerCase();

                // 🔥 IPL filter
                if (!seriesName.contains("indian premier league")) {
                    continue;
                }

                JsonNode matches = series.get("matches");

                for (JsonNode matchWrapper : matches) {

                    JsonNode matchInfo = matchWrapper.get("matchInfo");
                    JsonNode matchScore = matchWrapper.get("matchScore");

                    String team1 = matchInfo.get("team1").get("teamName").asText();
                    String team2 = matchInfo.get("team2").get("teamName").asText();

                    String status = matchInfo.get("status").asText();

                    String score = "-";

                    if (matchScore != null && matchScore.has("team1Score")) {
                        JsonNode t1 = matchScore.get("team1Score").get("inngs1");

                        score = t1.get("runs").asInt() + "/" +
                                t1.get("wickets").asInt() + " (" +
                                t1.get("overs").asText() + ")";
                    }

                    return new MatchResponse(
                            team1 + " vs " + team2,
                            status,
                            score
                    );
                }
            }
        }

        return new MatchResponse("No IPL Match", "-", "-");
    }
}
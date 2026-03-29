package ipl.live.score.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CricketApiClient implements CricketClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_KEY = "d8fa8dd257msh90b39c9b7cd8de3p1490c7jsn17c5fd392162"; // 🔥 replace
    private static final String HOST = "cricbuzz-cricket.p.rapidapi.com";

    private static final String URL =
            "https://cricbuzz-cricket.p.rapidapi.com/matches/v1/live";
    public String fetchLiveMatches() {
        System.out.println("Calling RapidAPI...");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", API_KEY);
        headers.set("X-RapidAPI-Host", HOST);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }
}
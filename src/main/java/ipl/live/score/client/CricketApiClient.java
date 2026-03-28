package ipl.live.score.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CricketApiClient implements CricketClient{

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_KEY = "913847c1-08ec-42d3-a01c-9238b34aa161";
    private static final String URL =
            "https://api.cricapi.com/v1/currentMatches?apikey=" + API_KEY;

    public String fetchLiveMatches() {
        return restTemplate.getForObject(URL, String.class);
    }
}
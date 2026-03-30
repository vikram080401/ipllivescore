package ipl.live.score.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CricketApiClient implements CricketClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_KEY = "0bc7864a-88c4-4301-a250-6e45bda4db58";

    private static final String URL =
            "https://api.cricapi.com/v1/matches?apikey=" + API_KEY + "&offset=0";

    @Override
    public String fetchLiveMatches() {
        System.out.println("Calling CricAPI...");
        return restTemplate.getForObject(URL, String.class);
    }
}
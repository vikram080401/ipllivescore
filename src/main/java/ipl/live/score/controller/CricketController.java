package ipl.live.score.controller;

import ipl.live.score.model.MatchResponse;
import ipl.live.score.service.CricketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cricket")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://abhinavkumarvikram.co.in")
public class CricketController {

    private final CricketService cricketService;

    @GetMapping("/live-score")
    public List<MatchResponse> getLiveScore() throws Exception {
        return cricketService.getLiveScore();
    }
}
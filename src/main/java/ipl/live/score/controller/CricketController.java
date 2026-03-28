package ipl.live.score.controller;

import ipl.live.score.model.MatchResponse;
import ipl.live.score.service.CricketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/cricket")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CricketController {

    private final CricketService service;

    @GetMapping("/live-score")
    public MatchResponse getLiveScore() throws Exception {
        return service.getLiveScore();
    }
}
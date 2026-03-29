package ipl.live.score.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchResponse {

    private String match;
    private String status;
    private String score;
}
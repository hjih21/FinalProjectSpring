package practical.llm.score.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ScoreInsertParam {
    private Integer scoreId; // useGeneratedKeys
    private Integer questionId;
    private Integer evalModelId;
    private Integer answerId;

    private String graderPromptText;
    // ↓ 전부 "문자열 JSON"
    private String graderPromptParamsJson;
    private String graderResponseRawJson;
    private String metricsJson;

    private BigDecimal overallScore;
}

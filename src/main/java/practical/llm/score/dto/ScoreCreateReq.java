package practical.llm.score.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ScoreCreateReq {
    private Integer documentId;
    private Integer evalModelId;
    private Integer answerId;

    private String graderPromptText;

    // 아래 셋은 Map<Object>로 받아도 되지만 Service에서 반드시 문자열로 변환하세요.
    private Map<String, Object> graderPromptParams;
    private Object graderResponseRaw; // null 가능
    private Map<String, Object> metricsJson;

    private BigDecimal overallScore;
}
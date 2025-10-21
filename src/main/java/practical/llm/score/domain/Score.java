package practical.llm.score.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Score {
    private Integer scoreId;

    private Integer questionId;          // 필수: QuestionMapper로 documentId→최신 question_id 조회
    private Integer evalModelId;
    private Integer answerId;

    private String  graderPromptText;
    private Object  graderPromptParams;

    private String  graderResponseRaw;   // nullable
    private Object  metricsJson;         // JSON or Map
    private Double  overallScore;        // DECIMAL(6,3) nullable
}
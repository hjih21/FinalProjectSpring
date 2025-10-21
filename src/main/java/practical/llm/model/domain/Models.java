package practical.llm.model.domain;

import lombok.Data;

@Data
public class Models {
    private Long modelId;
    private String name;
    private String provider;
    private String modelKey;
    private String modelRole;   // EVALUATION, GEN_Q ...
    private String params;      // JSON 문자열
    private java.time.LocalDateTime createdAt;
}

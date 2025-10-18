package practical.llm.question.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    private Long questionId;
    private Long userId;
    private Long documentId;
    private String questionText;
    private String lang;             // e.g., "ko", "en"
    private String genPromptText;    // nullable
    private String genPromptParams;  // JSON string (nullable)
    private LocalDateTime createdAt;
}

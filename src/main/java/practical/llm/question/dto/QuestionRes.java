package practical.llm.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionRes {
    private Object questionText; // JSON or String
}
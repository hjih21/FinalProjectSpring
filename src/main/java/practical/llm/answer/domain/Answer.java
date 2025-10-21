package practical.llm.answer.domain;

import lombok.Data;

@Data
public class Answer {
    private Long answerId;
    private Long questionId;
    private String answerText;
}

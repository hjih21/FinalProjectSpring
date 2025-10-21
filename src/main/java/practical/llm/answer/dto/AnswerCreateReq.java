package practical.llm.answer.dto;

public record AnswerCreateReq(
        Long questionId,
        String answerText
) {
}

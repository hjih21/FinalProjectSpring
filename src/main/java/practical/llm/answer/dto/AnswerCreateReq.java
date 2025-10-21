package practical.llm.answer.dto;

public record AnswerCreateReq(
        Long documentId,
        String answerText
) {
}

package practical.llm.question.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WorkerQuestionCreateRequest {

    private Long documentId;
    private String modelName;      // 기록용(선택)
    private String lang;           // "ko"/"en" 등
    private String genPromptText;  // 선택
    private Map<String, Object> genPromptParams;// Map/JsonNode → Jackson이 문자열로 직렬화 가능
    private List<QuestionItem> questions;// 생성된 질문 텍스트 배열
    private String token;

    @Data
    public static class QuestionItem {
        private String text;
        private String type;
        private Map<String, Object> meta;
    }
}

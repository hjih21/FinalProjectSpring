package practical.llm.question.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import practical.llm.file.domain.DocumentFile;
import practical.llm.file.mapper.DocumentMapper;
import practical.llm.question.domain.Question;
import practical.llm.question.dto.WorkerQuestionCreateRequest;
import practical.llm.question.mapper.QuestionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;
    private final DocumentMapper documentMapper;
    private final ObjectMapper objectMapper = new ObjectMapper(); //ObjectMapper(): 자바 객체 <-> JSON 문자열 간의 변환 담당

    @Value("${worker.token}")
    private String workerToken;
    // ==============
// 바뀐 부분
// ==============
    public void saveFromWorker(WorkerQuestionCreateRequest request) {
        if (request.getToken() == null || !request.getToken().equals(workerToken)) {
            throw new SecurityException("invalid worker token");
        }

        DocumentFile doc = documentMapper.findById(request.getDocumentId());
        if (doc == null) throw new IllegalArgumentException("document not found");
        Long userId = doc.getUserId();

        String paramsJson = null;
        if (request.getGenPromptParams() != null) {
            try {
                paramsJson = objectMapper.writeValueAsString(request.getGenPromptParams());
            } catch (Exception ignored) {}
        }

        // 핵심: questions 전체를 JSON 문자열로 직렬화해서 한 행에 저장
        String questionsJson;
        try {
            questionsJson = objectMapper.writeValueAsString(request.getQuestions()); // ← '[{"Q1":".."},{"Q2":".."}]'
        } catch (Exception e) {
            throw new IllegalArgumentException("questions serialize error", e);
        }

        // tb_question 한 행에 JSON blob 저장
        Question row = Question.builder()
                .userId(userId)
                .documentId(request.getDocumentId())
                .questionText(questionsJson)       // ← JSON 배열 그대로
                .lang(request.getLang())
                .genPromptText(request.getGenPromptText())
                .genPromptParams(paramsJson)
                .build();

        // 단일 insert (insertBatch 대신)
        questionMapper.insertOne(row);
    }

    /**
     * 문서 소유자 검증 후 해당 문서의 질문 목록을 반환
     * @param requestUserId 세션에서 꺼낸 현재 사용자
     * @param documentId 조히할 문서 ID*/
    public List<Question> getByDocumentId(Long requestUserId, Long documentId) {
        // 소유자 검증
        DocumentFile doc = documentMapper.findById(documentId);
        if (doc == null) throw new IllegalArgumentException("document not found");
        // 요청 사용자와 문서 소유자가 다르면 접근 불가
        if (!doc.getUserId().equals(requestUserId)){
            throw new SecurityException("유저 정보 불일치");
        }
        return questionMapper.findByDocumentId(documentId);
    }
    // 현자 사용자의 모든 질문 목록 반환(최신순은 Mapper에서 정의)
    public List<Question> getByUserId(Long userId){
        return questionMapper.findByUserId(userId);
    }

    public List<String> getFlatQuestions(Long requestUserId, Long documentId) {
        // 1) 문서 소유자 검증
        var doc = documentMapper.findById(documentId);
        if (doc == null) throw new IllegalArgumentException("document not found");
        if (!doc.getUserId().equals(requestUserId)) {
            throw new SecurityException("유저 정보 불일치");
        }

        // 2) 문서당 1행 구조에서 질문 JSON 조회
        var row = questionMapper.findOneByUserAndDocument(requestUserId, documentId);
        if (row == null || row.getQuestionText() == null) return List.of();

        // 3) [{"Q1":"..."},{"Q2":"..."}] → ["...","..."]
        try {
            List<Map<String, String>> kvs =
                    new ObjectMapper().readValue(
                            row.getQuestionText(),
                            new TypeReference<List<Map<String, String>>>() {}
                    );

            List<String> flat = new ArrayList<>();
            for (Map<String, String> kv : kvs) {
                flat.add(kv.values().stream().findFirst().orElse(""));
            }
            return flat;
        } catch (Exception e) {
            throw new IllegalStateException("질문 JSON 파싱 실패", e);
        }
    }
    }




package practical.llm.question.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;
    private final DocumentMapper documentMapper;
    private final ObjectMapper objectMapper = new ObjectMapper(); //ObjectMapper(): 자바 객체 <-> JSON 문자열 간의 변환 담당

    @Value("${worker.token}")
    private String workerToken;

    public void saveFromWorker(WorkerQuestionCreateRequest request) {
        // 토큰이 없거나 토큰의 지정된 토크 이름이 아니면 예외처리
        if (request.getToken() == null || !request.getToken().equals(workerToken)) {
            throw new SecurityException("invalid worker token");
        }

        // 문서 Id를 통해 userId 추출
        DocumentFile doc = documentMapper.findById(request.getDocumentId());
        if (doc == null) throw new IllegalArgumentException("document not found");
        Long userId = doc.getUserId();

        String paramsJson = null;
        if (request.getGenPromptParams() != null) {
            try {
                paramsJson = objectMapper.writeValueAsString(request.getGenPromptParams());
            } catch (Exception ignored) {
            }
        }

        // 질문 객체(JSON) 배열 처리
        List<Question> batch = new ArrayList<>();
        if (request.getQuestions() != null) {
            // ==============
// 바뀐 부분
// ==============
            for (WorkerQuestionCreateRequest.QuestionItem item : request.getQuestions()) {
                if (item == null || item.getText() == null || item.getText().isBlank()) continue;

                String qJson;
                try {
                    qJson = objectMapper.writeValueAsString(item.getText()); // ← "\"질문문자열\"" 형태
                } catch (Exception e) {
                    // 직렬화 실패 시 그냥 스킵하거나 평문 사용
                    qJson = "\"" + item.getText().replace("\"", "\\\"") + "\"";
                }

                batch.add(Question.builder()
                        .userId(userId)
                        .documentId(request.getDocumentId())
                        .questionText(qJson)          // ← JSON 문자열로 저장
                        .lang(request.getLang())
                        .genPromptText(request.getGenPromptText())
                        .genPromptParams(paramsJson)
                        .build());
            }
// ==============
        }
        if (!batch.isEmpty()) questionMapper.insertBatch(batch);
    }

    public List<Question> getByDocumentId(Long requestUserId, Long documentId) {
        // 소유자 검증
        DocumentFile doc = documentMapper.findById(documentId);
        if (doc == null) throw new IllegalArgumentException("document not found");
        if (!doc.getUserId().equals(requestUserId)){
            throw new SecurityException("유저 정보 불일치");
        }
        return questionMapper.findByDocumentId(documentId);
    }

    public List<Question> getByUserId(Long userId){
        return questionMapper.findByUserId(userId);
    }
    }




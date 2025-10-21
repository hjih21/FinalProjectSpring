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
    /**
     * 워커 콜백을 받아 질문을 DB에 저장
     * 1) 보안 토큰 검사
     * 2) documentId로 userId 조회(문서 소유자 식별)
     * 3) genPromptParams를 JSON 문자열로 직렬화
     * 4) 질문 리스트를 순회하며 question_text를 JSON 문자열("...")로 직렬화해 tb_question에 배치 INSERT
     * */
    public void saveFromWorker(WorkerQuestionCreateRequest request) {
        // 1) 워커 인증: 환경설정에 등록된 토큰과 일치하는지 확인
        if (request.getToken() == null || !request.getToken().equals(workerToken)) {
            throw new SecurityException("invalid worker token");
        }

        // 2) 문사 -> 사용자 매핑: documentId로 문서 소유자(userId) 조회
        DocumentFile doc = documentMapper.findById(request.getDocumentId());
        if (doc == null) throw new IllegalArgumentException("document not found");
        Long userId = doc.getUserId();

        // 3) genPromptParams(Map) -> JSON 문자열로 직렬화 (NULL 허용)
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
            // 4) 각 질문을 순회하며 DB 레코드로 변환
            for (WorkerQuestionCreateRequest.QuestionItem item : request.getQuestions()) {
                if (item == null || item.getText() == null || item.getText().isBlank()) continue;

                String qJson;
                try {
                    // question_text 컬럼이 JSON 타입이므로, 평문이 아닌 JSON 문자열("...")로 저장한다.
                    qJson = objectMapper.writeValueAsString(item.getText()); // ← "\"질문문자열\"" 형태
                } catch (Exception e) {
                    // 직렬화 실패 시 안전하게 escape 하여 JSON 문자열 형태 유지
                    qJson = "\"" + item.getText().replace("\"", "\\\"") + "\"";
                }
                // Question 도메인 객체에 직렬화된 JSON 문자열을 세팅 (questionText)
                batch.add(Question.builder()
                        .userId(userId)
                        .documentId(request.getDocumentId())
                        .questionText(qJson)          // ← JSON 문자열로 저장
                        .lang(request.getLang())
                        .genPromptText(request.getGenPromptText())
                        .genPromptParams(paramsJson)
                        .build());
            }
        }
        // 5) 한 번에 배치 INSERT 수행 (묶음 저장)
        if (!batch.isEmpty()) questionMapper.insertBatch(batch);
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
    }




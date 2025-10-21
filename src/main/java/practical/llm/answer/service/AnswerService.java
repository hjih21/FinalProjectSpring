package practical.llm.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practical.llm.answer.domain.Answer;
import practical.llm.answer.dto.AnswerCreateReq;
import practical.llm.answer.mapper.AnswerMapper;

// ⬇️ 추가
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerMapper answerMapper;

    @Transactional
    public Long save(AnswerCreateReq req) {
        Answer a = new Answer();

        // ✅ documentId로 최신 question_id 해석
        Long questionId = answerMapper.findLatestQuestionIdByDocumentId(req.documentId());
        if (questionId == null) {
            throw new IllegalStateException("documentId=" + req.documentId() + " 에 대한 질문을 찾을 수 없습니다.");
        }
        a.setQuestionId(questionId);

        // 여러 줄을 [{"A1":"..."},...] JSON 문자열로 변환 (기존 그대로)
        a.setAnswerText(toAnswerJsonArray(req.answerText()));

        answerMapper.insertAnswer(a);
        return a.getAnswerId();
    }

    /**
     * 사용자가 입력한 여러 줄 텍스트를
     *  [{"A1":"line1"},{"A2":"line2"}, ...] 형태의 JSON 문자열로 변환한다.
     */
    private String toAnswerJsonArray(String raw) {
        try {
            if (raw == null) return "[]";
            String normalized = raw.replace("\r\n", "\n");
            String[] lines = normalized.split("\n");

            List<Map<String, String>> list = new ArrayList<>();
            int idx = 1;
            for (String line : lines) {
                String t = line == null ? "" : line.trim();
                if (t.isEmpty()) continue;           // 빈 줄은 스킵
                Map<String, String> obj = new LinkedHashMap<>();
                obj.put("A" + idx, t);
                list.add(obj);
                idx++;
            }
            return new ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            // 혹시 모를 직렬화 실패 대비: 원문을 안전하게 감싸서 저장
            try {
                ObjectMapper om = new ObjectMapper();
                return "[{\"A1\":" + om.valueToTree(raw).toString() + "}]";
            } catch (Exception ignore) {
                return "[{\"A1\":\"" + raw.replace("\"","\\\"") + "\"}]";
            }
        }
    }
}
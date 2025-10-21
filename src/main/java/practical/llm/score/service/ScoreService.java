package practical.llm.score.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practical.llm.question.mapper.QuestionMapper; // ★ 기존 매퍼 재사용
import practical.llm.score.domain.Score;
import practical.llm.score.dto.ScoreCreateReq;
import practical.llm.score.dto.ScoreInsertParam;
import practical.llm.score.mapper.ScoreMapper;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreMapper scoreMapper;
    private final QuestionMapper questionMapper; // documentId→최신 question_id
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Integer createScore(ScoreCreateReq req) {
        // 1) documentId -> 최신 question_id 조회
        Integer questionId = questionMapper.selectLatestQuestionIdByDocumentId(req.getDocumentId());
        if (questionId == null) {
            throw new IllegalStateException("No question for documentId=" + req.getDocumentId());
        }

        // 2) JSON 직렬화 (null 안전)
        String paramsJson = toJson(req.getGraderPromptParams());
        String respRawJson = toJson(req.getGraderResponseRaw());
        String metricsJson = toJson(req.getMetricsJson());

        // 3) Mapper 파라미터 DTO (문자열로만 전달)
        ScoreInsertParam p = new ScoreInsertParam();
        p.setQuestionId(questionId);
        p.setEvalModelId(req.getEvalModelId());
        p.setAnswerId(req.getAnswerId());
        p.setGraderPromptText(req.getGraderPromptText());
        p.setGraderPromptParamsJson(paramsJson);
        p.setGraderResponseRawJson(respRawJson);
        p.setMetricsJson(metricsJson);
        p.setOverallScore(req.getOverallScore());

        scoreMapper.insertScore(p);
        return p.getScoreId();
    }

    private String toJson(Object o) {
        if (o == null) return null;
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { throw new RuntimeException("JSON serialize fail", e); }
    }
}
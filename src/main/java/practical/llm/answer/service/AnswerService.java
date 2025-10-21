package practical.llm.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practical.llm.answer.domain.Answer;
import practical.llm.answer.dto.AnswerCreateReq;
import practical.llm.answer.mapper.AnswerMapper;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerMapper answerMapper;

    @Transactional
    public Long save(AnswerCreateReq req) {
        Answer a = new Answer();
        a.setQuestionId(req.questionId());
        a.setAnswerText(req.answerText());

        answerMapper.insertAnswer(a);         // a.answerId 채워짐
        return a.getAnswerId();
    }
}

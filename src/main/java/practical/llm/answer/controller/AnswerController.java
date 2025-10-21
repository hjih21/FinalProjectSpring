package practical.llm.answer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practical.llm.answer.dto.AnswerCreateReq;
import practical.llm.answer.dto.AnswerCreateRes;
import practical.llm.answer.service.AnswerService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/answers")
@Slf4j
public class AnswerController {
    private final AnswerService  answerService;

    // practical/llm/answer/controller/AnswerController.java
    /**
     * 프론트 → 스프링 바디(JSON):
     * {
     *   "documentId": number,       // 이 문서의 "최신 질문" 기준 저장
     *   "answerText": "사용자 답변"
     * }
     * 저장: tb_answer.question_id = (tb_question 에서 documentId로 최신 question_id)
     * 응답: { "answerId": 123 }
     */
    @PostMapping
    public ResponseEntity<AnswerCreateRes> create(@RequestBody AnswerCreateReq req) {
        log.info("엔서 저장 시작: documentId={}, len={}",
                req.documentId(), req.answerText()==null?0:req.answerText().length());
        Long answerId = answerService.save(req);
        return ResponseEntity.ok(new AnswerCreateRes(answerId));
    }
}

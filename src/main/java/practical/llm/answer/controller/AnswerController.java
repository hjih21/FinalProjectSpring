package practical.llm.answer.controller;

import lombok.RequiredArgsConstructor;
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
public class AnswerController {
    private final AnswerService  answerService;

    /**
     * 프론트에서 오는 바디(JSON):
     * {
     *   "questionId": number,          // 필수
     *   "answerText": "사용자 답변"     // 필수
     * }
     *
     * 저장되는 DB 행:
     *  - question_id = questionId
     *  - answer_text = {"text": "<answerText>"} (JSON)
     *
     * 응답:
     *  { "answerId": 123 }
     */
    @PostMapping
    public ResponseEntity<AnswerCreateRes> create(@RequestBody AnswerCreateReq req) {
        Long answerId = answerService.save(req);
        return ResponseEntity.ok(new AnswerCreateRes(answerId));
    }
}

package practical.llm.score.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practical.llm.answer.dto.AnswerRes;
import practical.llm.answer.service.AnswerService;
import practical.llm.question.dto.QuestionRes;
import practical.llm.question.service.QuestionService;
import practical.llm.score.dto.ScoreCreateReq;
import practical.llm.score.dto.ScoreCreateRes;
import practical.llm.score.service.ScoreService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping("/scores")
    public ResponseEntity<?> create(@RequestBody ScoreCreateReq req) {
        Integer id = scoreService.createScore(req);
        return ResponseEntity.ok(new ScoreCreateRes(id));
    }

    @GetMapping("/questions/by-document/{documentId}")
    public ResponseEntity<?> getByDocument(@PathVariable int documentId) {
        Object qText = questionService.getLatestQuestionTextByDocument(documentId);
        if (qText == null) {
            return ResponseEntity.status(404).body("Question not found for documentId=" + documentId);
        }
        return ResponseEntity.ok(new QuestionRes(qText));
    }

    @GetMapping("/answers/{answerId}")
    public ResponseEntity<?> getById(@PathVariable int answerId) {
        Object aText = answerService.getAnswerTextById(answerId);
        if (aText == null) {
            return ResponseEntity.status(404).body("Answer not found: " + answerId);
        }
        return ResponseEntity.ok(new AnswerRes(aText));
    }

}
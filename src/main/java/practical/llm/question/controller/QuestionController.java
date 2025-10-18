package practical.llm.question.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import practical.llm.question.domain.Question;
import practical.llm.question.dto.WorkerQuestionCreateRequest;
import practical.llm.question.service.QuestionService;
import practical.llm.user.web.LoginCheckInterceptor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    //파이썬 워커 콜백
    @PostMapping("/worker")
    public void createFromWorker(@RequestBody WorkerQuestionCreateRequest req) {
        questionService.saveFromWorker(req);
    }

    //로그인한 사용자 본인만 등록
    @GetMapping
    public List<Question> byDocumentId(@RequestParam Long documentId, HttpServletRequest request){
        Long userId = (Long) request.getSession(false).getAttribute(LoginCheckInterceptor.LOGIN_USER);
        return questionService.getByDocumentId(userId, documentId);
    }

    //내 모든 질문
    @GetMapping("/mine")
    public List<Question> mine(HttpServletRequest request){
        Long userId = (Long) request.getSession(false).getAttribute(LoginCheckInterceptor.LOGIN_USER);
        return questionService.getByUserId(userId);
    }



}

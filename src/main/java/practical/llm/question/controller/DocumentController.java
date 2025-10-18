package practical.llm.question.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import practical.llm.file.mapper.DocumentMapper;
import practical.llm.question.service.QuestionService;
import practical.llm.user.web.LoginCheckInterceptor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
@Slf4j
public class DocumentController {
    private final DocumentMapper documentMapper;
    private final QuestionService questionService;

    //생성된 질문 다음 페이지에 보여주기
    @GetMapping("/latest")
    public Map<String, Object> latest(HttpServletRequest request) {
        log.info("생성된 질문 가져오기 시작");
        Long userId = (Long) request.getSession(false).getAttribute(LoginCheckInterceptor.LOGIN_USER);
        Long docId = documentMapper.findLatestIdByUserId(userId);
        log.info("생성된 질문 정보 배출");
        return Map.of("documentId", docId);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadByDocument(
            @RequestParam Long documentId,
            HttpServletRequest request){

        Long userId = (Long) request.getSession(false)
                .getAttribute(LoginCheckInterceptor.LOGIN_USER);
        var list = questionService.getByDocumentId(userId, documentId);
        // 질문 텍스트 생성
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++){
            var q = list.get(i);
            sb.append("Q").append(i+1).append(":")
                    .append(q.getQuestionText() != null ? q.getQuestionText() : "")
                    .append("\n");
        }
        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // 다운받은 파일 이름은 여기 있음
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("questions_doc_" + documentId + ".txt",
                                java.nio.charset.StandardCharsets.UTF_8)
                        .build()
        );
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}

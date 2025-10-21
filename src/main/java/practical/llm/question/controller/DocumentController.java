package practical.llm.question.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            HttpServletRequest request) {

        Long userId = (Long) request.getSession(false)
                .getAttribute(LoginCheckInterceptor.LOGIN_USER);

        var list = questionService.getByDocumentId(userId, documentId);

        String bodyJson;
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (list == null || list.isEmpty()) {
                // 데이터가 없으면 빈 배열 JSON
                bodyJson = "[]";
            } else if (list.size() == 1 && isJson(list.get(0).getQuestionText())) {
                // 단일 행이고 question_text 자체가 JSON이면 그대로 사용
                bodyJson = list.get(0).getQuestionText();
            } else {
                // 여러 행이거나 JSON이 아니라면 문자열 배열로 직렬화
                List<String> texts = list.stream()
                        .map(q -> q.getQuestionText())
                        .filter(Objects::nonNull)
                        .toList();
                bodyJson = mapper.writeValueAsString(texts);
            }
        } catch (Exception e) {
            // 직렬화 실패 시 안전하게 빈 배열 반환
            bodyJson = "[]";
        }

        byte[] bytes = bodyJson.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("questions_doc_" + documentId + ".json", StandardCharsets.UTF_8)
                        .build()
        );
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // ============== 추가: JSON 여부 체크 유틸 ==============
    private boolean isJson(String s) {
        if (s == null || s.isBlank()) return false;
        try {
            new ObjectMapper().readTree(s);
            return true; // 파싱되면 JSON 맞음
        } catch (Exception e) {
            return false;
        }
    }
// =======================================================

}

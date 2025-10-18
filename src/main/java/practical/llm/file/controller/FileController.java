package practical.llm.file.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import practical.llm.file.domain.DocumentFile;
import practical.llm.file.dto.FileUploadResponse;
import practical.llm.file.service.FileService;
import practical.llm.user.web.LoginCheckInterceptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "meta", required = false) String metaJson,
            HttpServletRequest request
    ) throws IOException {
        //세션에서 userId 추출
        log.info("업로드 시작");
        Long userId = (Long) request.getSession(false).getAttribute(LoginCheckInterceptor.LOGIN_USER);
        return fileService.upload(userId, title, metaJson, file);
    }

    // 내 파일 목록
    @GetMapping
    public List<DocumentFile> myFiles(HttpServletRequest request) {
        Long userId = (Long) request.getSession(false).getAttribute(LoginCheckInterceptor.LOGIN_USER);
        return fileService.listByUserId(userId);
    }

    //단건 조회
    @GetMapping("/{documentId}")
    public DocumentFile get(@PathVariable Long documentId){
        return fileService.get(documentId);
    }

    //다운로드
    @GetMapping("/{documentId}/download")//세션에 로그인 정보 있으니까 수정해야할듯
    public ResponseEntity<FileSystemResource> download(@PathVariable Long documentId){
        DocumentFile doc = fileService.get(documentId);
        if (doc == null) return ResponseEntity.notFound().build();

        Path path = fileService.resolvePath(doc);
        File file = path.toFile();
        if (!file.exists()) return ResponseEntity.notFound().build();

        FileSystemResource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(doc.getDocumentTitle(), java.nio.charset.StandardCharsets.UTF_8)
                        .build()
        );
        headers.setContentType(MediaType.parseMediaType(doc.getMimeType() != null ? doc.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE));
        headers.setContentLength(doc.getSizeBytes());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

}

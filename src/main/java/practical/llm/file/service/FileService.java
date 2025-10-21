package practical.llm.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import practical.llm.file.domain.DocumentFile;
import practical.llm.file.dto.FileUploadResponse;
import practical.llm.file.mapper.DocumentMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * 파일 저장/메타 생성/DB 기록을 담당하는 서비스
 * - 물리 파일을 로컬 디스크에 저장
 * - 해시/용량/MIME 등 메타 생성
 * - tb_document에 UPSET
 * */
@Service
@RequiredArgsConstructor
public class FileService {

    private final DocumentMapper documentMapper;

    @Value("${file.dir}")
    private String baseDir;

    // 1) 유효성 검사
    public FileUploadResponse upload(Long userId, String title, String metaJson, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다");
        }
        // 2) 저장 결로 준비: baseDir/userId/ 에 UUID 파일명으로 저장
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = getExtension(originalName);


        Path userDir = Paths.get(baseDir, String.valueOf(userId));
        Files.createDirectories(userDir);

        String fileName = UUID.randomUUID().toString() + "." + ext;
        Path fullPath = userDir.resolve(fileName);

        // 3) 실제 파일 저장 (임시 업로드 스트림 -> 최종 결로로 복사)
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, fullPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 4) 메타 생성: 파일 크기, MIME, SHA-256 해시
        long size = Files.size(fullPath);
        String mime = Files.probeContentType(fullPath);
        String sha256 = sha256Hex(fullPath);

        // 5) DB 기록을 위한 도메인 객체 구성
        DocumentFile doc = DocumentFile.builder()
                .userId(userId)
                .documentTitle(title != null && !title.isBlank() ? title : originalName)
                .mimeType(mime != null ? mime : file.getContentType())
                .storageUri(fullPath.toString())
                .sizeBytes(size)
                .sha256(sha256)
                .status("READY")
                .meta(metaJson)
                .build();

        // 6) DB UPSERT를 실행 (중복 sha256이면 기존 행 갱신)
        documentMapper.insert(doc);

        // 7) 컨트롤러 응답 DTO 반환
        return new FileUploadResponse(
                doc.getDocumentId(),
                doc.getDocumentTitle(),
                doc.getMimeType(),
                doc.getSizeBytes(),
                doc.getStatus()
        );



    }

    public DocumentFile get(Long documentId) {
        return documentMapper.findById(documentId);
    }

    public List<DocumentFile> listByUserId(Long userId) {
        return documentMapper.findByUserId(userId);
    }

    // 파일 경로 반환 (다운로드용)
    public Path resolvePath(DocumentFile doc){
        // 저장된 storageUri(절대/성대 경로)를 그대로 Path로 변환
        return Paths.get(doc.getStorageUri());
    }

    public int updateStatus(Long documentId, String status) {
        return documentMapper.updateStatus(documentId, status);
    }
// ========================================================================================================
    // 파일명에서 확장자만 추출
    private static String getExtension(String originalName) {
        if (originalName == null) return "";
        int i = originalName.lastIndexOf('.');
        return (i == -1) ? "" : originalName.substring(i + 1);
    }

    // 파일 내용을 스트리밍하여 SHA-256 해시를 계산하여 Hex 문자열로 반환
    private static String sha256Hex(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)){
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            in.transferTo(new java.security.DigestOutputStream(OutputStream.nullOutputStream(), md));
            return HexFormat.of().formatHex(md.digest());
        } catch (IOException e) {
            throw new IOException("SHA-256 계산 실패", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 지원하지 않습니다", e);
        }
    }


}

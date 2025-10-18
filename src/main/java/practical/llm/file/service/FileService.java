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

@Service
@RequiredArgsConstructor
public class FileService {

    private final DocumentMapper documentMapper;

    @Value("${file.dir}")
    private String baseDir;

    public FileUploadResponse upload(Long userId, String title, String metaJson, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다");
        }
        // 저장 경로: baseDir/userId/yyyy/MM/dd/UUID.확장자
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = getExtension(originalName);


        Path userDir = Paths.get(baseDir, String.valueOf(userId));
        Files.createDirectories(userDir);

        String fileName = UUID.randomUUID().toString() + "." + ext;
        Path fullPath = userDir.resolve(fileName); //'/Users/jihyunhong/projectFileTest/123/abcd-uuid.pdf'

        // 로컬 저장소에 저장
        // MultipartFile file은 임시로 파일을 담고 있음
        // getInputStream() -> 담은 파일을 읽기 위한 스트림
        // Files.copy -> 스트림 내용을 복사 in내용을 fullPath경로로 복사
        // StandardCopyOption.REPLACE_EXISTING -> 같은 이름이 있으면 덮어쓰기
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, fullPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 메타정보
        long size = Files.size(fullPath);
        String mime = Files.probeContentType(fullPath);
        String sha256 = sha256Hex(fullPath);

        // DB 레코드 생성
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

        documentMapper.insert(doc);

        // Controller에게 응답 데이터를 반환
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
        return Paths.get(doc.getStorageUri());
    }

    public int updateStatus(Long documentId, String status) {
        return documentMapper.updateStatus(documentId, status);
    }
// ========================================================================================================

    private static String getExtension(String originalName) {
        if (originalName == null) return "";
        int i = originalName.lastIndexOf('.');
        return (i == -1) ? "" : originalName.substring(i + 1);
    }

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

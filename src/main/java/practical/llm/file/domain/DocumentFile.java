package practical.llm.file.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentFile {

    private Long documentId;       // PK (AI)
    private Long userId;           // 업로더(user) ID
    private String documentTitle;  // 문서 제목(원본 파일명 대체 가능)
    private String mimeType;       // MIME 타입 (ex: application/pdf)
    private String storageUri;     // 로컬 저장 경로(절대경로나 상대경로)
    private Long sizeBytes;        // 파일 크기(byte)
    private String sha256;         // 파일 해시(중복 검출용)
    private String status;         // ENUM('READY', ...) 기본 READY 사용
    private String meta;           // JSON 문자열(옵션)
    private LocalDateTime createdAt;
}

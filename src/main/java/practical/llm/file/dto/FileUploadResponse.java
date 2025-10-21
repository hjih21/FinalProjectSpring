package practical.llm.file.dto;

/**
 * 파일 업로드 성공 시 프론트로 전달되는 응답 DTO.
 *
 * @param documentId 생성(또는 갱신)된 문서 ID
 * @param title      문서 제목(원본 파일명 또는 지정명)
 * @param mimeType   MIME 타입
 * @param sizeBytes  파일 크기(byte)
 * @param status     처리 상태(READY/PARSING/DONE/FAILED)
 */
public record FileUploadResponse(
        Long documentId,
        String title,
        String mimeType,
        Long sizeBytes,
        String status
) { }

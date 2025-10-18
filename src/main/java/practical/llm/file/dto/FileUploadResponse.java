package practical.llm.file.dto;

public record FileUploadResponse(
        Long documentId,
        String title,
        String mimeType,
        Long sizeBytes,
        String status
) { }

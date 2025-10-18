package practical.llm.file.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practical.llm.file.domain.DocumentFile;

import java.util.List;

@Mapper
public interface DocumentMapper {

    void insert(DocumentFile doc);
    DocumentFile findById(@Param("documentId") Long documentId);
    List<DocumentFile> findByUserId(@Param("userId") Long userId);
    int updateStatus(@Param("documentId") Long documentId, @Param("status") String status);

    Long findLatestIdByUserId(Long userId);
}

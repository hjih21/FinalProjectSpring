package practical.llm.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practical.llm.question.domain.Question;

import java.util.List;

@Mapper
public interface QuestionMapper {

    void insertBatch(@Param("list") List<Question> questions);
    List<Question> findByDocumentId(@Param("documentId") Long documentId);
    List<Question> findByUserId(@Param("userId") Long userId);
}

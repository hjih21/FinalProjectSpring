package practical.llm.answer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practical.llm.answer.domain.Answer;

@Mapper
public interface AnswerMapper {

    int insertAnswer(@Param("a") Answer answer);
}

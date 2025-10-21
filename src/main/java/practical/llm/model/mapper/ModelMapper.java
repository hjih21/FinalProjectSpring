package practical.llm.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import practical.llm.model.domain.Models;

import java.util.List;

@Mapper
public interface ModelMapper {
    List<Models> findEvaluationModels();
}

package practical.llm.score.mapper;

import org.apache.ibatis.annotations.Mapper;
import practical.llm.score.domain.Score;
import practical.llm.score.dto.ScoreInsertParam;

@Mapper
public interface ScoreMapper {
    int insertScore(ScoreInsertParam param); // useGeneratedKeys=true
}
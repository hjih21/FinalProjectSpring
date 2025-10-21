// src/main/java/practical/llm/question/mapper/QuestionMapper.java
package practical.llm.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practical.llm.question.domain.Question;

import java.util.List;

@Mapper
public interface QuestionMapper {
    /** @Param: XML에서 쓸 이름을 붙여주는 애노테이션
     * 붙여준 이름으로 <mapper>.xml에서 #{...} 또는 ${...}로 참조*/
    // ==============
    // 바뀐 부분
    // ==============

    /** 질문 JSON(문서별 한 건) 저장 */
    void insertOne(Question question);

    /** userId + documentId 로 1건 조회 (JSON 묶음) */
    Question findOneByUserAndDocument(@Param("userId") Long userId,
                                      @Param("documentId") Long documentId);

    // (옵션) 기존 메서드 유지 여부는 선택
    // 문서에 여러 row를 저장하던 과거 로직을 쓰지 않는다면 제거 가능
    List<Question> findByDocumentId(@Param("documentId") Long documentId);
    List<Question> findByUserId(@Param("userId") Long userId);
}
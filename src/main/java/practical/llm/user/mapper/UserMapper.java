package practical.llm.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practical.llm.user.domain.User;

@Mapper
public interface UserMapper {

    // @param user 저장할 사용자 엔티티
    void insert(User user);
    // @param email 사용자 이메일
    // @return User 객체 또는 null
    User findByEmail(@Param("email") String email);
    /**
    * PK(userId)로 사용자 정보 조회
    * @param  userId 사용자 PK
    * @return User 객체 또는 null
    */
    User findById(@Param("userId") Long userId);
    /**
     * 회원 프로필 수정 요청 DTO
     */
    int updateProfile(@Param("userId") Long userId,
                      @Param("userName") String userName,
                      @Param("userBirth") String userBirth);
}

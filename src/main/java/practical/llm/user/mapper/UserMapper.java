package practical.llm.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practical.llm.user.domain.User;

@Mapper
public interface UserMapper {

    void insert(User user);
    User findByEmail(@Param("email") String email);
    User findById(@Param("userId") Long userId);
    int updateProfile(@Param("userId") Long userId,
                      @Param("userName") String userName,
                      @Param("userBirth") String userBirth);
}

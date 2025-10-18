package practical.llm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import practical.llm.user.domain.User;
import practical.llm.user.dto.SignupRequest;
import practical.llm.user.mapper.UserMapper;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    //비밀번호 암호화를 위한 BCrypt 인코더
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /*
    * 회원가입
    */
    public Long signup(SignupRequest req){
        if (userMapper.findByEmail(req.getEmail()) != null){
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .userEmail(req.getEmail())
                .userPw(encoder.encode(req.getPassword()))//해시로 암호화
                .userName(req.getName())
                .userBirth(LocalDate.parse(req.getBirth()))
                .build();

        userMapper.insert(user);
        return user.getUserId();
    }

    /*
    * 로그인
    */
    public User login(String email, String pw){
        User user = userMapper.findByEmail(email);
        if (user == null || !encoder.matches(pw, user.getUserPw())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }
        return user;
    }


    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userMapper.findById(userId));
    }

    public void updateProfile(Long userId, String name, String birth) {
        userMapper.updateProfile(userId, name, birth);
    }

}

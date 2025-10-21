package practical.llm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import practical.llm.user.domain.User;
import practical.llm.user.dto.SignupRequest;
import practical.llm.user.mapper.UserMapper;

import java.time.LocalDate;
import java.util.Optional;

/*
* 사용자 관련 서비스 클래스
* 회원가입, 로그인, 사용자 조회, 프로필 수정 비즈니스 로직
*/
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    //비밀번호 암호화를 위한 BCrypt 인코더
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /*
    * 회원가입 처리, 이미 존재하는 이메일이면 예외 발생
    * @param req 회원가입 요청 정보(email, password, name, birth)
    * @return 생성된 사용자의 userId
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
    * 로그인 처리, 이메일/비밀번호가 일치하지 않으면 예외 발생
    * @param eamil 사용자 이메일
    * @param pw 평문 비밀번호
    * @return 인증된 User 객체
    */
    public User login(String email, String pw){
        User user = userMapper.findByEmail(email);
        if (user == null || !encoder.matches(pw, user.getUserPw())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }
        return user;
    }

    /*
    * 사용자 ID로 사용자 정보 조회
    * @param userId 사용자 PK
    * @return User(Optional), 존재하지 않으면 Optional.empty()
    */
    // Optional: 값이 있을 수도 있고 없을 수도 있는 객체
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userMapper.findById(userId));
    }


    /*
    * 사용자 프로필(이름/생일) 수정
    * @param userId 사용자 PK
    * @param name 새로운 이름
    * @param birth 새로운 생일(yyyy-MM-dd)
    */
    public void updateProfile(Long userId, String name, String birth) {
        userMapper.updateProfile(userId, name, birth);
    }

}

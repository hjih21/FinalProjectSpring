package practical.llm.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practical.llm.user.domain.User;
import practical.llm.user.dto.LoginRequest;
import practical.llm.user.dto.SignupRequest;
import practical.llm.user.service.UserService;
import practical.llm.user.web.LoginCheckInterceptor;

/*
* 인증 관련 컨트롤러
* - 회원가입 / 로그인 / 로그아웃
*/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<?> check(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object uid = (session == null) ? null : session.getAttribute(LoginCheckInterceptor.LOGIN_USER);
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // 필요시 사용자 정보 JSON으로 반환해도 됨
        return ResponseEntity.ok().body(uid);
    }
    //회원가입
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest signupRequest){
        userService.signup(signupRequest);
        return "회원가입 성공";
    }


    //로그인
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        //세션 생성 및 userId 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginCheckInterceptor.LOGIN_USER, user.getUserId());
        return user; //프론트로 사용자 정보 반환
    }

    //로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); //세션 삭제
        }
        return "로그아웃 완료";
    }
}

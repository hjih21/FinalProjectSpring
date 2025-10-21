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
* 인증/세션 관련 컨트롤러
* - 세션 기반 로그인/로그아웃, 회원가입, 로그인 상태 확인
* - 세션에 userId를 저장하여 인증 상태를 관리함
*/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    /**
     * 로그인 상태 확인 엔드포인트
     * 세션에 로그인 정보가 있으면 userId 반환, 없으면 401 Unauthorized
     * @param request HttpServletRequest (세션 접근용)
     * @return userId 또는 401
     * */
    @GetMapping("/check")
    public ResponseEntity<?> check(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object uid = (session == null) ? null : session.getAttribute(LoginCheckInterceptor.LOGIN_USER);
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // 필요시 사용자 정보 JSON으로 반환해도 됨
        return ResponseEntity.ok().body(uid);
    }
    /**
     * 회원가입 엔드포인트
     * 요청 body에 SignupRequest(email. password, name, birth)를 받아 회원가입 처리
     * @param signupRequest 회원가입 정보
     * @return 성공 메시지
     */
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest signupRequest){
        userService.signup(signupRequest);
        return "회원가입 성공";
    }


    /**
     * 로그인 엔드포인트
     * 요청 body에 LoginRequest(email, password)를 받아 인증
     * 인증 성공 시 세션에 userId 저장, User 정보를 반환
     * @param loginRequest 로그인 정보
     * @param request HttpServletRequest (세션 생성용)
     * @return User 정보(JSON)
     * */
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        //세션 생성 및 userId 저장 (allowCredentials=true로 클라이언트에 쿠키 전달)
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginCheckInterceptor.LOGIN_USER, user.getUserId());
        return user; //프론트로 사용자 정보 반환
    }

    /**
     * 로그아웃 엔드포인트
     * 세션이 있으면 삭제
     * @param request HttpServletRequest (세션 접근용)
     * @return 성공 메시지
     * */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); //세션 삭제
        }
        return "로그아웃 완료";
    }
}

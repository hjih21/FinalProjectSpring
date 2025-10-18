package practical.llm.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practical.llm.user.dto.UpdateProfileRequest;
import practical.llm.user.service.UserService;
import practical.llm.user.web.LoginCheckInterceptor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    public String updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return "로그인이 필요합니다.";

        Long userId = (Long) session.getAttribute(LoginCheckInterceptor.LOGIN_USER);
        userService.updateProfile(userId, updateProfileRequest.getName(), updateProfileRequest.getBirth());
        return "회원정보 수정 완료";
    }
}

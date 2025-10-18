package practical.llm.user.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    //세션에 저장할 속성명
    public static final String LOGIN_USER = "LOGIN_USER";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        //세션이 없거나 로그인 사용자 정보가 없으면 401
        //삼항 연산자 조건?A:B true -> A false -> B
        Object loginUser = request.getSession(false) == null ? null : request.getSession(false).getAttribute(LOGIN_USER);
        if (loginUser == null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

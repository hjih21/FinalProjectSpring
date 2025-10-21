package practical.llm.user.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/*
* 로그인 세션 체크 인터셉터
* 모든 요청에 대해 세선에 로그인 정보가 있는지 검사
* - 없을 경우 401
* - OPTIONS 요청 (CORS preflight)은 무조건 통과
*/
public class LoginCheckInterceptor implements HandlerInterceptor {

    //세션에 저장할 속성명
    public static final String LOGIN_USER = "LOGIN_USER";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS preflight 요청(OPTIONS)은 인증 필요 없이 무조건 허용
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        //세션이 없거나 로그인 사용자 정보가 없으면 401 Unauthorized 반환
        //삼항 연산자 조건?A:B true -> A false -> B
        Object loginUser = request.getSession(false) == null ? null : request.getSession(false).getAttribute(LOGIN_USER);
        if (loginUser == null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

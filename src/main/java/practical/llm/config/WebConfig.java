package practical.llm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import practical.llm.user.web.LoginCheckInterceptor;

/**
 * 웹 설정 파일
 * - CORS 설정: React 프론트엔드와의 통신은 허용
 *  - allowCredentials(true): 쿠키/세션 등 인증정보를 클라이언트에 전달하기 위해 필요
 *  - allowedOrigins: 프론트엔드 개발 서버 주소만 허용
 *  - exposedHeaders: 파일 다운로드 등에서 Content-Disposition 헤더 노출 허용
 * -로그인 인터셉터 등록: 인증이 필요한 경로에 세션 체크 인터셉터를 적용
 *  - excludePathPatterns: 로그인/회원가입 등 인증이 필요 없는 경로는 인터셉터 적용 제외
*/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // React 개발 서버 주소
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true) // 쿠키/세션 허용
                .maxAge(3600);
    }

    /**
     * 로그인 체크 인터셉터 등록
     * - addPathPatterns: 인증이 필요한 경로 지정(예: /files/**)
     * - excludePathPatterns: 인증이 필요없는 경로(로그인, 회원가입 등)는 인터셉터에서 제외
     * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/files/**") // 인터셉터가 적용될 경로
                .excludePathPatterns("/auth/**"); // 로그인/회원가입 제외
    }
}

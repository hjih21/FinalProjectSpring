package practical.llm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import practical.llm.user.web.LoginCheckInterceptor;

/*
* 웹 설정 파일
* - CORS 설정 (React 프론트엔드와 통신 허용)
* - 로그인 인터셉터 등록
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

    //인터셉터 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/files/**") // 인터셉터가 적용될 경로
                .excludePathPatterns("/auth/**"); // 로그인/회원가입 제외
    }
}

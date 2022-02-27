package com.cos.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	//application.yml에있는 viewResolver인 mustache 설정을 재설정 할 수 있음
	public void configureViewResolvers(ViewResolverRegistry registry) {
		MustacheViewResolver resolver = new MustacheViewResolver();
		
		resolver.setCharset("UTF-8"); //인코딩
		resolver.setContentType("text/html; charset=UTF-8"); //데이터는 utf8의 html파일
		resolver.setPrefix("classpath:/templates/"); //classpath:은 내 프로젝트라고 보면됨 접두사 설정
		resolver.setSuffix(".html"); // 접미사 설정
		
		registry.viewResolver(resolver); //매개변수 registry로 view resolver 등록
	}
}

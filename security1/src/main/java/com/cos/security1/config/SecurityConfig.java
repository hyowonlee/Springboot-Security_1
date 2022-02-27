package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨 즉 이 클래스가 앞단에서 필터링해주는 설정파일이 되는거
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) 
// securedEnabled은 secured 어노테이션 활성화 컨트롤러에 @Secured("ROLE_ADMIN")이런 어노테이션을 걸어주면 접근을 제한함 즉 특정메서드에 간단하게 걸고싶을때 사용
// prePostEnabled 이건 preAuthorize과 postAuthorize 어노테이션 활성화 (함수 전, 후에 실행) 컨트롤러에 @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") 이런 어노테이션을 걸어주면 접근을 제한함 여러개의 접근제한을 할 수 있음
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean //메서드에 bean 어노테이션 붙이면 해당 메서드의 리턴되는 오브젝를 ioc로 등록
	public BCryptPasswordEncoder encodePwd() { //password 해쉬화 객체 생성 함수
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //이 url은 인증이 필요, 로그인 한사람만 올수있음
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // admin, manager권한을 가진사람이 접근가능
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // admin권한을 가진사람이 접근가능
			.anyRequest().permitAll() // 위의 url말고 다른 url들은 전부 접근 허용
			.and()
			.formLogin() // form 로그인 인증 기능이 작동함
			.loginPage("/loginForm") //사용자 정의 로그인 페이지
			.loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 로그인 진행 즉 로그인 뷰의 <form>을 여기로 보내주면 되는거
			.defaultSuccessUrl("/") // 로그인 성공시 /로 이동
			.and()
			.oauth2Login() // oauth로그인 사용, 이건 maven dependency 연결되어있어야함
			.loginPage("/loginForm") // oauth로그인 페이지를 설정하는건데 우린 기존 로그인 페이지에 oauth 버튼이 있으니 기존 로그인페이지와 같게 설정 그리고 애초에 인증필요시 formLogin()이 위에 적혀있어서 저 주소로 가게됨
			.userInfoEndpoint() //구글로그인 완료시 엑세스토큰+사용자프로필정보를 한번에 받음 (이미 코드는 받았고 그 코드로 엑세스토큰을 받고 그 토큰으로 사용자정보를 받는것 이 정보가 principalOauth2UserService에 리턴됨)
			.userService(principalOauth2UserService);// 구글 로그인 완료 후 후처리 필요 그 후처리를 이 객체에서 처리해줌 
	}
}

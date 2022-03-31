package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller //view를 리턴하겠다
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired //SecurityConfig에 구현되어있는 객체로 @Bean으로 등록해놔서 @Autowired가능
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//일반 id pw 로그인시
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication, //스프링 시큐리티에서 Authentication객체는 세션객체로써 di되어 들어온 객체로 PrincipalDetails.java에 설명 적어놨음 
			@AuthenticationPrincipal PrincipalDetails userDetails) { //@AuthenticationPrincipal 이 어노테이션을 통해 세션정보에 접근할 수 있음 UserDetails타입을 반환하기에 우리가 구현한 PrincipalDetails로 받을수 있음
		System.out.println("/test/login ===================");
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal(); //Authentication객체의 getPrincipal() 메서드를 실행하면 UserDetails타입의 사용자객체를 return 그럼 우리는 UserDetails를 구현한 PrincipalDetails로 다운캐스팅
		System.out.println("authentication : "+ principalDetails.getUser()); //이렇게 Authentication을 다운캐스팅 해서 안에있는 User객체를 가져올수도 있고
		
		System.out.println("userDetails : "+userDetails.getUser());//아니면 @AuthenticationPrincipal을 통해 User객체를 가져올수도 있다
		return "세션정보확인하기";
	}
	
	//구글 oauth 로그인 시
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { //여기서도 위처럼 @AuthenticationPrincipal로 세션정보에 접근가능함 대신 타입이 위와 다름
		System.out.println("/test/oauth/login ===================");
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal(); //구글 로그인시 Authentication객체의 getPrincipal() 메서드를 실행하면 OAuth2User 객체를 리턴
		System.out.println("authentication : "+ oauth2User.getAttributes()); //이렇게 Authentication을 다운캐스팅 해서 안에있는 User객체를 가져올수도 있다
		System.out.println("oauth2User : "+oauth.getAttributes()); //아니면 @AuthenticationPrincipal을 통해 User객체를 가져올수도 있다
		
		return "OAuth 세션정보확인하기";
		
		//이렇게 일반로그인 oauth 로그인 객체가 UserDetails/OAuth2User로 2개면 처리하기 귀찮음 그래서 이 2개를 다 상속받는 객체를 사용해주면 편함
		//즉 우리가 만든 PrincipalDetails이 이 2개다 상속받으면 된다는거
	}
	
	//localhost:8080/
	//localhost:8080
	@GetMapping({"", "/"})
	public String index() {
		
		//이 프로젝트에선 템플릿 엔진을 jsp가 아닌 spring에서 공식으로 지원해주는 mustache를 사용
		//spring이 기본적으로 권장하는 템플릿 엔진이기에 기본폴더가 src/main/resources/
		//viewresolver 설정 : /templates/(prefix), .mustache(suffix) 이 설정을 application.yml에 적어줬음 (근데 pom.xml에 mustache를 의존성 등록해주면 자동으로 이 경로가 잡혀서 생략가능 근데 걍 적었음)
		//그런데 여기서 부르는건 index.mustache가 아닌 index.html을 부를건데 WebMvcConfig.java에서 viewresolver를 재설정해서 html을 부를수 있게 만들었기 때문
		return "index";
		
		//여기서  index로 처음 들어가보면 스프링시큐리티 로그인창이 나옴 시큐리티 의존성 등록을 하면 기본적으로 모든 url은 막혀서 인증이 필요한 서버가 됨
	}

	//일반로그인을 해도 PrincipalDetails로 받음
	//oauth 로그인해도 PrincipalDetails로 받음
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : "+principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	//요건 아무 설정하지 않았으면 스프링 시큐리티가 이 주소를 낚아채서 스프링 시큐리티 로그인 페이지로 사용 - SecurityConfig.java파일에서 @EnableWebSecurity를 통해 필터등록을 시켜주니 작동 안함
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword); // Security
		user.setPassword(encPassword);
		userRepository.save(user); //패스워드가 평문이면 시큐리티로 로그인 불가능
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}

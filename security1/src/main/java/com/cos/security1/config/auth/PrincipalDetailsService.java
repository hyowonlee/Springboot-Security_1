package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

// 시큐리티 설정(SecurityConfig.java)에서 loginProcessingUrl("/login"); 을 해놈
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행
// 즉 로그인 요청시 밑에 service로 등록된 클래스의 함수 loadUserByUsername가 실행된다는 뜻
@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	
	// 시큐리티 session(내부 Authentication(내부 UserDetails)) 이 함수가 다 알아서 이런식으로 구성해줌
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//여기서 매개변수인 username은 뷰(html)에서 input으로 넘긴 username이다 뷰에서도 이 input의 name이 username이여야됨
		//만약 뷰 단에서 input의 name이 username이 아니라 다른거면 여기서 인식을 못해줌
		//뷰에서 input의 name을 username말고 다른걸로 쓰고싶다면 SecurityConfig.java에서  .usernameParameter("username2") 이런식으로 수정해주면 됨 
		
		User userEntity = userRepository.findByUsername(username); //로그인한 이름의 유저를 db에서 조회 
		if(userEntity != null) {
			//만약 유저가 있다면 시큐리티에서 유저 정보저장에 사용하는 UserDetails를 상속하는 PrincipalDetails에 user객체를 넣어서 리턴
			// 이 리턴되는 값이 Authentication객체 내부로 리턴됨 그리고 이 Authentication은 세션안에 들어감
			// 시큐리티 session(내부 Authentication(내부 UserDetails))   즉 이런식으로 세션이 구성되어있는 형태
			return new PrincipalDetails(userEntity);
		}
		//유저가 없다면 null 리턴
		return null;
	}

}

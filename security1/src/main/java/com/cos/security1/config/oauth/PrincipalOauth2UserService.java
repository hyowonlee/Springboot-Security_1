package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

//SecurityConfig에서 userService(principalOauth2UserService);를 통해 구글 로그인 완료 후 후처리를 여기서 처리하는 객체
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; //password 해쉬화 객체 생성 함수
	
	@Autowired
	private UserRepository userRepository;
	
	//구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수 / userRequest객체가 구글로부터 받은 사용자정보 객체이다
	//이 객체를 통해 구글 로그인한 사람을 강제 회원가입 시킬것
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration :"+userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인했는지 확인가능 구글로그인했는지 facebook로그인했는지
		System.out.println("getAccessToken :"+userRequest.getAccessToken().getTokenValue());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글로그인 버튼클릭 -> 구글 로그인창 -> 로그인완료 -> code를 리턴(OAuth-Client 라이브러리) -> AccessToken요청
		//-> AccessToken 받은게 userRequest정보 -> loadUser함수 호출(회원프로필 받아야함 이때 쓰는 함수가 loadUser함수) -> 구글로부터 회원프로필 받아준다
		System.out.println("getAttributes :"+oauth2User.getAttributes()); // 이 super.loadUser(userRequest).getAttributes()정보를 토대로 강제 회원가입시킬것
		
		// 구글 oauth 시 회원가입을 강제로 진행
		String provider = userRequest.getClientRegistration().getClientId(); // google
		String providerId = oauth2User.getAttribute("sub"); // oauth로그인시 정보를 받아오는 Attribute안에 sub라는 key로 되어있는 값을 가져옴 (이값은 구글이 보내주는 primary key 값)
		String username = provider+"_"+providerId; //username은 중복되면 안되니 sub값을 통해 만들어줌 (예 google_113020069459415702274)
		String password = bCryptPasswordEncoder.encode("겟인데어"); // 겟인데어라는 값을 해쉬화 시켜서 비밀번호로 db에 넣을것
		String email = oauth2User.getAttribute("email"); 
		String role = "ROLE_USER";
		
		//중복로그인이면 안되니 db에 없을때만 회원가입
		User userEntity = userRepository.findByUsername(username);
		if(userEntity == null) {
			//User에 생성자 만들고 @Builder넣어서 빌더패턴으로 객체생성
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity); //db에 밀어넣기
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}

package com.cos.security1.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

//SecurityConfig에서 userService(principalOauth2UserService);를 통해 구글 로그인 완료 후 후처리를 여기서 처리하는 객체
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

	//구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수 userRequest객체가 구글로부터 받은 사용자정보 객체
	//이 객체를 통해 구글 로그인한 사람을 강제 회원가입 시킬것
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration :"+userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인했는지 확인가능 구글로그인했는지 facebook로그인했는지
		System.out.println("getAccessToken :"+userRequest.getAccessToken().getTokenValue());
		// 구글로그인 버튼클릭 -> 구글 로그인창 -> 로그인완료 -> code를 리턴(OAuth-Client 라이브러리) -> AccessToken요청
		//-> AccessToken 받은게 userRequest정보 -> loadUser함수 호출(회원프로필 받아야함 이때 쓰는 함수가 loadUser함수) -> 구글로부터 회원프로필 받아준다
		System.out.println("getAttributes :"+super.loadUser(userRequest).getAttributes()); // 이 super.loadUser(userRequest).getAttributes()정보를 토대로 강제 회원가입시킬것
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		
		return super.loadUser(userRequest);
	}
}

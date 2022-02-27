package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시키다
// 로그인 진행이 완료되면 session을 만들어줌
// 단 시큐리티는 시큐리티 자체 session을 만듬 (Security ContextHolder 이 값에 세션정보를 저장)
// 시큐리티에선 Authentication 타입 객체에 세션을 저장
// Authentication 안에 User 정보가 있어야됨
// 이 User 정보는 UserDetails 타입 객체여야함
// Security Session 에 세션정보를 저장해주는데 여기 들어갈수있는게 => Authentication객체 이 안에 유저정보를 저장하는건 => UserDetails 객체

@Data
public class PrincipalDetails implements UserDetails{
	
	private User user; //콤포지션
	
	//생성자
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	//해당 User의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>(); //arraylist는 collection의 자식
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
				return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	//이 밑으로 true 때린거 다 만료되지 않았고 계정이 막히지 않았고 사용중이라는 의미로 true
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 암호가 만료되지 않았나? 묻는함수
		return true;
	}

	@Override
	public boolean isEnabled() {
		// 우리 사이트에서 1년동안 회원이 로그인 안하면 휴면계정으로 하기로 했다면
		// user에서 마지막 로그인한 날짜를 저장해서 현재시간과 비교해 1년 이상 안했다면 return을 false로 해주는식으로 설정함
		// 지금은 안할거라 true로 해줌
		return true;
	}

}

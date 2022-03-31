package com.cos.security1.config.oauth.provider;

//oauth 로그인시 naver,google..등 각기 다른 사이트의 로그인을 다 같이 관리하기위한 인터페이스 (각 사이트별 유저정보 객체를 받는 인터페이스)
public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}

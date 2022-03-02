package com.cos.security1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Entity //이게 jpa 테이블 자동생성 어노테이션
@Data
public class User {
	@Id //primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role; //ROLE_USER, ROLE_ADMIN
	private String provider; // 구글로그인한 사람을 구분하기위한 필드
	private String providerId; // 구글로그인에서 받아오는 유저정보의 primary key를 저장할것
	@CreationTimestamp
	private Timestamp createDate;
	
	@Builder
	public User(int id, String username, String password, String email, String role, String provider, String providerId,
			Timestamp createDate) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
		this.createDate = createDate;
	}
	
	
}

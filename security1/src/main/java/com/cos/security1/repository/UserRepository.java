package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//JpaRepository 가 기본적인 crud함수를 들고있음
//@Repository라는 어노테이션이 없어도 빈등록되어 ioc됨 이유는 JpaRepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// jpa query methods - findBy규칙 -> Username 문법
	// select * from user where username = 1?(이게 매개변수로 들어오는 username)
	// 이 함수는 db에서 select *를 할건데 user 테이블에서 이름이 username인 놈을 찾겠다는 쿼리를 쏜다
	public User findByUsername(String username); // jpa query methods

}

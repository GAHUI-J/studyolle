package com.studyolle.account;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.studyolle.domain.Account;

import lombok.Getter;


/*
 * 스프링 시큐리티가 다루는 유저정보와 
 * 도메인에서 다루는 유저정보 사이의 
 * 갭을 매꿔주는 일종의 어댑터
 * */



@Getter
public class UserAccount extends User{ //User <- 스프링 시큐리티가 다루는 유저정보

	private Account account; //Account <- 우리가 다루는 유저정보
	
	
	//	스프링 시큐리티가 다루는 유저정보와 우리가 다루는 유저정보를 연동
	public UserAccount(Account account) {
		super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
		this.account = account;
	}

}

package com.studyolle.main;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;


import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;


@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
	

	/*
	[autowired로 주입을 받는 이유]
	junit5-> dependency injection 지원 
	(생성자를 사용하는 방법 private final ~~ @RequredArgs~~) 이걸로는 주입이 안 됨 -> junit이 먼저 개입을 해버리기 때문
	*/
	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	
	
	@BeforeEach
	void beforeEach() {
		SignUpForm signUpForm = new SignUpForm();
		signUpForm.setNickname("gahui");
		signUpForm.setEmail("gahui@email.com");
		signUpForm.setPassword("12345678");
		accountService.processNewAccount(signUpForm);
	}
	
	
	//db에 중복해서 들어가는 문제 해결
	@AfterEach
	void AfterEach() {
		accountRepository.deleteAll();
	}
	
	
	@DisplayName("이메일로 로그인 성공")
	@Test
	void login_with_email() throws Exception {
		mockMvc.perform(post("/login")
				.param("username","gahui@email.com")
				.param("password","12345678")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"))
		.andExpect(authenticated().withUsername("gahui"));

	}

	@DisplayName("닉네임으로 로그인 성공")
	@Test
	void login_with_nickname() throws Exception {
		mockMvc.perform(post("/login")
				.param("username","gahui")
				.param("password","12345678")
				.with(csrf())) //스프링 시큐리티를 쓰면 기본적으로 csrf 라는 protection이 활성화되므로 테스트에서도 넣어줘야 함
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"))
		.andExpect(authenticated().withUsername("gahui"));
		
	}
	
	
	@DisplayName("로그인 실패")
	@Test
	void login_fail() throws Exception {
		mockMvc.perform(post("/login")
				.param("username","1111111")
				.param("password","111111111111")
				.with(csrf())) 
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/login?error"))
		.andExpect(unauthenticated());
		
	}
	
	
	
	@WithMockUser
	@DisplayName("로그아웃")
	@Test
	void logout() throws Exception {
		mockMvc.perform(post("/logout")
				.with(csrf())) 
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"))
		.andExpect(unauthenticated());
		
	}
	
	
	
	

}

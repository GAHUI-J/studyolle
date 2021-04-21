package com.studyolle.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@DisplayName("회원가입화면 보이는지 테스트")
	@Test
	void signUpForm() throws Exception {
		mockMvc.perform(get("/sign-up"))
				.andDo(print()) //view Test
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"))
				//signUpForm Attribute 가 없으면 에러가 나므로 확인절차
				.andExpect(model().attributeExists("signUpForm"));
	}


}














package com.studyolle.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.*;

import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.domain.Account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@Transactional //트랜잭션 안 걸면 token값 null값 돼버림
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	//유저를 조회해봐야 하므로
	@Autowired
	private AccountRepository accountRepository;
	
	@MockBean
	JavaMailSender javaMailSender; // mailsender는 내가 인터페이스만 관리할 뿐이고 외부서비스임
	
	@DisplayName("인증 메일 확인 - 입력값 오류")
	@Test
	void checkEmailToken_with_wrong_input() throws Exception {
		mockMvc.perform(get("/check-email-token")
				.param("token", "sdasgdsaeawdas")
				.param("email", "email@email.com"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("error"))
				.andExpect(view().name("account/checked-email"))
				.andExpect(unauthenticated()); //인증이 안 된 사용자인지
	}
	@DisplayName("인증 메일 확인 - 입력값 정상")
	@Test
	void checkEmailToken() throws Exception {
		
		Account account = Account.builder()
				.email("gahui@test.com")
				.password("12345678")
				.nickname("gahui")
				.build();
		Account newAccount = accountRepository.save(account);
		newAccount.generateEmailCheckToken();
		
		mockMvc.perform(get("/check-email-token")
				.param("token", newAccount.getEmailCheckToken())
				.param("email", newAccount.getEmail()))
		.andExpect(status().isOk())
		.andExpect(model().attributeDoesNotExist("error"))
		.andExpect(model().attributeExists("nickname"))
		.andExpect(model().attributeExists("numberOfUser"))
		.andExpect(view().name("account/checked-email"))
		.andExpect(authenticated().withUsername("gahui")); //인증이 된 사용자인지
	}
	
	
	@DisplayName("회원가입화면 보이는지 테스트")
	@Test
	void signUpForm() throws Exception {
		mockMvc.perform(get("/sign-up"))
				.andDo(print()) //view Test
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"))
				//signUpForm Attribute 가 없으면 에러가 나므로 확인절차
				.andExpect(model().attributeExists("signUpForm"))
				.andExpect(unauthenticated());;
	}
	
	@DisplayName("회원 가입 처리 - 입력값 오류")
	@Test
	void signUpSubmit_with_wrong_input() throws Exception{
		
		mockMvc.perform(post("/sign-up")
				.param("nickname","gahui")
				.param("email", "email..")
				.param("password", "12345")
				/*
				 * CSRF 토큰이 제공이 안되고 데이터가 넘어로 경우
				 * (또는 토큰값이 다를 경우) ->403에러
				 * 
				 * 테스트할때는 CSRF 토큰이 없기때문에 403에러(유효하지 않다!)가 뜬 것
				 * */
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"))
				.andExpect(unauthenticated());
	}
	
	
	@DisplayName("회원 가입 처리 - 입력값 정상")
	@Test
	void signUpSubmit_with_correct_input() throws Exception{
		
		mockMvc.perform(post("/sign-up")
				.param("nickname","gahui")
				.param("email", "gahui@test.com")
				.param("password", "1234512345")
				.with(csrf()))
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/"))
		.andExpect(authenticated().withUsername("gahui")); //.withUsername() 유저네임도 같이 확인 //왜 여기서 test error가 뜰까????? -> 해결)가입할때 닉네임에 이메일을 넣어버림
		
		Account account = accountRepository.findByEmail("gahui@test.com");
		assertNotNull(account);
//		assertTrue(accountRepository.existsByEmail("gahui@test.com")); 위와 같음		
		assertNotEquals(account.getPassword(), "1234512345");
		//토큰이 null이 아닌지 확인
		assertNotNull(account.getEmailCheckToken());
		//아무거나[any(SimpleMailMessage.class)] 가지고 send(호출)됐는지 확인
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}
	


}














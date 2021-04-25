package com.studyolle.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	AccountRepository accountRepository;
	
	
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}

	
	
	
//	@BeforeEach
//	void beforeEach() {
//		SignUpForm signUpForm = new SignUpForm();
//		signUpForm.setNickname("gahui");
//		signUpForm.setEmail("gahui@email.com");
//		signUpForm.setPassword("12345678");
//		accountService.processNewAccount(signUpForm);
//	}
	
	
	
	@WithAccount("gahui") //없으면 실행 안 됨 (실제로 있는 이용자여야 폼 확인 가능)
//	@WithUserDetails(value="gahui") //문제점: BeforeEach 전에 WithUserDetails 가 먼저 실행됨
	@DisplayName("프로필 수정 폼")
	@Test
	void updateProfileForm() throws Exception {
		mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("profile"));
		
		
	}
	
	@WithAccount("gahui")
//	@WithUserDetails(value="gahui") //문제점: BeforeEach 전에 WithUserDetails 가 먼저 실행됨
	@DisplayName("프로필 수정하기 - 입력값 정상")
	@Test
	void updateProfile() throws Exception {
		String bio = "짧은 소개를 수정하는 경우.";
		mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
				.param("bio", bio)
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
				.andExpect(flash().attributeExists("message"));
		
		Account gahui = accountRepository.findByNickname("gahui");
		assertEquals(bio, gahui.getBio());
		
	}
	
	
	@WithAccount("gahui")
	@DisplayName("프로필 수정하기 - 입력값 에러")
	@Test
	void updateProfile_error() throws Exception {
		String bio = "길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.길게 소개를 수정하는 경우.";
		mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
				.param("bio", bio)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
		.andExpect(model().attributeExists("account"))
		.andExpect(model().attributeExists("profile"))
		.andExpect(model().hasErrors());
		
		Account gahui = accountRepository.findByNickname("gahui");
		assertNull(gahui.getBio());
		
	}

}

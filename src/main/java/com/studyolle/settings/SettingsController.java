package com.studyolle.settings;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.main.CurrentUser;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class SettingsController {
	
	
	@InitBinder("passwordForm") //패스워드폼을 처리할 때
	public void initBinder(WebDataBinder webDataBinder) {
		//validator 추가
		webDataBinder.addValidators(new PasswordFormValidator());
	}
	
	
	
	static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
	static final String SETTINGS_PROFILE_URL = "/settings/profile";
	
	static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
	static final String SETTINGS_PASSWORD_URL = "/settings/password";
	
	
	private final AccountService accountService;
	
	@GetMapping(SETTINGS_PROFILE_URL)
	public String updateProfileForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new Profile(account));
		return SETTINGS_PROFILE_VIEW_NAME;
	}
	
	
	@PostMapping(SETTINGS_PROFILE_URL) //ModelAttribute 생략 가능
	public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors, 
								Model model, RedirectAttributes attributes)  {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_PROFILE_VIEW_NAME;
		}
		accountService.updateProfile(account, profile);
		attributes.addFlashAttribute("message", "프로필을 수정했습니다."); // 한 번 쓰고 없어지는 데이터("Flash") , @GetMapping(SETTINGS_PROFILE_URL) 로 전송돼  폼이 보여졌을 때 메세지 출력
		
		return "redirect:" + SETTINGS_PROFILE_URL;
	}
	
	@GetMapping(SETTINGS_PASSWORD_URL)
	public String updatePasswordForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new PasswordForm());
		return SETTINGS_PASSWORD_VIEW_NAME;
	}
	
	@PostMapping(SETTINGS_PASSWORD_URL)
	public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
								Model model, RedirectAttributes attributes) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_PASSWORD_VIEW_NAME;
		}
		
		accountService.updatePassword(account, passwordForm.getNewPassword());
		attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
		return "redirect:" + SETTINGS_PASSWORD_URL;
	}
	
	

}

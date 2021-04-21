package com.studyolle.account;



import javax.validation.Valid;

import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.studyolle.domain.Account;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final SignUpFormValidator signUpFormValidator;
	private final AccountService accountService;
		
	
	@InitBinder("signUpForm")
	public void initBinder(WebDataBinder webDataBinder) {
		//validator 추가
		webDataBinder.addValidators(signUpFormValidator);
	}
	
	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		//해당 클래스 이름(SignUpForm)이 attribute의 이름이 될 경우 생략 가능
//		model.addAttribute("signUpForm", new SignUpForm());
		model.addAttribute(new SignUpForm());
		return "account/sign-up";
	}
	
	@PostMapping("/sign-up")
	//3개의 값들을 SignUpForm이라는 하나의 객체로 받아올 때 @ModelAttribute를 써서 받아옴-> 생략 가능
//	public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
	public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
		if(errors.hasErrors()) {
			return "account/sign-up";
		}
		
		//인증이메일을 보냈다는 걸 컨트롤러에서 알 필요는 없다(서비스 뒤쪽으로 숨기자)
		accountService.processNewAccount(signUpForm);
		return "redirect:/";
	}



}
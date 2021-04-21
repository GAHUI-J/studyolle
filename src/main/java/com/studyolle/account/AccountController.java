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
	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	
	
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
		
		Account account = Account.builder()
				.email(signUpForm.getEmail())
				.nickname(signUpForm.getEmail())
				.password(signUpForm.getPassword()) // TODO encoding 해야함 -> password를 평문으로 저장하게 되면 서비스가 굉장히 위험해짐(db 털리면 끝장남))
				.studyCreatedByWeb(true)
				.studyEnrollmentResultByWeb(true)
				.studyUpdatedByWeb(true)
				.build();
		
		Account newAccount = accountRepository.save(account);
		newAccount.generateEmailCheckToken();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(newAccount.getEmail());
		mailMessage.setSubject("스터디올래, 회원가입 인증");
		mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()+
				"&email="+newAccount.getEmail());
		
		javaMailSender.send(mailMessage);
		
		
		
		
		return "redirect:/";
	}

}

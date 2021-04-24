package com.studyolle.account;



import java.time.LocalDateTime;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.studyolle.domain.Account;
import com.studyolle.main.CurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final SignUpFormValidator signUpFormValidator;
	private final AccountService accountService;
	private final AccountRepository accountRepository;
		
	
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
		Account account = accountService.processNewAccount(signUpForm);
		accountService.login(account);
		return "redirect:/";
	}
	
	
	@GetMapping("/check-email-token")
	public String checkEmailToken(String token, String email, Model model) {
		Account account = accountRepository.findByEmail(email); //여기서의 account 객체는 영속성 컨텍스트에서 관리하는 객체(=persist 상태) 
		String view = "account/checked-email";
		if(account == null) {
			model.addAttribute("error", "wrong.email");
			return view;
		}
		
//수정	if(!account.getEmailCheckToken().equals(token)) {
		if(!account.isValidToken(token)) {
			model.addAttribute("error", "wrong.token");
			return view;
		}
		
		
		
		//데이터를 변경해야 하는 일이 있다면 트랜잭션으로 관리 ---> 서비스에 위임해서 관리하자!

		accountService.completeSignUp(account);
//		account.completeSignUp();
		//account에서 했어야 할 일이므로 따로 빼서 account로 ㄱㄱ
//		account.setEmailVerified(true);
//		account.setJoinedAt(LocalDateTime.now());
//		accountService.login(account);
		model.addAttribute("numberOfUser", accountRepository.count()); //count 기본 제공되는 메서드
		model.addAttribute("nickname", account.getNickname());
		return view;
		
		
	}
	
	
	@GetMapping("/check-email")
	public String checkEmail(@CurrentUser Account account, Model model) {
		model.addAttribute("email", account.getEmail());
		return "account/check-email";
	}
	
	@GetMapping("/resend-confirm-email")
	public String resendConfirmEmail(@CurrentUser Account account, Model model) {
		if(!account.canSendConfirmEmail()) {
			model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
			model.addAttribute("email", account.getEmail());
			return "account/check-email";
		}
		
		accountService.sendSignUpConfirmEmail(account); //AccountService에서 public으로 바꾸자
		
		
		
		/*
		 * redirect를 쓰는 이유 >>> 
		화면이 refresh 될 때마다 이메일이 재전송되면 안 되므로 
		(/resend-confirm-email이 url에 계속 남아있으면 화면을 refresh 할 때마다 이메일을 계속 보내게 됨) 
		의도치않게 그런 일이 일어날 수 있음을 미연에 방지 (form submit도 마찬가지) -> 조금 더 나은 UX를 제공

		 */
		
		
		
		return "redirect:/";
	}
	
	
	
	@GetMapping("/profile/{nickname}")
	public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
		
		Account byNickname = accountRepository.findByNickname(nickname);
		
		if(byNickname == null) {
			throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
		}
		
		model.addAttribute(byNickname); // model.addAttribute("account", byNickname); 과 같음
		model.addAttribute("isOwner", byNickname.equals(account));
		return "account/profile";
		
	}



}

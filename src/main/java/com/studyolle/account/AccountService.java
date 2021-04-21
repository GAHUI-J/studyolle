package com.studyolle.account;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.studyolle.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

		//컨트롤러에서 빼옴
		private final AccountRepository accountRepository;
		private final JavaMailSender javaMailSender;

		
		public void processNewAccount(@Valid SignUpForm signUpForm) {
			Account newAccount = saveNewAccount(signUpForm);
			newAccount.generateEmailCheckToken();
			sendSignUpConfirmEmail(newAccount);
			
		}
		
		
		//컨트롤러에서 빼옴
		private Account saveNewAccount(SignUpForm signUpForm) {
			Account account = Account.builder()
					.email(signUpForm.getEmail())
					.nickname(signUpForm.getEmail())
					.password(signUpForm.getPassword()) // TODO encoding 해야함 -> password를 평문으로 저장하게 되면 서비스가 굉장히 위험해짐(db 털리면 끝장남))
					.studyCreatedByWeb(true)
					.studyEnrollmentResultByWeb(true)
					.studyUpdatedByWeb(true)
					.build();
			
			Account newAccount = accountRepository.save(account);
			return newAccount;
		}
		//컨트롤러에서 빼옴
		private void sendSignUpConfirmEmail(Account newAccount) {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(newAccount.getEmail());
			mailMessage.setSubject("스터디올래, 회원가입 인증");
			mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()+
					"&email="+newAccount.getEmail());
			
			
			
			javaMailSender.send(mailMessage);//이 부분이 없으면 test에서 깨짐 -> then(javaMailSender).should().send(any(SimpleMailMessage.class)) ;
		}

}

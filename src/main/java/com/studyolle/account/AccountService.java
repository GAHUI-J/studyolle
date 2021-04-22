package com.studyolle.account;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

		//컨트롤러에서 빼옴
		private final AccountRepository accountRepository;
		private final JavaMailSender javaMailSender;
		
		private final PasswordEncoder passwordEncoder;
//		스프링 시큐리티에 빈으로 정의가 돼있지만 지금은 그 빈이 노출돼있지 않기 때문에 지금은 주입받을 수 없음 ( 주입받으려면 spring security 약간 손봐야함)
//		private final AuthenticationManager authenticationManager;

		@Transactional
		public Account processNewAccount(@Valid SignUpForm signUpForm) {
			//Transaction 범위 안에 있으므로 
			//newAccount 객체는 detached상태가 아닌 persist상태가 유지됨
			//persist상태의 객체는 트랜잭션이 종료될 때 상태를 db에 싱크
			Account newAccount = saveNewAccount(signUpForm);
			newAccount.generateEmailCheckToken();
			sendSignUpConfirmEmail(newAccount);
			
			return newAccount;
			
		}
		
		/*
		단방향
		해커한테 db가 털렸을 때 위험하므로 비밀번호는 절대로 평문을 저장해서는 안 됨!
		123456 + salt
		salt는 해싱을 할때마다 랜덤한 값을 넣어도 동작함
		동일한 비밀번호지만 솔트값이 매번 바뀌기 때문에 해싱된 결과가 매번 바뀐다
		비크립트 : 의도적으로 해싱하는 데에 시간이 좀 걸린다(장점! 느리다는 게 오히려 장점) -> 해커들이 여러번 시도할 수 없게끔
		 */
		
		//컨트롤러에서 빼옴
		private Account saveNewAccount(SignUpForm signUpForm) {
			Account account = Account.builder()
					.email(signUpForm.getEmail())
					.nickname(signUpForm.getNickname())
					//TODO 비밀번호 인코딩
					.password(passwordEncoder.encode(signUpForm.getPassword())) // TODO encoding 해야함 -> password를 평문으로 저장하게 되면 서비스가 굉장히 위험해짐(db 털리면 끝장남))
					.studyCreatedByWeb(true)
					.studyEnrollmentResultByWeb(true)
					.studyUpdatedByWeb(true)
					.build();
			
			Account newAccount = accountRepository.save(account);
			return newAccount;
		}
		//컨트롤러에서 빼옴
		public void sendSignUpConfirmEmail(Account newAccount) {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(newAccount.getEmail());
			mailMessage.setSubject("스터디올래, 회원가입 인증");
			mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()+
					"&email="+newAccount.getEmail());
			
			newAccount.setEmailCheckTokenGeneratedAt(LocalDateTime.now());
			
			
			javaMailSender.send(mailMessage);//이 부분이 없으면 test에서 깨짐 -> then(javaMailSender).should().send(any(SimpleMailMessage.class)) ;
		}


		public void login(Account account) {
			//지금은 인코딩된 password만 알 수 있기에 이 방법으로	
			UsernamePasswordAuthenticationToken token 
				= new UsernamePasswordAuthenticationToken(
							new UserAccount(account), //principal 객체
							account.getPassword(),
							List.of(new SimpleGrantedAuthority("ROLE USER"))); //세번째 파라미터로 오는 값이 권한목록
 
			SecurityContext context = SecurityContextHolder.getContext(); // securitycontextholder가 들고 있는 context 안에
			context.setAuthentication(token); // authentication 세팅을 해줄 수 있다

			/*
		 [정석]authenticationManager를 통해 인증(plain한 password 알고 있어야 함)
			UsernamePasswordAuthenticationToken token2
			= new UsernamePasswordAuthenticationToken(
					username, password);
			Authentication authenticate = authenticationManager.authenticate(token2);
			SecurityContext context2 = SecurityContextHolder.getContext();
			context.setAuthentication(token2);
			 */
			
		}

}

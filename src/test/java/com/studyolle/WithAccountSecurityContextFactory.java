package com.studyolle;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
//	이 클래스는 빈으로 등록되기 때문에  필요로 하는 빈들 얼마든지 빈 주입 받을 수 있음 
	
	private final AccountService accountService;

@Override
public SecurityContext createSecurityContext(WithAccount withAccount) {
	
	String nickname = withAccount.value();
	
	//유저 만드는 일도 여기서 (beforeEach 대신)
	SignUpForm signUpForm = new SignUpForm();
	signUpForm.setNickname(nickname);
	signUpForm.setEmail(nickname+"@email.com");
	signUpForm.setPassword("12345678");
	accountService.processNewAccount(signUpForm);
	
	
	
	UserDetails principal = accountService.loadUserByUsername(nickname);
	Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
	SecurityContext context = SecurityContextHolder.createEmptyContext();
	context.setAuthentication(authentication);
	
	return context;
}
	
	

}

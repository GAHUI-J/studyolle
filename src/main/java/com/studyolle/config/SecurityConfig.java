package com.studyolle.config;


import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.studyolle.account.AccountService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
/*
 * 이 url들은 인증하지 않고 사용해도 된다! 
 * but 그렇다고해서 안전하지 않은 요청(ex.CSRF)까지 받아들이는 것은 아님
 * CSRF: 내 사이트로 공격하는 타 사이트를 대상으로 폼 데이터를 보내는 것
 	->방어)CSRF 토큰(Security Token) 사용
 * */
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final AccountService accountService;
	private final DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				//인증 체크 하지 않도록 걸러낼 수 있음
				.mvcMatchers("/","/login","/sign-up", "/check-email-token",
							"/email-login","/check-email-login","/login-link").permitAll()
				//get만 허용
				.mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()
				//나머지는 로그인해야 가능
				.anyRequest().authenticated();
		
		//로그인 기능 사용
		http.formLogin()
				.loginPage("/login").permitAll();
		
		http.logout()
				.logoutSuccessUrl("/");
		
		
		
		/*
		이 서버를 쓰고 있지 않은 클라이언트 세션 정보까지 너무 오래 보유하게 되면 메모리에 낭비가 생김 
		(현재 이 서버를 쓰고 있는 사용자에게 할당해야 할 메모리가 부족해지는 상황 발생)
		-> 쿠키에 인증 번호를 담아서 넣어두자
		지금 이 요청에 해당하는 세션을 찾지 못할 때 
		같이 보내 온 정보 중 remeberme cookie가 있을 경우 
		이 쿠키에 들어있는 인증번호(username, password)로 인증을 시도
		*/
		http.rememberMe()
//				.key(""); 해시 기반의 쿠키 (안전하지 않은 방법)
				.userDetailsService(accountService)
				.tokenRepository(tokenRepository()); //db에서 토큰값을 읽어오거나 저장하는 인터페이스의 구현체(객체)를 주입
		

	} //손쉽게 security filter 가능

	
	
	private PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}



	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.mvcMatchers("/node_modules/**")
			//location에 있는 static한 resources들은 security filter들을 적용하지 않도록
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	
	
	

	
}

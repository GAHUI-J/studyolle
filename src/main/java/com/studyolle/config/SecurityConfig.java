package com.studyolle.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
/*
 * 이 url들은 인증하지 않고 사용해도 된다! 
 * but 그렇다고해서 안전하지 않은 요청(ex.CSRF)까지 받아들이는 것은 아님
 * CSRF: 내 사이트로 공격하는 타 사이트를 대상으로 폼 데이터를 보내는 것
 	->방어)CSRF 토큰(Security Token) 사용
 * */
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				//인증 체크 하지 않도록 걸러낼 수 있음
				.mvcMatchers("/","/login","/sign-up", "/check-email", "/check-email-token",
							"/email-login","/check-email-login","/login-link").permitAll()
				//get만 허용
				.mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()
				//나머지는 로그인해야 가능
				.anyRequest().authenticated();
		

	} //손쉽게 security filter 가능

	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.mvcMatchers("/node_modules/**")
			//location에 있는 static한 resources들은 security filter들을 적용하지 않도록
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	
	
	

	
}

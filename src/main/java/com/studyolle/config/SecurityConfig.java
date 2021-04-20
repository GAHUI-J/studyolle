package com.studyolle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
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

	
}

package com.studyolle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory=WithAccountSecurityContextFactory.class) //SecurityContext를 만들어줄 Factory를 만들러 ㄱㄱ
public @interface WithAccount {
	
	String value();

}

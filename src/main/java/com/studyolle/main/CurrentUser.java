package com.studyolle.main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

//런타임까지도 유지가 되야 하고
@Retention(RetentionPolicy.RUNTIME)
//타겟은 파라미터에 붙일 수 있도록 
@Target(ElementType.PARAMETER)
/*
 현재 이 어노테이션이 참조하고 있는 객체가 
 anonymouseUser 문자열이라면, 파라미터를 null로 세팅하고 
 아닌 경우, account라는 프로퍼티를 세팅
 */
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") //
public @interface CurrentUser {

}

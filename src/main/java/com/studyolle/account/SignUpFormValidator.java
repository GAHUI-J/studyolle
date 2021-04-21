package com.studyolle.account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

//의존성 주입
/*
 * 어떤 빈(Bean)이 생성자가 하나만 있고 
 * 생성자가 받는 파라미터들이 빈으로 등록되어 있다면 
 * 자동으로 빈을 주입해주므로 @Autowired나 @Inject같은 어노테이션을 사용하지 않아도 
 * 의존성 주입이 됨*/
@Component
@RequiredArgsConstructor //private final 생성자 만들어줌
public class SignUpFormValidator implements Validator{
	//실제로 db에서 조회해야함
	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> aClass) {
		//signupform 타입의 인스턴스를 검사할 것이다
		return aClass.isAssignableFrom(SignUpForm.class);
	}

	@Override
	public void validate(Object object, Errors errors) {
		// TODO email, nickname (이메일, 닉네임 중복검사)
		SignUpForm signUpForm = (SignUpForm)object;
		
		//이메일에 해당하는 데이터가 있다면
		if(accountRepository.existsByEmail(signUpForm.getEmail())) {
			errors.rejectValue("email", "invalid.email", new Object[] {signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
		}
		//닉네임에 해당하는 데이터가 있다면
		if(accountRepository.existsByNickname(signUpForm.getNickname())) {
			errors.rejectValue("nickname", "invalid.nickname", new Object[] {signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
			
		}
		
	}
	

}

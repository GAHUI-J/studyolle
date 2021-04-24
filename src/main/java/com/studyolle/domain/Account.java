package com.studyolle.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
//id만 사용하는 이유 ->연관관계가 복잡해질 때 @EqualsAndHashCode 에서 서로 다른 연관관계를 계속해서 
//				  순환참조하느라 무한루프가 발생(stackoverflow로 이어짐)하는 것을 방지하기 위함
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {
	
	//varchar(255)
	
	@Id @GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String email;
	
	@Column(unique=true)
	private String nickname;
	
	private String password;
	
	//이메일 검증 여부
	private boolean emailVerified;
	
	//이메일 검증할 때 토큰값
	private String emailCheckToken;
	private LocalDateTime emailCheckTokenGeneratedAt;
	
	private LocalDateTime joinedAt;
	
	//간단한 자기소개
	private String bio;
	
	private String url;
	
	private String occupation;
	
	private String location;
	
	@Lob @Basic(fetch = FetchType.EAGER)
	private String profileImage;
	
	//알림 설정
	
	//스터디 개설 알림
	private boolean studyCreatedByEmail; //이메일로 받을 것인지 여부
	private boolean studyCreatedByWeb; //웹상에서 받을 것인지 여부
	
	//스터디입회 알림
	private boolean studyEnrollmentResultByEmail; 
	private boolean studyEnrollmentResultByWeb; 
	
	//스터디 업데이트 내용 알림
	private boolean studyUpdatedByEmail; 
	private boolean studyUpdatedByWeb;
	
	
	
	public void generateEmailCheckToken() {

		this.emailCheckToken = UUID.randomUUID().toString();
		this.emailCheckTokenGeneratedAt = LocalDateTime.now();
	}



	public void completeSignUp() {
		//account.setEmailVerified(true);
		this.emailVerified = true;
		//account.setJoinedAt(LocalDateTime.now());
		this.joinedAt = LocalDateTime.now();
		
	}



	public boolean isValidToken(String token) {
		return this.emailCheckToken.equals(token);
	}



	public boolean canSendConfirmEmail() {
		return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
	} 
	
	
	
	
	
	
	
	

}

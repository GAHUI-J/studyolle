package com.studyolle.settings;

import com.studyolle.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //기본 생성자를 만들어줌 -> 없을 시 AccountService의 updateProfile(Account account, Profile profile)에서 NullPointerException 일어남
public class Profile {
	
	private String bio;
	
	private String url;
	
	private String occupation;
	
	private String location;
	
	public Profile(Account account) {
		this.bio = account.getBio();
		this.url = account.getUrl();
		this.occupation = account.getOccupation();
		this.location = account.getLocation();
	}


}

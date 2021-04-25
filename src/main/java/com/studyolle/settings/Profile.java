package com.studyolle.settings;

import org.hibernate.validator.constraints.Length;

import com.studyolle.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //기본 생성자를 만들어줌 -> 없을 시 AccountService의 updateProfile(Account account, Profile profile)에서 NullPointerException 일어남
public class Profile {
	
	@Length(max=35)
	private String bio;
	
	@Length(max=50)
	private String url;
	
	@Length(max=50)
	private String occupation;
	
	@Length(max=50)
	private String location;
	
	private String profileImage;
	
	public Profile(Account account) {
		this.bio = account.getBio();
		this.url = account.getUrl();
		this.occupation = account.getOccupation();
		this.location = account.getLocation();
		this.profileImage = account.getProfileImage();
	}


}

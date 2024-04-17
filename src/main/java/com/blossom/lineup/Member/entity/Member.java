package com.blossom.lineup.Member.entity;

import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.base.BaseEntity;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Member extends BaseEntity implements BaseMember{

	private String userName;
	private String phoneNumber;
	private Long socialId;
	@Enumerated(value = EnumType.STRING)
	private Role role;
	private String email;

	protected Member(String userName, String phoneNumber, Long socialId, Role role, String email) {
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.socialId = socialId;
		this.role = role;
		this.email = email;
	}

}

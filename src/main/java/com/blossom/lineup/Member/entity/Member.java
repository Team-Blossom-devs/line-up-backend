package com.blossom.lineup.Member.entity;

import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.base.BaseEntity;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Member extends BaseEntity implements BaseMember {

	private String userName;
	private String phoneNumber;
	@Enumerated(value = EnumType.STRING)
	private Role role;
	private String uuid;
	private String refreshToken; //JWT 사용하여 로그인 시 발행된 RefreshToken을 저장한다.

	protected Member(String userName, String phoneNumber,Role role, String refreshToken) {
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.uuid = UUID.randomUUID().toString();
		this.refreshToken = refreshToken;
	}

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}
}

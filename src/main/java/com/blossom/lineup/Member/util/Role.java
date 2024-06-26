package com.blossom.lineup.Member.util;

import lombok.Getter;

/**
 * MANAGER: Organization 관리자, ADMIN: 프로젝트 소유자 관리자 계정(슈퍼 계정), USER: 이용객, GUEST: 소셜 로그인을 처음하는 USER
 */
@Getter
public enum Role {

	MANAGER("MANAGER"), ADMIN("ADMIN"), USER("USER"), GUEST("GUEST");

	private final String role;

	Role(String role){
		this.role = role;
	}
}

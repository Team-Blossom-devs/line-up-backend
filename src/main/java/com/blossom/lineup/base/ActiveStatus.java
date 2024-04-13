package com.blossom.lineup.base;

import lombok.Getter;

@Getter
public enum ActiveStatus {
	ACTIVATED("ACTIVATED"), DELETED("DELETED");

	private final String activeStatus;

	ActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
}

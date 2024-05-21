package com.blossom.lineup.LineManagement.util;

import lombok.Getter;

@Getter
public enum EntranceTimeLimit {
	TEMP(15L);

	private final Long time;

	EntranceTimeLimit(Long time) {
		this.time = time;
	}
}

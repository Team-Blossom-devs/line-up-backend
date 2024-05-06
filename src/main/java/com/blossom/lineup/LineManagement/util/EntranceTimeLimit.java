package com.blossom.lineup.LineManagement.util;

import lombok.Getter;

@Getter
public enum EntranceTimeLimit {
	TEMP(10L);

	private final Long time;

	EntranceTimeLimit(Long time) {
		this.time = time;
	}
}

package com.blossom.lineup.Waiting.util;

import lombok.Getter;

/**
 * WAITING: 줄 서는 중, PENDING: 입장 중, COMPLETE: 입장 완료/이용 중
 */
@Getter
public enum EntranceStatus {

	WAITING("WAITING"), PENDING("PENDING"), COMPLETE("COMPLETE");

	private final String entranceStatus;

	EntranceStatus(String entranceStatus){
		this.entranceStatus = entranceStatus;
	}
}

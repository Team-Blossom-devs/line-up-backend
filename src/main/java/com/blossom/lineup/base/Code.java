package com.blossom.lineup.base;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum Code {

	// OK
	OK(HttpStatus.OK, "COM-000", "ok"),

	// WAT : Waiting entity error
	WAITING_NOT_FOUND(HttpStatus.BAD_REQUEST, "WAT-001", "대기 기록을 찾을 수 없습니다."),
	WAITING_NUMBER_NULLPOINTER(HttpStatus.BAD_REQUEST, "WAT-101", "대기 번호가 null 입니다."),

	// KFK : Kafka error
	CONSUME_DATA_NOT_FOUND(HttpStatus.BAD_REQUEST, "KFK-001", "클러스터에서 오류가 발생했습니다. 로그를 확인해주세요.");


	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	Code(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}

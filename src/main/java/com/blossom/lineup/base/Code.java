package com.blossom.lineup.base;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum Code {

	// OK
	OK(HttpStatus.OK, "COM-000", "ok"),
	JSON_TYPE_FAIL(HttpStatus.BAD_REQUEST, "COM-400", "JSON 타입이 맞지 않습니다."),
	VALIDATION_FAIL(HttpStatus.BAD_REQUEST, "COM-400", "입력 형식이 올바르지 않습니다."),
	SERVER_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "COM-500", "서버 내의 문제가 발생했습니다."),

	// CST : Customer entity error
	CUSTOMER_NOT_FOUND(HttpStatus.BAD_REQUEST, "CST-001", "회원을 찾을 수 없습니다."),

	// ORG : Organization entity error
	ORGANIZATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "ORG-001", "주점을 찾을 수 없습니다."),

	// WAT : Waiting entity error
	WAITING_NOT_FOUND(HttpStatus.BAD_REQUEST, "WAT-001", "대기 기록을 찾을 수 없습니다."),
	WAITING_DUPLICATE(HttpStatus.BAD_REQUEST, "WAT-002", "대기는 1곳만 가능합니다."),
	WAITING_NUMBER_IS_NULL(HttpStatus.BAD_REQUEST, "WAT-101", "대기 번호가 null 입니다."),

	ENTRANCE_TIME_IS_NULL(HttpStatus.BAD_REQUEST, "WAT-501", "입장시간이 null 인 테이블이 존재합니다."),

	// KFK : Kafka error
	CONSUME_DATA_NOT_FOUND(HttpStatus.BAD_REQUEST, "KFK-001", "클러스터에서 오류가 발생했습니다. 로그를 확인해주세요."),

	// MEM : Member Entity error
	MEMBER_UNAUTHORIZED(HttpStatus.BAD_REQUEST, "MEM-001", "유효하지 않은 토큰입니다."),
	MANAGER_PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "MEM-100", "비밀번호가 일치하지 않습니다."),
	MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-101", "일치하는 매니저가 없습니다."),
	MEMBER_LOGIN_REQUIRED(HttpStatus.BAD_REQUEST, "MEM-102", "로그인 후 다시 시도해주세요."),
	MEMBER_LOGIN_FAIL(HttpStatus.BAD_REQUEST, "MEM-103", "로그인에 실패했습니다.");


	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	Code(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}

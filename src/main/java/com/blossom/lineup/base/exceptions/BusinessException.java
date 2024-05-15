package com.blossom.lineup.base.exceptions;

import com.blossom.lineup.base.Code;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

	private Code code;

	public BusinessException(Code code) {
		super(code.getMessage());
		this.code = code;
	}

	public BusinessException(String message, Code code) {
		super(message);
		this.code = code;
	}

	public BusinessException(String message, Throwable cause, Code code) {
		super(message, cause);
		this.code = code;
	}

	public BusinessException(Throwable cause, Code code) {
		super(cause);
		this.code = code;
	}

	public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

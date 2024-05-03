package com.blossom.lineup.base.exceptions;

import com.blossom.lineup.base.Code;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException{

	private Code code;

	public ServerException(Code code) {
		super();
		this.code = code;
	}

	public ServerException(String message, Code code) {
		super(message);
		this.code = code;
	}

	public ServerException(String message, Throwable cause, Code code) {
		super(message, cause);
		this.code = code;
	}

	public ServerException(Throwable cause, Code code) {
		super(cause);
		this.code = code;
	}

	public ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

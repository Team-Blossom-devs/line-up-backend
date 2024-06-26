package com.blossom.lineup.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

	private String code;
	private String message;
	private T data;

	private Response(String code) {
		this.code = code;
	}

	private Response(String code, T data) {
		this.code = code;
		this.data = data;
	}

	private Response(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public static Response<Void> ok(){
		return new Response<>(Code.OK.getCode());
	}

	public static <T> Response<T> ok(T data) {
		return new Response<>(Code.OK.getCode(), data);
	}

	public static <E> Response<Void> fail(String code) {
		return new Response<>(code);
	}

	public static <E> Response<Void> fail(String code, String message) {
		return new Response<>(code, message);
	}

	public static <T> Response<T> fail(String code, String message, T data){
		return new Response<>(code,message,data);
	}

}

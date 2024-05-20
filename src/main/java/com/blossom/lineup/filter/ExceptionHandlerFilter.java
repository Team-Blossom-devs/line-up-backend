package com.blossom.lineup.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	public static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (BusinessException exception){

			log.error("BusinessException={}", exception.getMessage());

			response.setStatus(exception.getCode().getHttpStatus().value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");

			setBusinessExceptionResponse(response, exception.getCode());

		} catch (Exception exception){

			log.error("Exception={}", exception.getMessage());

			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");

			setExceptionResponse(response, exception);

		}

	}

	private void setBusinessExceptionResponse(HttpServletResponse response, Code code) {
		try{
			response.getWriter().write(objectMapper.writeValueAsString(Response.fail(code.getMessage(), code.getCode())));
		} catch (IOException e){
			log.error("check exception filter");
			log.error(e.getMessage());
		}

	}

	private void setExceptionResponse(HttpServletResponse response, Exception exception) {
		try{
			response.getWriter().write(objectMapper.writeValueAsString(Response.fail(Code.SERVER_FAIL.getCode(), exception.getMessage())));
		} catch (IOException e){
			log.error("check exception filter");
			log.error(e.getMessage());
		}

	}

}

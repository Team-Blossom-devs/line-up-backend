package com.blossom.lineup.base.exceptions;

import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHanlder {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<?>> handleNotReadableException(HttpMessageNotReadableException e){
        // 입력형식이 long인데 string으로 입력하는 등의 오류 처리
        // getMessage에서 어떤 값이 틀렸는지 알려줌. -> 형식 오류는 알려줘야 할 것 같아서 status 400으로 내려줌.
        log.error("Type Error = {}, ", e.getMessage());
        return ResponseEntity.badRequest().body(
                Response.fail(Code.JSON_TYPE_FAIL.getCode(),
                        Code.JSON_TYPE_FAIL.getMessage(),
                        e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> handleValidateException(MethodArgumentNotValidException e) {
        log.error("validate fail = {}, error field = {}", e.getMessage(), e.getBindingResult());
        return Response.fail(Code.VALIDATION_FAIL.getCode(),Code.VALIDATION_FAIL.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e){
        log.info("Exception Code = {}, message = {}", e.getCode(), e.getMessage());
        return Response.fail(e.getCode().getCode(),e.getCode().getMessage());
    }

    @ExceptionHandler(ServerException.class)
    public Response<?> handleServerException(ServerException e){
        log.error("Exception Code = {}, message = {}", e.getCode().getCode(), e.getMessage());
        return Response.fail(e.getCode().getCode(), e.getCode().getMessage());
    }
}

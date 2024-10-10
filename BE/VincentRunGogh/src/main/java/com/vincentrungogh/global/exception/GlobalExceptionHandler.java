package com.vincentrungogh.global.exception;

import com.vincentrungogh.global.util.ResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(final CustomException ex) {
        ResultDto<Object> response = ResultDto.res(
                ex.getErrorCode().getHttpStatus().value(),
                ex.getMessage()
        );

        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(final BadCredentialsException ex) {
        ResultDto<Object> response = ResultDto.res(
                HttpStatus.UNAUTHORIZED.value(),
                "회원정보가 일치하지 않습니다."
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 첫 번째 에러 메시지 반환
        ResultDto<Object> response = ResultDto.res(
                HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(final RuntimeException ex) {
        ResultDto<Object> response = ResultDto.res(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final Exception ex) {
        log.info(ex.getMessage());
        ResultDto<Object> response = ResultDto.res(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        ResultDto<Object> response = ResultDto.res(
                HttpStatus.BAD_REQUEST.value(),
                "파일 크기가 너무 큽니다. 최대 10MB까지 업로드할 수 있습니다."
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

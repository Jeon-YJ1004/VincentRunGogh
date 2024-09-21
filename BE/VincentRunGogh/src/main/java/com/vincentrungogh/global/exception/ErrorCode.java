package com.vincentrungogh.global.exception;


import org.springframework.http.HttpStatus;

//에러 코드 모음집
//사용법 에러명("message", 실제 에러상태)
public enum ErrorCode {
    UNAUTHORIZED("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    FAILED_CHANGE_ROOTING("루트화 하는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_SAVE_ROOTING("루트르 저장하는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("존재하지 않은 유저입니다.", HttpStatus.NOT_FOUND),
    JSON_PROCESSING_ERROR("JSON을 처리하는 도중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATED_EMAIL("존재하는 이메일입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_NICKNAME("존재하는 닉네임입니다", HttpStatus.BAD_REQUEST),
    INVALID_NICKNAME_LENGTH("10자 이하의 닉네임을 입력해주세요", HttpStatus.BAD_REQUEST),
    FAILED_CONVERT_FILE("파일을 변환하는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_SEND_EMAIL("이메일을 전송하는데 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    CODE_NOT_FOUND("인증코드 발급내역이 존재하지 않습니다. 인증코드를 발급해 주세요", HttpStatus.NOT_FOUND),
    CODE_EXPIRED("인증 코드가 만료되었습니다. 새로운 인증코드를 발급해 주세요", HttpStatus.GONE),
    BIRTH_INVALID("이메일과 생년월일이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;


    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

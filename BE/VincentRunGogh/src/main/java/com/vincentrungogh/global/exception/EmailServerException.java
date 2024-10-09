package com.vincentrungogh.global.exception;


public class EmailServerException extends RuntimeException {
    public EmailServerException() {
        super("이메일 전송 실패했습니다");  // 기본 메시지 설정
    }
}

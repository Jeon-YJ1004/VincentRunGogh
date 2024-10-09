package com.vincentrungogh.global.service;

import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.EmailServerException;
import com.vincentrungogh.global.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String serviceName;

    // 인증 코드 이메일 전송
    public String sendCodeEmail(String to, int code) {
        LocalDateTime expirationTime;
        try {
            expirationTime = LocalDateTime.now().plusMinutes(3);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
            String formattedExpirationTime = expirationTime.format(formatter);

            String title = "회원 가입을 위한 이메일입니다!";
            String content =
                    "이메일을 인증하기 위한 절차입니다." +
                            "<br><br>" +
                            "인증 번호는 " + code + "입니다." +
                            "<br>" +
                            "인증 번호는 " + formattedExpirationTime + "까지 유효합니다.";

            sendEmail(to, title, content);
        } catch (Exception e) {
            throw new EmailServerException();
        }

        return expirationTime.toString();
    }

    // 비밀 번호 이메일 전송
    public void sendPasswordEmail(String to, String password){
        // 1. 이메일 작성
        String from = serviceName;
        String title = "비밀번호 재발급 안내입니다!";
        String content =
                "비밀번호 재발급 절차입니다." +
                        "<br><br>" +
                        "새로운 비밀번호는 " + password + "입니다." +
                        "<br>" +
                        "로그인 후 반드시 비밀번호를 변경해주시기 바랍니다.";

        // 2. 전송
        sendEmail(to, title, content);
    }

    private void sendEmail(String to, String title, String content){
        // 이메일 전송
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(serviceName);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e){
            log.info("이메일 전송 실패" + e.getMessage());
            throw new CustomException(ErrorCode.FAILED_SEND_EMAIL);
        }
    }
}

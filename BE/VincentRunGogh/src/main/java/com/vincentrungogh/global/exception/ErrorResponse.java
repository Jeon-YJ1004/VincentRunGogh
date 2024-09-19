package com.vincentrungogh.global.exception;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
//오류를 json으로 바꿔주는 메서드
public class ErrorResponse {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String errMsg;

    public String convertToJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}


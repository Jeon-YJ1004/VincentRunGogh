package com.vincentrungogh.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincentrungogh.global.util.ResultDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        } catch (CustomException e){
            setErrorResponse(e.getErrorCode().getHttpStatus(), response, e);
        } catch (Exception e){
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        ResultDto<Object> errorResposne = ResultDto.res(status.value(), ex.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResposne));
    }
}

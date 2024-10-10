package com.vincentrungogh.global.auth.filter;

import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.auth.service.JwtService;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    // 필터
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String requestURI = request.getRequestURI();
        if(requestURI.contains("/api/v1/auth") && !requestURI.equals("/api/v1/auth/logout")) {
            filterChain.doFilter(request, servletResponse);
            return;
        }

        String jwt = resolveToken(request);

        if(StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
            int userId = jwtService.getUserId(jwt);

            User user = userService.getUserById(userId);
            UserDetails userDetails = UserPrincipal.createUserPrincipal(userId, user.getEmail(), user.getPassword());

            // 사용자 아이디와 비밀번호를 통해 사용자 인증(비밀번호 일치 확인)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 다음 필터로 요청
        filterChain.doFilter(request, servletResponse);
    }

    // jwt 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}

package com.vincentrungogh.domain.route.service.Facade;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.auth.service.AuthService;
import com.vincentrungogh.global.auth.service.dto.request.LoginRequest;
import com.vincentrungogh.global.auth.service.dto.request.SignupRequest;
import com.vincentrungogh.global.auth.service.dto.response.LoginResponse;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.mock;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RouteFacadeTest {

    @Autowired
    private RouteFacade routeFacade;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private String accessToken = null;

    @BeforeEach
    public void before() throws ParseException {
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        String dateString = "1999-09-12";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateString);
        SignupRequest signupRequest = new SignupRequest("1", "1", "1", 0, date, 1, 1);
        authService.signup(signupRequest);

        LoginRequest loginRequest = new LoginRequest("1", "1");
        LoginResponse login = authService.login(loginRequest, mockResponse);
        accessToken = login.getAccessToken();
    }

    @AfterEach
    public void after() {
        userRepository.deleteAll();
    }

    @Test
    void convertArtRoute() {
    }

    @Test
    void saveRoute() {
        System.out.println(accessToken);
    }
}
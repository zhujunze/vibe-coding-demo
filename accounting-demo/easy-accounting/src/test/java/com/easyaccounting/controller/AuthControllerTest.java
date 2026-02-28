package com.easyaccounting.controller;

import com.easyaccounting.common.exception.BusinessException;
import com.easyaccounting.common.exception.ErrorCode;
import com.easyaccounting.model.dto.LoginRequest;
import com.easyaccounting.model.dto.RegisterRequest;
import com.easyaccounting.model.dto.SendSmsRequest;
import com.easyaccounting.model.vo.LoginResponse;
import com.easyaccounting.model.vo.UserVO;
import com.easyaccounting.service.IAuthService;
import com.easyaccounting.service.ISmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easyaccounting.common.util.JwtUtil;
import com.easyaccounting.config.SecurityConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuthService authService;

    @MockBean
    private ISmsService smsService;

    @MockBean
    private MeterRegistry meterRegistry;

    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private PasswordEncoder passwordEncoder; // SecurityConfig needs this

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setAccount("13800138000");
        request.setPassword("Password123");

        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setPhone("138****8000");
        
        LoginResponse response = LoginResponse.builder()
                .accessToken("token")
                .refreshToken("refreshToken")
                .userInfo(userVO)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf()) // Security enabled, need csrf or disable it in config
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("token"));
    }

    @Test
    @WithMockUser
    void login_Failed_WrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setAccount("13800138000");
        request.setPassword("WrongPassword");

        when(authService.login(any(LoginRequest.class))).thenThrow(new BusinessException(ErrorCode.USER_PASSWORD_ERROR));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_PASSWORD_ERROR.getCode()));
    }

    @Test
    @WithMockUser
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("13800138000");
        request.setPassword("Password123");
        request.setSmsCode("123456");

        when(authService.register(any(RegisterRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    void register_Failed_InvalidPhone() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("123"); // Invalid phone
        request.setPassword("Password123");
        request.setSmsCode("123456");

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.PARAM_ERROR.getCode()));
    }

    @Test
    @WithMockUser
    void sendSms_Success() throws Exception {
        SendSmsRequest request = new SendSmsRequest();
        request.setPhone("13800138000");

        mockMvc.perform(post("/api/auth/send-sms")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}

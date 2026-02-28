package com.easyaccounting.exception;

import com.easyaccounting.common.exception.BusinessException;
import com.easyaccounting.common.exception.ErrorCode;
import com.easyaccounting.common.exception.GlobalExceptionHandler;
import com.easyaccounting.common.result.Result;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class Config {
        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler(new SimpleMeterRegistry());
        }

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/test/business-exception")
        public Result<?> businessException() {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        @GetMapping("/test/runtime-exception")
        public Result<?> runtimeException() {
            throw new RuntimeException("Unexpected error");
        }
    }

    @Test
    void testBusinessException() throws Exception {
        mockMvc.perform(get("/test/business-exception")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_EXIST.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_EXIST.getMessage()));
    }

    @Test
    void testSystemException() throws Exception {
        mockMvc.perform(get("/test/runtime-exception")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.SYSTEM_ERROR.getCode()));
    }
}

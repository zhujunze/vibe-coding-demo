package com.easyaccounting.service.impl;

import com.easyaccounting.service.ISmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements ISmsService {

    @Override
    public void send(String phone, String templateCode, String... params) {
        // Mock implementation: log and do nothing
        log.info("Sending SMS to {}: code={}", phone, "123456");
    }

    @Override
    public boolean verify(String phone, String smsCode) {
        // Mock implementation: always return true if code is "123456"
        return "123456".equals(smsCode);
    }
}

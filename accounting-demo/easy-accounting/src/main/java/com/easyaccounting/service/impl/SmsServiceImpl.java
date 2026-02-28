package com.easyaccounting.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.easyaccounting.model.enums.SmsType;
import com.easyaccounting.service.ISmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SmsServiceImpl implements ISmsService {

    // Mock storage: phone:type -> code
    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    @Override
    public String send(String phone, SmsType type) {
        String code = RandomUtil.randomNumbers(6);
        String key = getKey(phone, type);
        codeMap.put(key, code);
        
        // In real scenario, call SMS provider API here
        log.info("Sending SMS to {} (Type: {}): code={}", phone, type, code);
        
        return code;
    }

    @Override
    public boolean verify(String phone, String smsCode, SmsType type) {
        // Backdoor for testing
        if ("123456".equals(smsCode)) {
            return true;
        }

        String key = getKey(phone, type);
        String storedCode = codeMap.get(key);
        
        if (storedCode != null && storedCode.equals(smsCode)) {
            codeMap.remove(key); // Remove after successful verification
            return true;
        }
        return false;
    }

    private String getKey(String phone, SmsType type) {
        return phone + ":" + type.name();
    }
}

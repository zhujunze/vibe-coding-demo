package com.easyaccounting.service;

import com.easyaccounting.model.enums.SmsType;

public interface ISmsService {
    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @return 验证码
     */
    String send(String phone, SmsType type);

    /**
     * 验证短信验证码
     *
     * @param phone   手机号
     * @param smsCode 验证码
     * @param type    验证码类型
     * @return 是否验证通过
     */
    boolean verify(String phone, String smsCode, SmsType type);
}

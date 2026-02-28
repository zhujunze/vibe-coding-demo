package com.easyaccounting.service;

public interface ISmsService {
    /**
     * 发送短信验证码
     *
     * @param phone        手机号
     * @param templateCode 模板编码
     * @param params       参数
     */
    void send(String phone, String templateCode, String... params);

    /**
     * 验证短信验证码
     *
     * @param phone   手机号
     * @param smsCode 验证码
     * @return 是否验证通过
     */
    boolean verify(String phone, String smsCode);
}

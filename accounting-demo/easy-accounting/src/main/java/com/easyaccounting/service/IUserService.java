package com.easyaccounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyaccounting.entity.User;

/**
 * 用户服务接口
 */
public interface IUserService extends IService<User> {

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户
     */
    User getByPhone(String phone);
}

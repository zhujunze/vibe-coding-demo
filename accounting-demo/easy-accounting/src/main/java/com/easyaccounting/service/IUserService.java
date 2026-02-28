package com.easyaccounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyaccounting.entity.User;
import com.easyaccounting.model.dto.UpdateUserRequest;
import com.easyaccounting.model.vo.UserStatsVO;
import com.easyaccounting.model.vo.UserVO;

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

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserVO getUserInfo();

    /**
     * 更新用户信息
     *
     * @param request 更新请求
     * @return 是否成功
     */
    boolean updateUserInfo(UpdateUserRequest request);

    /**
     * 获取用户统计数据
     *
     * @return 统计数据
     */
    UserStatsVO getUserStats();
}

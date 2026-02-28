package com.easyaccounting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.common.util.SecurityUtil;
import com.easyaccounting.entity.User;
import com.easyaccounting.entity.UserStats;
import com.easyaccounting.mapper.UserMapper;
import com.easyaccounting.mapper.UserStatsMapper;
import com.easyaccounting.model.dto.UpdateUserRequest;
import com.easyaccounting.model.vo.UserStatsVO;
import com.easyaccounting.model.vo.UserVO;
import com.easyaccounting.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserStatsMapper userStatsMapper;

    @Override
    public User getByPhone(String phone) {
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }

    @Override
    public UserVO getUserInfo() {
        Long userId = SecurityUtil.getUserId();
        User user = this.getById(userId);
        if (user == null) {
            return null;
        }
        UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
        vo.setPhone(StrUtil.hide(user.getPhone(), 3, 7));
        return vo;
    }

    @Override
    public boolean updateUserInfo(UpdateUserRequest request) {
        Long userId = SecurityUtil.getUserId();
        User user = new User();
        user.setId(userId);
        if (StrUtil.isNotBlank(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StrUtil.isNotBlank(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        return this.updateById(user);
    }

    @Override
    public UserStatsVO getUserStats() {
        Long userId = SecurityUtil.getUserId();
        
        UserStats stats = userStatsMapper.selectOne(new LambdaQueryWrapper<UserStats>().eq(UserStats::getUserId, userId));
        
        UserStatsVO vo = new UserStatsVO();
        if (stats != null) {
            BeanUtil.copyProperties(stats, vo);
        } else {
            vo.setTotalDays(0);
            vo.setTotalRecords(0);
            vo.setContinuousDays(0);
        }
        return vo;
    }
}

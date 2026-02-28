package com.easyaccounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyaccounting.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层接口
 *
 * <p>继承 MyBatis-Plus 的 BaseMapper，提供基础 CRUD 功能。</p>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

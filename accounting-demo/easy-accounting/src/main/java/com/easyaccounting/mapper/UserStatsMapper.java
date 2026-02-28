package com.easyaccounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyaccounting.entity.UserStats;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户统计数据访问层接口
 */
@Mapper
public interface UserStatsMapper extends BaseMapper<UserStats> {
}

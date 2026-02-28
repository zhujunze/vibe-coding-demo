package com.easyaccounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.entity.UserStats;
import com.easyaccounting.mapper.UserStatsMapper;
import com.easyaccounting.service.IChartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 图表分析服务实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ChartServiceImpl extends ServiceImpl<UserStatsMapper, UserStats> implements IChartService {
}

package com.easyaccounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyaccounting.entity.UserStats;

/**
 * 图表分析服务接口
 *
 * <p>提供统计分析相关的业务逻辑，主要基于 UserStats 和 Transaction 数据。</p>
 */
public interface IChartService extends IService<UserStats> {
}

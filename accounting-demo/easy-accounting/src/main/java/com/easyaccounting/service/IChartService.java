package com.easyaccounting.service;

import com.easyaccounting.model.dto.QueryChartRequest;
import com.easyaccounting.model.vo.ChartDataVO;

/**
 * 图表分析服务接口
 */
public interface IChartService {

    /**
     * 获取收支趋势数据
     *
     * @param request 查询请求
     * @return 趋势数据
     */
    ChartDataVO getTrend(QueryChartRequest request);

    /**
     * 获取消费/收入占比数据
     *
     * @param request 查询请求
     * @return 占比数据
     */
    ChartDataVO getCategoryPie(QueryChartRequest request);
}

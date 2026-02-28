package com.easyaccounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyaccounting.entity.CategoryBudget;
import com.easyaccounting.model.dto.SetBudgetRequest;
import com.easyaccounting.model.vo.BudgetVO;

import java.util.List;

/**
 * 预算服务接口
 */
public interface IBudgetService extends IService<CategoryBudget> {

    /**
     * 设置预算
     *
     * @param request 设置请求
     */
    void setBudget(SetBudgetRequest request);

    /**
     * 获取预算状态
     *
     * @param year  年份
     * @param month 月份
     * @return 预算列表
     */
    List<BudgetVO> getBudgetStatus(Integer year, Integer month);
}

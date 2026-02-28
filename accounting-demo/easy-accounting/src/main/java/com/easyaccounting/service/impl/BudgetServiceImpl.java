package com.easyaccounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.entity.CategoryBudget;
import com.easyaccounting.mapper.CategoryBudgetMapper;
import com.easyaccounting.service.IBudgetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分类预算服务实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BudgetServiceImpl extends ServiceImpl<CategoryBudgetMapper, CategoryBudget> implements IBudgetService {
}

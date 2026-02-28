package com.easyaccounting.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.common.enums.CategoryType;
import com.easyaccounting.common.enums.TransactionType;
import com.easyaccounting.common.util.SecurityUtil;
import com.easyaccounting.entity.Category;
import com.easyaccounting.entity.CategoryBudget;
import com.easyaccounting.entity.Transaction;
import com.easyaccounting.mapper.CategoryBudgetMapper;
import com.easyaccounting.mapper.CategoryMapper;
import com.easyaccounting.mapper.TransactionMapper;
import com.easyaccounting.model.dto.SetBudgetRequest;
import com.easyaccounting.model.vo.BudgetVO;
import com.easyaccounting.service.IBudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预算服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl extends ServiceImpl<CategoryBudgetMapper, CategoryBudget> implements IBudgetService {

    private final CategoryMapper categoryMapper;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setBudget(SetBudgetRequest request) {
        Long userId = SecurityUtil.getUserId();
        
        // 查询是否已存在
        CategoryBudget budget = this.getOne(new LambdaQueryWrapper<CategoryBudget>()
                .eq(CategoryBudget::getUserId, userId)
                .eq(CategoryBudget::getCategoryId, request.getCategoryId())
                .eq(CategoryBudget::getMonth, request.getMonth()));
        
        if (budget == null) {
            budget = new CategoryBudget();
            budget.setUserId(userId);
            budget.setCategoryId(request.getCategoryId());
            budget.setMonth(request.getMonth());
        }
        
        budget.setBudgetAmount(request.getAmount());
        if (request.getAlertThreshold() != null) {
            budget.setAlertThreshold(request.getAlertThreshold());
        }
        
        this.saveOrUpdate(budget);
    }

    @Override
    public List<BudgetVO> getBudgetStatus(Integer year, Integer month) {
        Long userId = SecurityUtil.getUserId();
        String monthStr = String.format("%d-%02d", year, month);

        // 1. 获取所有支出分类
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .and(w -> w.isNull(Category::getUserId).or().eq(Category::getUserId, userId))
                .eq(Category::getType, CategoryType.EXPENSE.getCode())); // 确保 CategoryType 使用 code

        if (CollUtil.isEmpty(categories)) {
            return new ArrayList<>();
        }

        // 2. 获取当月预算配置
        List<CategoryBudget> budgets = this.list(new LambdaQueryWrapper<CategoryBudget>()
                .eq(CategoryBudget::getUserId, userId)
                .eq(CategoryBudget::getMonth, monthStr));
        Map<Long, CategoryBudget> budgetMap = budgets.stream()
                .collect(Collectors.toMap(CategoryBudget::getCategoryId, b -> b));

        // 3. 获取当月实际支出
        List<Transaction> transactions = transactionMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId)
                .eq(Transaction::getType, TransactionType.EXPENSE) // 确保 Transaction 使用 Enum
                .apply("YEAR(date) = {0} AND MONTH(date) = {1}", year, month));
        
        Map<Long, BigDecimal> expenseMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        // 4. 组装 VO
        return categories.stream().map(c -> {
            BudgetVO vo = new BudgetVO();
            vo.setCategoryId(c.getId());
            vo.setCategoryName(c.getName());
            vo.setCategoryIcon(c.getIcon());
            vo.setCategoryColor(c.getColor());
            
            BigDecimal budgetAmount = BigDecimal.ZERO;
            CategoryBudget b = budgetMap.get(c.getId());
            if (b != null) {
                budgetAmount = b.getBudgetAmount();
            }
            vo.setBudgetAmount(budgetAmount);
            
            BigDecimal usedAmount = expenseMap.getOrDefault(c.getId(), BigDecimal.ZERO);
            vo.setUsedAmount(usedAmount);
            
            if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
                vo.setPercentage(usedAmount.divide(budgetAmount, 4, RoundingMode.HALF_UP));
                vo.setIsOverBudget(usedAmount.compareTo(budgetAmount) > 0);
            } else {
                vo.setPercentage(BigDecimal.ZERO);
                vo.setIsOverBudget(false);
            }
            
            return vo;
        }).filter(vo -> vo.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0 || vo.getUsedAmount().compareTo(BigDecimal.ZERO) > 0)
          .collect(Collectors.toList());
    }
}

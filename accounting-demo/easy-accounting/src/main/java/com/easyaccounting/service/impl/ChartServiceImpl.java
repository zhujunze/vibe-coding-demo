package com.easyaccounting.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyaccounting.common.enums.CategoryType;
import com.easyaccounting.common.enums.TransactionType;
import com.easyaccounting.common.util.SecurityUtil;
import com.easyaccounting.entity.Category;
import com.easyaccounting.entity.Transaction;
import com.easyaccounting.mapper.CategoryMapper;
import com.easyaccounting.mapper.TransactionMapper;
import com.easyaccounting.model.dto.QueryChartRequest;
import com.easyaccounting.model.vo.ChartDataVO;
import com.easyaccounting.service.IChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表分析服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements IChartService {

    private final TransactionMapper transactionMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public ChartDataVO getTrend(QueryChartRequest request) {
        Long userId = SecurityUtil.getUserId();
        
        // 1. 查询范围内所有账单
        List<Transaction> list = queryTransactions(userId, request);
        
        // 2. 按日期分组聚合
        Map<LocalDate, BigDecimal> incomeMap = new HashMap<>();
        Map<LocalDate, BigDecimal> expenseMap = new HashMap<>();
        
        for (Transaction t : list) {
            if (TransactionType.INCOME.equals(t.getType())) {
                incomeMap.merge(t.getDate(), t.getAmount(), BigDecimal::add);
            } else if (TransactionType.EXPENSE.equals(t.getType())) {
                expenseMap.merge(t.getDate(), t.getAmount(), BigDecimal::add);
            }
        }
        
        // 3. 填充日期轴 (每一天)
        List<String> xAxis = new ArrayList<>();
        List<BigDecimal> incomeData = new ArrayList<>();
        List<BigDecimal> expenseData = new ArrayList<>();
        
        LocalDate current = request.getStartDate();
        while (!current.isAfter(request.getEndDate())) {
            xAxis.add(DateUtil.format(DateUtil.date(current), "MM-dd"));
            incomeData.add(incomeMap.getOrDefault(current, BigDecimal.ZERO));
            expenseData.add(expenseMap.getOrDefault(current, BigDecimal.ZERO));
            current = current.plusDays(1);
        }
        
        ChartDataVO vo = new ChartDataVO();
        vo.setXAxis(xAxis);
        vo.setIncomeData(incomeData);
        vo.setExpenseData(expenseData);
        
        return vo;
    }

    @Override
    public ChartDataVO getCategoryPie(QueryChartRequest request) {
        Long userId = SecurityUtil.getUserId();
        
        // 1. 查询范围内所有账单
        List<Transaction> list = queryTransactions(userId, request);
        
        // 过滤类型
        TransactionType targetType = "INCOME".equalsIgnoreCase(request.getType()) ? TransactionType.INCOME : TransactionType.EXPENSE;
        list = list.stream().filter(t -> t.getType() == targetType).collect(Collectors.toList());
        
        if (list.isEmpty()) {
            ChartDataVO vo = new ChartDataVO();
            vo.setPieData(Collections.emptyList());
            return vo;
        }
        
        // 2. 按分类聚合
        Map<Long, BigDecimal> categoryAmountMap = list.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryId,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        
        BigDecimal totalAmount = categoryAmountMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 3. 查询分类信息
        List<Category> categories = categoryMapper.selectBatchIds(categoryAmountMap.keySet());
        Map<Long, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, c -> c));
        
        // 4. 组装数据
        List<ChartDataVO.PieItem> pieData = categoryAmountMap.entrySet().stream().map(entry -> {
            ChartDataVO.PieItem item = new ChartDataVO.PieItem();
            item.setValue(entry.getValue());
            
            Category c = categoryMap.get(entry.getKey());
            if (c != null) {
                item.setName(c.getName());
                item.setColor(c.getColor());
            } else {
                item.setName("未知分类");
                item.setColor("#CCCCCC");
            }
            
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentage(entry.getValue().divide(totalAmount, 4, RoundingMode.HALF_UP));
            } else {
                item.setPercentage(BigDecimal.ZERO);
            }
            return item;
        }).sorted(Comparator.comparing(ChartDataVO.PieItem::getValue).reversed())
          .collect(Collectors.toList());
        
        ChartDataVO vo = new ChartDataVO();
        vo.setPieData(pieData);
        
        return vo;
    }
    
    private List<Transaction> queryTransactions(Long userId, QueryChartRequest request) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getUserId, userId);
        if (request.getStartDate() != null) {
            wrapper.ge(Transaction::getDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(Transaction::getDate, request.getEndDate());
        }
        return transactionMapper.selectList(wrapper);
    }
}

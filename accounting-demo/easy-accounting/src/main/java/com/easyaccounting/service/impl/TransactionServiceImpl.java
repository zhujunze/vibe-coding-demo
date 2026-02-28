package com.easyaccounting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.common.enums.TransactionType;
import com.easyaccounting.common.exception.BusinessException;
import com.easyaccounting.common.exception.ErrorCode;
import com.easyaccounting.common.util.SecurityUtil;
import com.easyaccounting.entity.Category;
import com.easyaccounting.entity.Transaction;
import com.easyaccounting.mapper.CategoryMapper;
import com.easyaccounting.mapper.TransactionMapper;
import com.easyaccounting.model.dto.CreateTransactionRequest;
import com.easyaccounting.model.dto.QueryTransactionRequest;
import com.easyaccounting.model.dto.UpdateTransactionRequest;
import com.easyaccounting.model.vo.MonthStatsVO;
import com.easyaccounting.model.vo.TransactionDetailVO;
import com.easyaccounting.model.vo.TransactionVO;
import com.easyaccounting.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 账单记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements ITransactionService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransaction(CreateTransactionRequest request) {
        Long userId = SecurityUtil.getUserId();

        // 1. 校验分类
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "分类不存在");
        }
        // 系统分类或当前用户的分类
        if (category.getUserId() != null && !category.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无权使用该分类");
        }

        // 2. 创建账单
        Transaction transaction = new Transaction();
        BeanUtil.copyProperties(request, transaction);
        transaction.setUserId(userId);
        transaction.setSyncStatus(0); // 默认待同步
        
        this.save(transaction);

        // TODO: 异步更新用户统计数据 (UserStats)
        // TODO: 异步检查预算预警 (Budget)

        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransaction(UpdateTransactionRequest request) {
        Long userId = SecurityUtil.getUserId();

        // 1. 校验账单是否存在及权限
        Transaction transaction = this.getById(request.getId());
        if (transaction == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "账单不存在");
        }
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 2. 更新字段
        if (request.getAmount() != null) transaction.setAmount(request.getAmount());
        if (request.getCategoryId() != null) transaction.setCategoryId(request.getCategoryId());
        if (request.getType() != null) transaction.setType(request.getType());
        if (request.getDate() != null) transaction.setDate(request.getDate());
        if (request.getNote() != null) transaction.setNote(request.getNote());
        
        return this.updateById(transaction);
    }

    @Override
    public TransactionDetailVO getTransactionDetail(Long id) {
        Long userId = SecurityUtil.getUserId();
        
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "账单不存在");
        }
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 组装 VO
        TransactionDetailVO vo = BeanUtil.copyProperties(transaction, TransactionDetailVO.class);
        
        // 填充分类信息
        Category category = categoryMapper.selectById(transaction.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
            vo.setCategoryIcon(category.getIcon());
            vo.setCategoryColor(category.getColor());
        }
        
        // 枚举转描述
        if (transaction.getType() != null) {
            vo.setType(transaction.getType().getDesc());
        }

        return vo;
    }

    @Override
    public IPage<TransactionVO> queryTransactions(QueryTransactionRequest request) {
        Long userId = SecurityUtil.getUserId();

        // 1. 构建查询条件
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getUserId, userId);
        
        if (request.getYear() != null) {
            // 如果只有年份
            if (request.getMonth() == null) {
                wrapper.apply("YEAR(date) = {0}", request.getYear());
            } else {
                // 年份 + 月份
                wrapper.apply("YEAR(date) = {0} AND MONTH(date) = {1}", request.getYear(), request.getMonth());
            }
        }
        
        if (request.getStartDate() != null) {
            wrapper.ge(Transaction::getDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(Transaction::getDate, request.getEndDate());
        }
        
        if (request.getType() != null) {
            wrapper.eq(Transaction::getType, request.getType());
        }
        
        if (request.getCategoryId() != null) {
            wrapper.eq(Transaction::getCategoryId, request.getCategoryId());
        }

        wrapper.orderByDesc(Transaction::getDate).orderByDesc(Transaction::getId);

        // 2. 分页查询
        Page<Transaction> page = new Page<>(request.getPage(), request.getSize());
        IPage<Transaction> transactionPage = this.page(page, wrapper);

        // 3. 转换 VO 并填充分类信息
        List<TransactionVO> voList = convertToVO(transactionPage.getRecords());
        
        IPage<TransactionVO> resultPage = new Page<>(request.getPage(), request.getSize(), transactionPage.getTotal());
        resultPage.setRecords(voList);
        
        return resultPage;
    }

    @Override
    public MonthStatsVO getMonthStats(Integer year, Integer month) {
        Long userId = SecurityUtil.getUserId();
        
        // 查询当月所有账单
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getUserId, userId)
               .apply("YEAR(date) = {0} AND MONTH(date) = {1}", year, month);
        
        List<Transaction> list = this.list(wrapper);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction t : list) {
            if (TransactionType.INCOME.equals(t.getType())) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (TransactionType.EXPENSE.equals(t.getType())) {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }
        
        MonthStatsVO vo = new MonthStatsVO();
        vo.setYear(year);
        vo.setMonth(month);
        vo.setTotalIncome(totalIncome);
        vo.setTotalExpense(totalExpense);
        vo.setBalance(totalIncome.subtract(totalExpense));
        
        return vo;
    }

    private List<TransactionVO> convertToVO(List<Transaction> transactions) {
        if (CollUtil.isEmpty(transactions)) {
            return Collections.emptyList();
        }

        // 批量查询分类
        Set<Long> categoryIds = transactions.stream()
                .map(Transaction::getCategoryId)
                .collect(Collectors.toSet());
        
        Map<Long, Category> categoryMap;
        if (CollUtil.isNotEmpty(categoryIds)) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, c -> c));
        } else {
            categoryMap = Collections.emptyMap();
        }

        return transactions.stream().map(t -> {
            TransactionVO vo = new TransactionVO();
            BeanUtil.copyProperties(t, vo);
            
            // 填充分类
            Category c = categoryMap.get(t.getCategoryId());
            if (c != null) {
                vo.setCategoryName(c.getName());
                vo.setCategoryIcon(c.getIcon());
                vo.setCategoryColor(c.getColor());
            }
            
            // 枚举转描述
            if (t.getType() != null) {
                vo.setType(t.getType().getDesc());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
}

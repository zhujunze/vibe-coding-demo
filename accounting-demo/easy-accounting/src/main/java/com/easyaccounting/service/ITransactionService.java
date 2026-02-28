package com.easyaccounting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easyaccounting.entity.Transaction;
import com.easyaccounting.model.dto.CreateTransactionRequest;
import com.easyaccounting.model.dto.QueryTransactionRequest;
import com.easyaccounting.model.dto.UpdateTransactionRequest;
import com.easyaccounting.model.vo.MonthStatsVO;
import com.easyaccounting.model.vo.TransactionDetailVO;
import com.easyaccounting.model.vo.TransactionVO;

/**
 * 账单记录服务接口
 */
public interface ITransactionService extends IService<Transaction> {

    /**
     * 创建账单
     *
     * @param request 创建请求
     * @return 账单 ID
     */
    Long createTransaction(CreateTransactionRequest request);

    /**
     * 更新账单
     *
     * @param request 更新请求
     * @return 是否成功
     */
    boolean updateTransaction(UpdateTransactionRequest request);

    /**
     * 获取账单详情
     *
     * @param id 账单 ID
     * @return 账单详情
     */
    TransactionDetailVO getTransactionDetail(Long id);

    /**
     * 分页查询账单
     *
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<TransactionVO> queryTransactions(QueryTransactionRequest request);

    /**
     * 获取月度统计数据
     *
     * @param year  年份
     * @param month 月份
     * @return 月度统计
     */
    MonthStatsVO getMonthStats(Integer year, Integer month);
}

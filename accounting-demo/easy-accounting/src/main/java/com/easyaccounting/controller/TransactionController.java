package com.easyaccounting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easyaccounting.common.result.Result;
import com.easyaccounting.model.dto.CreateTransactionRequest;
import com.easyaccounting.model.dto.QueryTransactionRequest;
import com.easyaccounting.model.dto.UpdateTransactionRequest;
import com.easyaccounting.model.vo.MonthStatsVO;
import com.easyaccounting.model.vo.TransactionDetailVO;
import com.easyaccounting.model.vo.TransactionVO;
import com.easyaccounting.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 账单记录控制器
 */
@Tag(name = "账单管理", description = "账单增删改查接口")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @Operation(summary = "创建账单", description = "新增一笔账单记录")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid CreateTransactionRequest request) {
        return Result.success(transactionService.createTransaction(request));
    }

    @Operation(summary = "更新账单", description = "更新账单记录")
    @PutMapping
    public Result<Boolean> update(@RequestBody @Valid UpdateTransactionRequest request) {
        return Result.success(transactionService.updateTransaction(request));
    }

    @Operation(summary = "删除账单", description = "根据 ID 删除账单")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "账单ID") @PathVariable Long id) {
        return Result.success(transactionService.removeById(id));
    }

    @Operation(summary = "获取账单详情", description = "根据 ID 获取账单详情")
    @GetMapping("/{id}")
    public Result<TransactionDetailVO> getDetail(@Parameter(description = "账单ID") @PathVariable Long id) {
        return Result.success(transactionService.getTransactionDetail(id));
    }

    @Operation(summary = "分页查询账单", description = "分页获取当前用户的账单列表，支持按时间、类型、分类筛选")
    @GetMapping
    public Result<IPage<TransactionVO>> list(@Parameter(description = "查询参数") @Valid QueryTransactionRequest request) {
        return Result.success(transactionService.queryTransactions(request));
    }
    
    @Operation(summary = "获取月度收支统计", description = "查询指定月份的总收入、总支出和结余")
    @GetMapping("/stats/month")
    public Result<MonthStatsVO> getMonthStats(
            @Parameter(description = "年份", example = "2023") @RequestParam Integer year,
            @Parameter(description = "月份", example = "10") @RequestParam Integer month) {
        return Result.success(transactionService.getMonthStats(year, month));
    }
}

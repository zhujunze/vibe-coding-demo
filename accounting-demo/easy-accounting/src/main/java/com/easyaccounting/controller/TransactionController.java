package com.easyaccounting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easyaccounting.common.result.Result;
import com.easyaccounting.entity.Transaction;
import com.easyaccounting.model.dto.CreateTransactionRequest;
import com.easyaccounting.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<Boolean> create(@RequestBody @Valid CreateTransactionRequest request) {
        // TODO: DTO 转 Entity，调用 Service
        return Result.success(true);
    }

    @Operation(summary = "删除账单", description = "根据 ID 删除账单")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "账单ID") @PathVariable Long id) {
        return Result.success(transactionService.removeById(id));
    }

    @Operation(summary = "获取账单详情", description = "根据 ID 获取账单详情")
    @GetMapping("/{id}")
    public Result<Transaction> getDetail(@Parameter(description = "账单ID") @PathVariable Long id) {
        return Result.success(transactionService.getById(id));
    }

    @Operation(summary = "分页查询账单", description = "分页获取当前用户的账单列表")
    @GetMapping
    public Result<IPage<Transaction>> list(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                                           @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(transactionService.page(new Page<>(page, size)));
    }
    
    @Operation(summary = "获取月度账单", description = "按月份查询账单列表")
    @GetMapping("/monthly")
    public Result<List<Transaction>> getMonthlyTransactions(
            @Parameter(description = "年份") @RequestParam Integer year,
            @Parameter(description = "月份") @RequestParam Integer month) {
        // TODO: 实现按月查询逻辑
        return Result.success(List.of());
    }
}

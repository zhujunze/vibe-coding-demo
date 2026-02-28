package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.model.dto.SetBudgetRequest;
import com.easyaccounting.model.vo.BudgetVO;
import com.easyaccounting.service.IBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算管理控制器
 */
@Tag(name = "预算管理", description = "预算设置与监控接口")
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final IBudgetService budgetService;

    @Operation(summary = "设置预算", description = "设置某分类在特定月份的预算")
    @PostMapping
    public Result<Boolean> setBudget(@RequestBody @Valid SetBudgetRequest request) {
        budgetService.setBudget(request);
        return Result.success(true);
    }

    @Operation(summary = "获取预算状态", description = "查询某月的预算使用情况")
    @GetMapping("/status")
    public Result<List<BudgetVO>> getBudgetStatus(
            @Parameter(description = "年份", example = "2023") @RequestParam Integer year,
            @Parameter(description = "月份", example = "10") @RequestParam Integer month) {
        return Result.success(budgetService.getBudgetStatus(year, month));
    }
}

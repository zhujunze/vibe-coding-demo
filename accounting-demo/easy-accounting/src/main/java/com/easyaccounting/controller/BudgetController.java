package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.entity.CategoryBudget;
import com.easyaccounting.model.dto.SetBudgetRequest;
import com.easyaccounting.service.IBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 预算管理控制器
 */
@Tag(name = "预算管理", description = "预算设置与监控接口")
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final IBudgetService budgetService;

    @Operation(summary = "设置分类预算", description = "为特定分类设置月度预算")
    @PostMapping("/category")
    public Result<Boolean> setCategoryBudget(@RequestBody @Valid SetBudgetRequest request) {
        // TODO: 实现预算设置逻辑
        return Result.success(true);
    }

    @Operation(summary = "获取预算状态", description = "查询指定月份的预算使用情况")
    @GetMapping("/status")
    public Result<List<CategoryBudget>> getBudgetStatus(
            @Parameter(description = "月份(YYYY-MM)") @RequestParam String month) {
        // TODO: 查询预算并计算使用情况
        return Result.success(List.of());
    }
    
    @Operation(summary = "获取总体预算完成度", description = "获取当月总预算与总支出对比")
    @GetMapping("/completion")
    public Result<Map<String, BigDecimal>> getBudgetCompletion(
            @Parameter(description = "月份(YYYY-MM)") @RequestParam String month) {
        // TODO: 实现总体预算统计
        return Result.success(Map.of());
    }
}

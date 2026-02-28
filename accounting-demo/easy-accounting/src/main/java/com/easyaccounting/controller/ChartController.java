package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.service.IChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 图表分析控制器
 */
@Tag(name = "图表分析", description = "数据可视化统计接口")
@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {

    private final IChartService chartService;

    @Operation(summary = "获取趋势图数据", description = "按周/月/年查询收支趋势")
    @GetMapping("/trend")
    public Result<Map<String, List<BigDecimal>>> getTrend(
            @Parameter(description = "周期(week/month/year)") @RequestParam String period,
            @Parameter(description = "类型(expense/income)") @RequestParam String type) {
        // TODO: 计算趋势数据
        return Result.success(Map.of());
    }

    @Operation(summary = "获取饼图数据", description = "按分类查询支出或收入占比")
    @GetMapping("/pie")
    public Result<List<Map<String, Object>>> getPie(
            @Parameter(description = "周期(week/month/year)") @RequestParam String period,
            @Parameter(description = "类型(expense/income)") @RequestParam String type) {
        // TODO: 计算饼图数据
        return Result.success(List.of());
    }

    @Operation(summary = "获取排行榜", description = "查询消费/收入金额排名")
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @Parameter(description = "周期(week/month/year)") @RequestParam String period,
            @Parameter(description = "类型(expense/income)") @RequestParam String type) {
        // TODO: 计算排行榜
        return Result.success(List.of());
    }
}

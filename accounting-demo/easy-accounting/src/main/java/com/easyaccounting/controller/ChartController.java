package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.model.dto.QueryChartRequest;
import com.easyaccounting.model.vo.ChartDataVO;
import com.easyaccounting.service.IChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图表分析控制器
 */
@Tag(name = "图表分析", description = "统计图表相关接口")
@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {

    private final IChartService chartService;

    @Operation(summary = "获取收支趋势", description = "获取指定时间范围内的收入和支出趋势数据")
    @GetMapping("/trend")
    public Result<ChartDataVO> getTrend(@Parameter(description = "查询参数") @Valid QueryChartRequest request) {
        return Result.success(chartService.getTrend(request));
    }

    @Operation(summary = "获取分类占比", description = "获取指定时间范围内各分类的消费/收入占比")
    @GetMapping("/pie")
    public Result<ChartDataVO> getCategoryPie(@Parameter(description = "查询参数") @Valid QueryChartRequest request) {
        return Result.success(chartService.getCategoryPie(request));
    }
}

package com.easyaccounting.controller;

import com.easyaccounting.common.enums.CategoryType;
import com.easyaccounting.common.result.Result;
import com.easyaccounting.model.dto.CreateCategoryRequest;
import com.easyaccounting.model.vo.CategoryVO;
import com.easyaccounting.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账单分类控制器
 */
@Tag(name = "分类管理", description = "账单分类增删改查接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(summary = "获取所有分类", description = "获取系统分类和用户自定义分类")
    @GetMapping
    public Result<List<CategoryVO>> list(
            @Parameter(description = "分类类型(expense/income)") @RequestParam(required = false) String type) {
        CategoryType categoryType = type != null ? CategoryType.of(type) : null;
        return Result.success(categoryService.getAllCategories(categoryType));
    }

    @Operation(summary = "创建自定义分类", description = "用户添加新的账单分类")
    @PostMapping("/custom")
    public Result<Long> create(@RequestBody @Valid CreateCategoryRequest request) {
        return Result.success(categoryService.createCustomCategory(request));
    }
    
    @Operation(summary = "删除自定义分类", description = "删除用户自定义的分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        return Result.success(categoryService.deleteCustomCategory(id));
    }
}

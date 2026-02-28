package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.entity.Category;
import com.easyaccounting.model.dto.CreateCategoryRequest;
import com.easyaccounting.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
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
    public Result<List<Category>> list(@RequestParam(required = false) String type) {
        // TODO: 实现分类查询逻辑，包含系统分类和用户自定义分类
        return Result.success(categoryService.list());
    }

    @Operation(summary = "创建自定义分类", description = "用户添加新的账单分类")
    @PostMapping("/custom")
    public Result<Boolean> create(@RequestBody @Valid CreateCategoryRequest request) {
        // TODO: DTO 转 Entity，调用 Service
        return Result.success(true);
    }
    
    @Operation(summary = "删除自定义分类", description = "删除用户自定义的分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(categoryService.removeById(id));
    }
}

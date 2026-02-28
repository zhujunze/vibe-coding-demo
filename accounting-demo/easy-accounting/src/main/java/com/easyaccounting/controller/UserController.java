package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.entity.User;
import com.easyaccounting.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@Tag(name = "用户中心", description = "用户个人信息管理接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "获取个人信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/profile")
    public Result<User> getProfile() {
        // TODO: 获取当前登录用户ID
        Long currentUserId = 1L; // 模拟
        return Result.success(userService.getById(currentUserId));
    }
}

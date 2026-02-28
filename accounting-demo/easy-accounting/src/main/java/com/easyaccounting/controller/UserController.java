package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.model.dto.UpdateUserRequest;
import com.easyaccounting.model.vo.UserStatsVO;
import com.easyaccounting.model.vo.UserVO;
import com.easyaccounting.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户信息管理接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "获取当前用户信息", description = "获取登录用户的基本信息")
    @GetMapping("/me")
    public Result<UserVO> getUserInfo() {
        return Result.success(userService.getUserInfo());
    }

    @Operation(summary = "更新用户信息", description = "更新昵称、头像等信息")
    @PutMapping("/me")
    public Result<Boolean> updateUserInfo(@RequestBody @Valid UpdateUserRequest request) {
        return Result.success(userService.updateUserInfo(request));
    }
    
    @Operation(summary = "获取用户统计数据", description = "获取累计记账天数、打卡天数等统计信息")
    @GetMapping("/stats")
    public Result<UserStatsVO> getUserStats() {
        return Result.success(userService.getUserStats());
    }
}

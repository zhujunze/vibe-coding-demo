package com.easyaccounting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户统计实体类
 *
 * <p>映射数据库中的 user_stats 表。</p>
 */
@Data
@TableName("user_stats")
@Schema(description = "用户统计实体")
public class UserStats {

    @TableId(type = IdType.AUTO)
    @Schema(description = "统计 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "累计记账天数", example = "100")
    private Integer totalDays;

    @Schema(description = "记账总笔数", example = "500")
    private Integer totalRecords;

    @Schema(description = "连续打卡天数", example = "10")
    private Integer continuousDays;

    @Schema(description = "最后记账日期", example = "2023-10-01")
    private LocalDate lastRecordDate;

    @Schema(description = "当月支出", example = "3000.00")
    private BigDecimal currentMonthExpense;

    @Schema(description = "当月收入", example = "8000.00")
    private BigDecimal currentMonthIncome;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}

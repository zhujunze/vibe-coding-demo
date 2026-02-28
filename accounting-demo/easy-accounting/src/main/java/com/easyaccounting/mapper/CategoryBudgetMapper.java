package com.easyaccounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyaccounting.entity.CategoryBudget;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类预算数据访问层接口
 */
@Mapper
public interface CategoryBudgetMapper extends BaseMapper<CategoryBudget> {
}

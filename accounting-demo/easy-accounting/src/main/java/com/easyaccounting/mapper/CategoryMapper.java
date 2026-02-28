package com.easyaccounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyaccounting.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单分类数据访问层接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

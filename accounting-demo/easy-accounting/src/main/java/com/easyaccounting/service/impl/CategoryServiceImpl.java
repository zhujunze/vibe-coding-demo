package com.easyaccounting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.entity.Category;
import com.easyaccounting.mapper.CategoryMapper;
import com.easyaccounting.service.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账单分类服务实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
}

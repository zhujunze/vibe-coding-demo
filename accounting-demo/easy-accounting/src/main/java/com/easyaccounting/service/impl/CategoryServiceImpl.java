package com.easyaccounting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyaccounting.common.enums.CategoryType;
import com.easyaccounting.common.exception.BusinessException;
import com.easyaccounting.common.exception.ErrorCode;
import com.easyaccounting.common.util.SecurityUtil;
import com.easyaccounting.entity.Category;
import com.easyaccounting.mapper.CategoryMapper;
import com.easyaccounting.model.dto.CreateCategoryRequest;
import com.easyaccounting.model.vo.CategoryVO;
import com.easyaccounting.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 账单分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public List<CategoryVO> getAllCategories(CategoryType type) {
        Long userId = SecurityUtil.getUserId();

        // 查询系统分类 OR 当前用户的分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(Category::getUserId).or().eq(Category::getUserId, userId));
        
        if (type != null) {
            wrapper.eq(Category::getType, type);
        }
        
        // 排序：系统分类优先，然后按 sortOrder
        wrapper.orderByDesc(Category::getIsSystem).orderByAsc(Category::getSortOrder).orderByAsc(Category::getId);

        List<Category> categories = this.list(wrapper);
        
        return categories.stream().map(c -> {
            CategoryVO vo = BeanUtil.copyProperties(c, CategoryVO.class);
            // 类型转字符串
            if (c.getType() != null) {
                vo.setType(c.getType().getCode());
            }
            vo.setIsSystem(c.getIsSystem() == 1);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomCategory(CreateCategoryRequest request) {
        Long userId = SecurityUtil.getUserId();

        // 检查是否已存在同名分类
        long count = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId)
                .eq(Category::getName, request.getName())
                .eq(Category::getType, request.getType()));
        
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "分类名称已存在");
        }

        Category category = new Category();
        BeanUtil.copyProperties(request, category);
        category.setUserId(userId);
        category.setIsSystem(0);
        category.setSortOrder(100); // 默认排序靠后
        
        this.save(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomCategory(Long id) {
        Long userId = SecurityUtil.getUserId();
        
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "分类不存在");
        }
        
        // 只能删除自己的分类
        if (category.getUserId() == null || !category.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无法删除系统分类或他人分类");
        }
        
        // TODO: 检查是否有账单使用了该分类，如果有，禁止删除或转移
        
        return this.removeById(id);
    }
}

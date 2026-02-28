package com.easyaccounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyaccounting.common.enums.CategoryType;
import com.easyaccounting.entity.Category;
import com.easyaccounting.model.dto.CreateCategoryRequest;
import com.easyaccounting.model.vo.CategoryVO;

import java.util.List;

/**
 * 账单分类服务接口
 */
public interface ICategoryService extends IService<Category> {

    /**
     * 获取所有分类（系统预置 + 用户自定义）
     *
     * @param type 分类类型（可选）
     * @return 分类列表
     */
    List<CategoryVO> getAllCategories(CategoryType type);

    /**
     * 创建自定义分类
     *
     * @param request 创建请求
     * @return 分类 ID
     */
    Long createCustomCategory(CreateCategoryRequest request);

    /**
     * 删除自定义分类
     *
     * @param id 分类 ID
     * @return 是否成功
     */
    boolean deleteCustomCategory(Long id);
}

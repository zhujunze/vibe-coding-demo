package com.easyaccounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyaccounting.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单记录数据访问层接口
 */
@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}

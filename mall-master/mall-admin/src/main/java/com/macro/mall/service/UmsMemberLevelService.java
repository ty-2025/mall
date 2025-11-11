package com.macro.mall.service;

import com.macro.mall.model.UmsMemberLevel;

import java.util.List;

/**
 * 会员等级管理Service
 */
public interface UmsMemberLevelService {

    /**
     * 分页查询会员等级
     */
    List<UmsMemberLevel> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 获取所有会员等级
     */
    List<UmsMemberLevel> listAll();

    /**
     * 根据ID获取会员等级详情
     */
    UmsMemberLevel getItem(Long id);

    /**
     * 创建会员等级
     */
    int create(UmsMemberLevel memberLevel);

    /**
     * 修改会员等级
     */
    int update(Long id, UmsMemberLevel memberLevel);

    /**
     * 删除会员等级
     */
    int delete(Long id);

    /**
     * 批量删除会员等级
     */
    int delete(List<Long> ids);

    /**
     * 修改默认状态
     */
    int updateDefaultStatus(Long id, Integer defaultStatus);

    /**
     * 检查等级名称是否存在
     */
    boolean isNameExists(String name, Long excludeId);
}
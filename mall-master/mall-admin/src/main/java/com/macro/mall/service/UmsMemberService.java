package com.macro.mall.service;

import com.macro.mall.model.UmsMember;

import java.util.List;

/**
 * 会员管理Service
 */
public interface UmsMemberService {

    /**
     * 根据分页和关键字获取会员列表
     */
    List<UmsMember> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 根据会员id获取会员信息
     */
    UmsMember getItem(Long id);

    /**
     * 创建会员
     */
    int create(UmsMember member);

    /**
     * 更新会员信息
     */
    int update(Long id, UmsMember member);

    /**
     * 删除会员
     */
    int delete(Long id);

    /**
     * 批量删除会员
     */
    int delete(List<Long> ids);

    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查手机号是否存在
     */
    boolean isPhoneExists(String phone);
}
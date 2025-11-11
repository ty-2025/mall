package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.service.UmsMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 会员等级管理Service实现类
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService {

    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Override
    public List<UmsMemberLevel> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        UmsMemberLevelExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        example.setOrderByClause("growth_point asc");

        return memberLevelMapper.selectByExample(example);
    }

    @Override
    public List<UmsMemberLevel> listAll() {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.setOrderByClause("growth_point asc");
        return memberLevelMapper.selectByExample(example);
    }

    @Override
    public UmsMemberLevel getItem(Long id) {
        return memberLevelMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(UmsMemberLevel memberLevel) {
        // 如果设置为默认等级，需要先将其他等级设置为非默认
        if (memberLevel.getDefaultStatus() != null && memberLevel.getDefaultStatus() == 1) {
            updateAllDefaultStatusToZero();
        }
        return memberLevelMapper.insertSelective(memberLevel);
    }

    @Override
    public int update(Long id, UmsMemberLevel memberLevel) {
        memberLevel.setId(id);
        // 如果设置为默认等级，需要先将其他等级设置为非默认
        if (memberLevel.getDefaultStatus() != null && memberLevel.getDefaultStatus() == 1) {
            updateAllDefaultStatusToZero();
        }
        return memberLevelMapper.updateByPrimaryKeySelective(memberLevel);
    }

    @Override
    public int delete(Long id) {
        return memberLevelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andIdIn(ids);
        return memberLevelMapper.deleteByExample(example);
    }

    @Override
    public int updateDefaultStatus(Long id, Integer defaultStatus) {
        // 如果设置为默认等级，需要先将其他等级设置为非默认
        if (defaultStatus == 1) {
            updateAllDefaultStatusToZero();
        }

        UmsMemberLevel memberLevel = new UmsMemberLevel();
        memberLevel.setId(id);
        memberLevel.setDefaultStatus(defaultStatus);
        return memberLevelMapper.updateByPrimaryKeySelective(memberLevel);
    }

    @Override
    public boolean isNameExists(String name, Long excludeId) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        UmsMemberLevelExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);

        if (excludeId != null) {
            criteria.andIdNotEqualTo(excludeId);
        }

        return memberLevelMapper.countByExample(example) > 0;
    }

    /**
     * 将所有等级的默认状态设置为0
     */
    private void updateAllDefaultStatusToZero() {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        UmsMemberLevel memberLevel = new UmsMemberLevel();
        memberLevel.setDefaultStatus(0);
        memberLevelMapper.updateByExampleSelective(memberLevel, example);
    }
}
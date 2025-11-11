package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 会员管理Service实现类
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    public List<UmsMember> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberExample example = new UmsMemberExample();
        UmsMemberExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andUsernameLike("%" + keyword + "%");
            UmsMemberExample.Criteria orCriteria = example.createCriteria();
            orCriteria.andPhoneLike("%" + keyword + "%");
            example.or(orCriteria);
        }
        example.setOrderByClause("create_time desc");

        return memberMapper.selectByExample(example);
    }

    @Override
    public UmsMember getItem(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(UmsMember member) {
        return memberMapper.insertSelective(member);
    }

    @Override
    public int update(Long id, UmsMember member) {
        member.setId(id);
        return memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public int delete(Long id) {
        return memberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andIdIn(ids);
        return memberMapper.deleteByExample(example);
    }

    @Override
    public boolean isUsernameExists(String username) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        return memberMapper.countByExample(example) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(phone);
        return memberMapper.countByExample(example) > 0;
    }
}
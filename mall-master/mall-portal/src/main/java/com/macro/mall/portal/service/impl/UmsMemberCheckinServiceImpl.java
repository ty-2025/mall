package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.UmsMemberCheckinMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberCheckin;
import com.macro.mall.portal.service.UmsMemberCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UmsMemberCheckinServiceImpl implements UmsMemberCheckinService {

    @Autowired
    private UmsMemberCheckinMapper checkinMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    public UmsMemberCheckin checkin(Long memberId) {
        // 检查今日是否已签到
        if (isTodayChecked(memberId)) {
            throw new RuntimeException("今日已签到");
        }

        // 获取会员信息
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        // 计算连续签到天数
        Integer continuousDays = calculateContinuousDays(memberId);
        // 强制设置：昨天连续6天，今天第7天

        // 计算本次签到积分
        Integer integration = calculateIntegration(continuousDays + 1);

        // 创建签到记录
        UmsMemberCheckin checkin = new UmsMemberCheckin();
        checkin.setMemberId(memberId);
        checkin.setCheckinDate(new Date());
        checkin.setContinuousDays(continuousDays + 1);
        checkin.setIntegration(integration);

        checkinMapper.insert(checkin);

        // 更新会员积分
        member.setIntegration(member.getIntegration() + integration);
        memberMapper.updateByPrimaryKey(member);

        return checkin;
    }

    @Override
    public List<UmsMemberCheckin> getCheckinHistory(Long memberId, Date startDate, Date endDate, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;
        // 这里需要修改Mapper支持分页，暂时先返回所有数据
        return checkinMapper.selectByMemberIdAndDateRange(memberId, startDate, endDate);
    }

    @Override
    public Integer getContinuousCheckinDays(Long memberId) {
        UmsMemberCheckin latestCheckin = checkinMapper.selectLatestCheckin(memberId);
        if (latestCheckin == null) {
            return 0;
        }

        // 检查最后签到日期是否是昨天或今天
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String latestDateStr = sdf.format(latestCheckin.getCheckinDate());
        String todayStr = sdf.format(new Date());
        String yesterdayStr = sdf.format(yesterday);

        if (latestDateStr.equals(todayStr) || latestDateStr.equals(yesterdayStr)) {
            return latestCheckin.getContinuousDays();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isTodayChecked(Long memberId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        UmsMemberCheckin todayCheckin = checkinMapper.selectByMemberIdAndDate(memberId, new Date());
        return todayCheckin != null;
    }

    private Integer calculateContinuousDays(Long memberId) {
        UmsMemberCheckin latestCheckin = checkinMapper.selectLatestCheckin(memberId);
        if (latestCheckin == null) {
            return 0;
        }

        // 检查最后签到日期是否是昨天
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String latestDateStr = sdf.format(latestCheckin.getCheckinDate());
        String yesterdayStr = sdf.format(yesterday);

        if (latestDateStr.equals(yesterdayStr)) {
            return latestCheckin.getContinuousDays();
        } else {
            return 0;
        }
    }

    private Integer calculateIntegration(Integer continuousDays) {
        if (continuousDays >= 15) {
            return 30;
        } else if (continuousDays >= 7) {
            return 20;
        } else {
            return 10;
        }
    }
}
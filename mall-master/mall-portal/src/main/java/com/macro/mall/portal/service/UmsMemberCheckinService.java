package com.macro.mall.portal.service;

import com.macro.mall.model.UmsMemberCheckin;

import java.util.Date;
import java.util.List;

public interface UmsMemberCheckinService {
    /**
     * 会员签到
     */
    UmsMemberCheckin checkin(Long memberId);

    /**
     * 查询签到记录
     */
    List<UmsMemberCheckin> getCheckinHistory(Long memberId, Date startDate, Date endDate, Integer pageNum, Integer pageSize);

    /**
     * 查询当前连续签到天数
     */
    Integer getContinuousCheckinDays(Long memberId);

    /**
     * 查询今日是否已签到
     */
    boolean isTodayChecked(Long memberId);
}
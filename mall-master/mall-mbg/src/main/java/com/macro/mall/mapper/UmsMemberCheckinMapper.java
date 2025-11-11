package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberCheckin;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UmsMemberCheckinMapper {
    int insert(UmsMemberCheckin record);
    List<UmsMemberCheckin> selectByMemberIdAndDateRange(@Param("memberId") Long memberId,
                                                        @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate);
    UmsMemberCheckin selectLatestCheckin(@Param("memberId") Long memberId);
    UmsMemberCheckin selectByMemberIdAndDate(@Param("memberId") Long memberId,
                                             @Param("checkinDate") Date checkinDate);
    int countByMemberId(@Param("memberId") Long memberId);
}
package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberCheckin;
import com.macro.mall.portal.service.UmsMemberCheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员签到管理Controller
 */
@Controller
@Api(tags = "UmsMemberCheckinController", description = "会员签到管理")
@RequestMapping("/member/checkin")
public class UmsMemberCheckinController {

    @Autowired
    private UmsMemberCheckinService checkinService;

    @ApiOperation("会员签到")
    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsMemberCheckin> checkin() {
        try {
            // 从当前登录用户获取会员ID
            // 这里需要根据你的认证系统获取当前用户ID
            // 暂时使用测试ID，实际应该从SecurityContext或token中获取
            Long memberId = getCurrentMemberId();

            UmsMemberCheckin checkin = checkinService.checkin(memberId);
            return CommonResult.success(checkin);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("查询签到记录")
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberCheckin>> getCheckinHistory(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        System.out.println("签到记录接口被调用 - 返回测试数据");

        // 创建测试数据
        List<UmsMemberCheckin> checkinList = new ArrayList<>();

        // 获取当前时间
        long now = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000; // 一天的毫秒数

        // 添加过去7天的签到记录
        for (int i = 6; i >= 0; i--) {
            UmsMemberCheckin checkin = new UmsMemberCheckin();
            checkin.setId((long) (7 - i));
            checkin.setMemberId(11L);

            // 设置日期（过去i天）
            Date checkinDate = new Date(now - (i * oneDay));
            checkin.setCheckinDate(checkinDate);

            int continuousDays = 7 - i; // 连续天数从1到7
            checkin.setContinuousDays(continuousDays);

            // 根据连续天数计算积分（修正逻辑）
            if (continuousDays >= 15) {
                checkin.setIntegration(30);
            } else if (continuousDays >= 7) {
                checkin.setIntegration(20);
            } else {
                checkin.setIntegration(10);
            }

            checkinList.add(checkin);

            System.out.println("生成测试记录: 日期=" + checkinDate + ", 连续天数=" + continuousDays + ", 积分=" + checkin.getIntegration());
        }
        return CommonResult.success(CommonPage.restPage(checkinList));
    }

    @ApiOperation("查询连续签到天数")
    @RequestMapping(value = "/continuousDays", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> getContinuousCheckinDays() {
        System.out.println("连续签到天数接口被调用 - 返回测试值");
        return CommonResult.success(6);
    }

    @ApiOperation("查询今日是否已签到")
    @RequestMapping(value = "/todayStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> getTodayCheckinStatus() {
        System.out.println("今日状态接口被调用 - 返回测试值");
        return CommonResult.success(false);
    }

    @ApiOperation("会员签到")
    @RequestMapping(value = "/docheckin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsMemberCheckin> docheckin() {
        System.out.println("签到接口被调用 - 返回测试数据");

        // 创建测试签到记录
        UmsMemberCheckin checkin = new UmsMemberCheckin();
        checkin.setId(1L);
        checkin.setMemberId(11L);
        checkin.setCheckinDate(new Date());
        checkin.setContinuousDays(7);
        checkin.setIntegration(20);

        return CommonResult.success(checkin);
    }

    /**
     * 获取当前登录会员ID
     * 这里需要根据你的认证系统实现
     */
    private Long getCurrentMemberId() {
        // 临时返回测试ID，实际应该从SecurityContext或token中获取
        return 11L;

        // 如果是Spring Security，可以这样获取：
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // return ((UmsMember) authentication.getPrincipal()).getId();
    }
}
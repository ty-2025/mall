package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.service.UmsMemberLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员等级管理Controller
 */
@Controller
@Api(tags = "UmsMemberLevelController", description = "会员等级管理")
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {

    @Autowired
    private UmsMemberLevelService memberLevelService;

    @ApiOperation("分页查询会员等级列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberLevel>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsMemberLevel> memberLevelList = memberLevelService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(memberLevelList));
    }

    @ApiOperation("获取所有会员等级")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsMemberLevel>> listAll() {
        List<UmsMemberLevel> memberLevelList = memberLevelService.listAll();
        return CommonResult.success(memberLevelList);
    }

    @ApiOperation("根据ID获取会员等级详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMemberLevel> getItem(@PathVariable Long id) {
        UmsMemberLevel memberLevel = memberLevelService.getItem(id);
        if (memberLevel != null) {
            return CommonResult.success(memberLevel);
        }
        return CommonResult.failed("会员等级不存在");
    }

    @ApiOperation("添加会员等级")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody UmsMemberLevel memberLevel) {
        // 数据验证
        if (memberLevel.getName() == null || memberLevel.getName().trim().isEmpty()) {
            return CommonResult.failed("等级名称不能为空");
        }
        if (memberLevel.getGrowthPoint() == null) {
            return CommonResult.failed("成长值不能为空");
        }

        // 检查等级名称是否已存在
        if (memberLevelService.isNameExists(memberLevel.getName(), null)) {
            return CommonResult.failed("等级名称已存在");
        }

        // 设置默认值
        if (memberLevel.getDefaultStatus() == null) {
            memberLevel.setDefaultStatus(0);
        }
        if (memberLevel.getFreeFreightPoint() == null) {
            memberLevel.setFreeFreightPoint(BigDecimal.ZERO);
        }
        if (memberLevel.getCommentGrowthPoint() == null) {
            memberLevel.setCommentGrowthPoint(0);
        }
        if (memberLevel.getPriviledgeFreeFreight() == null) {
            memberLevel.setPriviledgeFreeFreight(0);
        }
        if (memberLevel.getPriviledgeSignIn() == null) {
            memberLevel.setPriviledgeSignIn(0);
        }
        if (memberLevel.getPriviledgeComment() == null) {
            memberLevel.setPriviledgeComment(0);
        }
        if (memberLevel.getPriviledgePromotion() == null) {
            memberLevel.setPriviledgePromotion(0);
        }
        if (memberLevel.getPriviledgeMemberPrice() == null) {
            memberLevel.setPriviledgeMemberPrice(0);
        }
        if (memberLevel.getPriviledgeBirthday() == null) {
            memberLevel.setPriviledgeBirthday(0);
        }

        int count = memberLevelService.create(memberLevel);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改会员等级")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody UmsMemberLevel memberLevel) {
        // 数据验证
        UmsMemberLevel existingLevel = memberLevelService.getItem(id);
        if (existingLevel == null) {
            return CommonResult.failed("会员等级不存在");
        }

        // 检查等级名称是否被其他等级使用
        if (memberLevel.getName() != null && !memberLevel.getName().equals(existingLevel.getName())) {
            if (memberLevelService.isNameExists(memberLevel.getName(), id)) {
                return CommonResult.failed("等级名称已存在");
            }
        }

        memberLevel.setId(id);
        int count = memberLevelService.update(id, memberLevel);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除会员等级")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        // 检查是否为默认等级
        UmsMemberLevel level = memberLevelService.getItem(id);
        if (level != null && level.getDefaultStatus() == 1) {
            return CommonResult.failed("不能删除默认等级");
        }

        int count = memberLevelService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除会员等级")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        // 检查是否包含默认等级
        for (Long id : ids) {
            UmsMemberLevel level = memberLevelService.getItem(id);
            if (level != null && level.getDefaultStatus() == 1) {
                return CommonResult.failed("不能删除默认等级");
            }
        }

        int count = memberLevelService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改默认状态")
    @RequestMapping(value = "/updateDefaultStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateDefaultStatus(@PathVariable Long id, @RequestParam Integer defaultStatus) {
        int count = memberLevelService.updateDefaultStatus(id, defaultStatus);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
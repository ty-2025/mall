package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 会员管理Controller
 */
@Controller
@Api(tags = "UmsMemberController", description = "会员管理")
@RequestMapping("/member")
public class UmsMemberController {

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("根据分页获取会员列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMember>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsMember> memberList = memberService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(memberList));
    }

    @ApiOperation("根据ID获取会员详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMember> getItem(@PathVariable Long id) {
        UmsMember member = memberService.getItem(id);
        if (member != null) {
            // 隐藏密码
            member.setPassword(null);
            return CommonResult.success(member);
        }
        return CommonResult.failed("会员不存在");
    }

    @ApiOperation("添加会员")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody UmsMember member) {
        // 数据验证
        if (member.getUsername() == null || member.getUsername().trim().isEmpty()) {
            return CommonResult.failed("用户名不能为空");
        }
        if (member.getPassword() == null || member.getPassword().trim().isEmpty()) {
            return CommonResult.failed("密码不能为空");
        }
        if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
            return CommonResult.failed("手机号不能为空");
        }

        // 检查用户名是否已存在
        if (memberService.isUsernameExists(member.getUsername())) {
            return CommonResult.failed("用户名已存在");
        }

        // 检查手机号是否已存在
        if (memberService.isPhoneExists(member.getPhone())) {
            return CommonResult.failed("手机号已存在");
        }

        // 设置默认值
        if (member.getStatus() == null) {
            member.setStatus(1); // 默认启用
        }
        if (member.getMemberLevelId() == null) {
            member.setMemberLevelId(1L); // 默认普通会员
        }
        member.setCreateTime(new Date());
        member.setIntegration(0);
        member.setGrowth(0);

        // 密码加密
        member.setPassword(new BCryptPasswordEncoder().encode(member.getPassword()));

        int count = memberService.create(member);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改会员")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody UmsMember member) {
        // 数据验证
        UmsMember existingMember = memberService.getItem(id);
        if (existingMember == null) {
            return CommonResult.failed("会员不存在");
        }

        // 检查用户名是否被其他会员使用
        if (member.getUsername() != null && !member.getUsername().equals(existingMember.getUsername())) {
            if (memberService.isUsernameExists(member.getUsername())) {
                return CommonResult.failed("用户名已存在");
            }
        }

        // 检查手机号是否被其他会员使用
        if (member.getPhone() != null && !member.getPhone().equals(existingMember.getPhone())) {
            if (memberService.isPhoneExists(member.getPhone())) {
                return CommonResult.failed("手机号已存在");
            }
        }

        // 如果传了密码，进行加密
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            if (!member.getPassword().startsWith("$2a$")) {
                member.setPassword(new BCryptPasswordEncoder().encode(member.getPassword()));
            }
        } else {
            // 不更新密码
            member.setPassword(null);
        }

        member.setId(id);
        int count = memberService.update(id, member);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除会员")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = memberService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除会员")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = memberService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改会员状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        UmsMember member = new UmsMember();
        member.setStatus(status);
        int count = memberService.update(id, member);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
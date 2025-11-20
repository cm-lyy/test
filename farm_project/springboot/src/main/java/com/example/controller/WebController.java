package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;


@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;


    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account ac = null;
        if ("ADMIN".equals(account.getRole())) {
            ac = adminService.login(account);
        } else {
            ac = userService.login(account);
        }
        return Result.success(ac);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if ("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        } else {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    /**
     * 找回密码
     */
    @PostMapping("/ForgotPassword")
    public Result forgotPassword(@RequestBody User user) {
        // 根据账号角色判断是管理员还是普通用户
        if ("ADMIN".equals(user.getRole())) {
            // 管理员找回密码逻辑
//            adminService.forgotPassword(Admin admin);
        } else {
            boolean success = userService.forgotPassword(user);
            if (success) {
                return Result.success("密码重置成功");
            } else {
                return Result.error("账号或手机号不匹配，无法重置密码");
            }
        }
        return Result.success("密码重置成功");
    }
    /**
     * 找回密码
     */
    @PostMapping("/ForgotUsername")
    public Result forgotUsername(@RequestBody User user) throws MessagingException {
        // 根据账号角色判断是管理员还是普通用户
        if ("ADMIN".equals(user.getRole())) {
            // 管理员找回密码逻辑
//            adminService.forgotUsername(Admin admin);
        } else {
            boolean success = userService.forgotUsername(user);
            if (success) {
                return Result.success("账户已发送到您的邮箱");
            } else {
                return Result.error("未查询到对应账户，请检查邮箱是否输入正确");
            }
        }
        return Result.success("账户已发送到您的邮箱");
    }
    /**
     * 忘记账号名
     */
//    @PostMapping("/ForgotUsername")
}

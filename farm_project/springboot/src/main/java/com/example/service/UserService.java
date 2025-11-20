package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.mapper.UserMapper;
import com.example.util.EmailSender;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户的业务处理
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // 创建加密器

    /**
     * 新增
     */
    public void  add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException("用户已存在");
        }
        User dbPhone = userMapper.selectByphoneNumber(user.getphoneNumber());
        if (ObjectUtil.isNotNull(dbPhone)) {
            throw  new CustomException("手机号已注册用户");
        }
        User dbEmail = userMapper.selectByEmail(user.getemail());
        if (ObjectUtil.isNotNull(dbEmail)) {
            throw  new CustomException("邮箱已注册用户");
        }

        // 如果没有密码默认设置为12345
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword("12345");
        }
        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);  // 设置加密后的密码

        //默认头像
        if (ObjectUtil.isEmpty(user.getAvatar())) {
            user.setAvatar("http://lilili.qicp.vip:9080/api/files/upload/1732527432486-nailong.jpg");
        }

        // 如果名称为空，默认设置为用户名
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername());
        }

        // 设置角色为USER
        user.setRole("USER");
        userMapper.insert(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void updateById(User user) {
        // 从数据库获取当前用户数据
        User dbUser = userMapper.selectById(user.getId());
        if (dbUser == null) {
            throw new CustomException("用户不存在");
        }

        // 检查密码是否为空，如果为空则保留原密码
        System.out.println(dbUser.getPassword());
        System.out.println(user.getPassword());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(dbUser.getPassword());
        } else {
            // 如果密码不为空，进行加密处理
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 进行更新操作
        userMapper.updateById(user);
    }

    /**
     * 用户找回密码
     */
    @Transactional
    public boolean forgotPassword(User user) {
        // 查询数据库中的用户信息
        User dbUser = userMapper.selectByUsername(user.getUsername());
        // 如果用户不存在
        if (dbUser == null) {
            return false;
        }
        // 判断用户名是否匹配
        if (! user.getUsername().equals(dbUser.getUsername())) {
            return false;
        }

        // 判断手机号是否匹配
        if (! user.getphoneNumber().equals(dbUser.getphoneNumber())) {
            return false;
        }
        // 更新密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.updateById(dbUser);

        // 如果更新成功，返回 true
        return true;
    }
    @Autowired
    private EmailSender emailSender;
    /**
     * 用户找回账户
     */
    @Transactional
    public boolean forgotUsername(User user) throws MessagingException {
        // 查询数据库中的用户信息
        User dbUser = userMapper.selectByEmail(user.getemail());
        if (dbUser == null) {
            return false;
        }
        String text = "您的账号是:  " + dbUser.getUsername() + "\n如果忘记密码,可通过登陆页面(忘记密码？)找回";
        String to = dbUser.getemail();
        String subject = "[裕裕商城]找回账号";

        emailSender.sendEmail(to, subject, text);

        System.out.println(text);

        return true;
    }


    /**
     * 根据ID查询
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException("用户不存在");
        }
        if (!passwordEncoder.matches(account.getPassword(), dbUser.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        return dbUser;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());

        if (dbUser == null) {
            throw new CustomException("用户不存在");
        }
        // 比对原密码，使用 BCryptPasswordEncoder 的 matches 方法
        if (!passwordEncoder.matches(account.getPassword(), dbUser.getPassword())) {
            throw new CustomException("原密码错误");
        }
        // 设置新密码并进行加密
        String encodedNewPassword = passwordEncoder.encode(account.getNewPassword());
        dbUser.setPassword(encodedNewPassword);
        // 更新密码
        userMapper.updateById(dbUser);
    }

    /**
     * 注册
     */
    public void register(User user) {
        this.add(user);
    }
}
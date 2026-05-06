package com.campus.lostfound.service;

import com.campus.lostfound.entity.User;
import com.campus.lostfound.mapper.UserMapper;
import com.campus.lostfound.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果（成功返回用户ID，失败返回错误信息）
     */
    @Transactional
    public Result register(User user) {
        // 参数校验
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        // 检查用户名是否已存在
        int count = userMapper.countByUsername(user.getUsername());
        if (count > 0) {
            return Result.error("用户名已存在");
        }
        
        // 设置默认值
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername()); // 默认昵称为用户名
        }
        
        // 密码加密
        String encryptedPassword = MD5Util.md5(user.getPassword());
        user.setPassword(encryptedPassword);
        
        // 设置角色和状态
        user.setRole(0); // 普通用户
        user.setStatus(0); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        int result = userMapper.insert(user);
        if (result > 0) {
            return Result.success("注册成功", user.getUserId());
        } else {
            return Result.error("注册失败，请稍后重试");
        }
    }
    
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（成功返回用户信息，失败返回错误信息）
     */
    public Result login(String username, String password) {
        // 参数校验
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }
        
        // 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }
        
        // 验证密码
        String encryptedPassword = MD5Util.md5(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        
        // 更新最后登录时间（如果需要的话）
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        
        // 不返回密码
        user.setPassword(null);
        return Result.success("登录成功", user);
    }
    
    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    @Transactional
    public Result updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            return Result.error("用户信息不完整");
        }
        
        // 获取原始用户信息
        User originalUser = userMapper.selectById(user.getUserId());
        if (originalUser == null) {
            return Result.error("用户不存在");
        }
        
        // 只允许更新部分字段
        originalUser.setNickname(user.getNickname());
        originalUser.setContact(user.getContact());
        originalUser.setAvatar(user.getAvatar());
        originalUser.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.update(originalUser);
        if (result > 0) {
            originalUser.setPassword(null);
            return Result.success("更新成功", originalUser);
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @Transactional
    public Result changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return Result.error("参数不完整");
        }
        
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证旧密码
        String encryptedOldPassword = MD5Util.md5(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            return Result.error("旧密码错误");
        }
        
        // 加密新密码
        String encryptedNewPassword = MD5Util.md5(newPassword);
        user.setPassword(encryptedNewPassword);
        user.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updatePassword(userId, encryptedNewPassword, LocalDateTime.now());
        if (result > 0) {
            return Result.success("密码修改成功");
        } else {
            return Result.error("密码修改失败");
        }
    }
    
    /**
     * 查询所有用户（管理员使用）
     *
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        List<User> users = userMapper.selectAll();
        // 移除密码
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 统一返回结果类
     */
    public static class Result {
        private boolean success;
        private String message;
        private Object data;
        
        private Result(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public static Result success(String message) {
            return new Result(true, message, null);
        }
        
        public static Result success(String message, Object data) {
            return new Result(true, message, data);
        }
        
        public static Result error(String message) {
            return new Result(false, message, null);
        }
        
        // Getter方法
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getData() {
            return data;
        }
    }
    
    /**
     * 统计所有用户数量
     *
     * @return 用户总数
     */
    public int countAllUsers() {
        try {
            return userMapper.countAll();
        } catch (Exception e) {
            // 如果查询出错，返回0
            return 0;
        }
    }
}
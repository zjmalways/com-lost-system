package com.campus.lostfound.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名（学号）
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 联系方式（微信/QQ/电话）
     */
    private String contact;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 用户角色：0-普通用户，1-管理员
     */
    private Integer role;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 用户状态：0-正常，1-禁用
     */
    private Integer status;
    
    // 简化构造函数
    public User() {
        this.role = 0; // 默认普通用户
        this.status = 0; // 默认正常状态
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
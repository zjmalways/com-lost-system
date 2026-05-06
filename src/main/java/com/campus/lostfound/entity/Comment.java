package com.campus.lostfound.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
public class Comment {
    /**
     * 评论ID
     */
    private Long commentId;
    
    /**
     * 物品ID
     */
    private Long itemId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户昵称（冗余字段）
     */
    private String userNickname;
    
    /**
     * 用户头像（冗余字段）
     */
    private String userAvatar;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（0表示顶级评论）
     */
    private Long parentId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 是否已读：0-未读，1-已读（用于通知）
     */
    private Integer isRead;
    
    // 简化构造函数
    public Comment() {
        this.parentId = 0L; // 默认顶级评论
        this.isRead = 0; // 默认未读
        this.createTime = LocalDateTime.now();
    }
}
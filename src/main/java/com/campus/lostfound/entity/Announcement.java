package com.campus.lostfound.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
public class Announcement {
    /**
     * 公告ID
     */
    private Long announcementId;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 发布人ID
     */
    private Long publisherId;
    
    /**
     * 发布人名称
     */
    private String publisherName;
    
    /**
     * 是否置顶：0-不置顶，1-置顶
     */
    private Integer isTop;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 简化构造函数
    public Announcement() {
        this.isTop = 0; // 默认不置顶
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
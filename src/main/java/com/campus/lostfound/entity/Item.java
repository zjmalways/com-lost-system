package com.campus.lostfound.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物品实体类
 */
@Data
public class Item {
    /**
     * 物品ID
     */
    private Long itemId;
    
    /**
     * 物品类型：1-证件, 2-钥匙, 3-电子设备, 4-衣物, 5-钱包, 6-其他
     */
    private Integer itemType;
    
    /**
     * 发布类型：0-丢失, 1-捡到
     */
    private Integer publishType;
    
    /**
     * 物品标题
     */
    private String title;
    
    /**
     * 物品描述
     */
    private String description;
    
    /**
     * 物品特征
     */
    private String features;
    
    /**
     * 图片URL（多个图片用逗号分隔）
     */
    private String images;
    
    /**
     * 丢失/捡到地点
     */
    private String location;
    
    /**
     * 丢失/捡到时间
     */
    private LocalDateTime eventTime;
    
    /**
     * 发布人ID
     */
    private Long publisherId;
    
    /**
     * 发布人昵称（冗余字段，避免联表查询）
     */
    private String publisherName;
    
    /**
     * 联系方式（冗余字段）
     */
    private String contact;
    
    /**
     * 存放地点（仅招领信息有效）
     */
    private String storageLocation;
    
    /**
     * 物品状态：0-未找回/未归还, 1-已找回/已归还
     */
    private Integer status;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 收藏次数
     */
    private Integer collectCount;
    
    /**
     * 评论次数
     */
    private Integer commentCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 简化构造函数
    public Item() {
        this.viewCount = 0;
        this.collectCount = 0;
        this.commentCount = 0;
        this.status = 0; // 默认未处理
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 获取物品类型名称
     */
    public String getItemTypeName() {
        switch (itemType) {
            case 1: return "证件";
            case 2: return "钥匙";
            case 3: return "电子设备";
            case 4: return "衣物";
            case 5: return "钱包";
            case 6: return "其他";
            default: return "未知";
        }
    }
    
    /**
     * 获取发布类型名称
     */
    public String getPublishTypeName() {
        return publishType == 0 ? "丢失" : "捡到";
    }
}
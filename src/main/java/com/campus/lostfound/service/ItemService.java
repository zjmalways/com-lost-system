package com.campus.lostfound.service;

import com.campus.lostfound.entity.Item;
import com.campus.lostfound.mapper.ItemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 物品服务类
 */
@Service
public class ItemService {
    
    @Autowired
    private ItemMapper itemMapper;
    
    @Autowired
    private UserService userService;
    
    /**
     * 发布物品
     *
     * @param item 物品信息
     * @param userId 发布人ID
     * @return 发布结果
     */
    @Transactional
    public UserService.Result publishItem(Item item, Long userId) {
        // 参数校验
        if (item == null || item.getTitle() == null || item.getTitle().isEmpty()) {
            return UserService.Result.error("物品标题不能为空");
        }
        
        if (item.getLocation() == null || item.getLocation().isEmpty()) {
            return UserService.Result.error("地点不能为空");
        }
        
        if (item.getEventTime() == null) {
            item.setEventTime(LocalDateTime.now());
        }
        
        // 验证发布人
        com.campus.lostfound.entity.User user = userService.getUserById(userId);
        if (user == null) {
            return UserService.Result.error("用户不存在");
        }
        
        // 设置发布人信息
        item.setPublisherId(userId);
        item.setPublisherName(user.getNickname());
        item.setContact(user.getContact());
        
        // 设置默认值
        if (item.getStatus() == null) {
            item.setStatus(0); // 默认未处理
        }
        if (item.getViewCount() == null) {
            item.setViewCount(0);
        }
        if (item.getCollectCount() == null) {
            item.setCollectCount(0);
        }
        if (item.getCommentCount() == null) {
            item.setCommentCount(0);
        }
        
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        
        // 保存物品
        int result = itemMapper.insert(item);
        if (result > 0) {
            return UserService.Result.success("发布成功", item.getItemId());
        } else {
            return UserService.Result.error("发布失败，请稍后重试");
        }
    }
    
    /**
     * 更新物品信息
     *
     * @param item 物品信息
     * @param userId 用户ID（用于权限验证）
     * @return 更新结果
     */
    @Transactional
    public UserService.Result updateItem(Item item, Long userId) {
        if (item == null || item.getItemId() == null) {
            return UserService.Result.error("物品信息不完整");
        }
        
        // 获取原始物品
        Item originalItem = itemMapper.selectById(item.getItemId());
        if (originalItem == null) {
            return UserService.Result.error("物品不存在");
        }
        
        // 权限验证：只有发布人或管理员可以修改
        if (!originalItem.getPublisherId().equals(userId)) {
            // TODO: 这里可以添加管理员权限检查
            return UserService.Result.error("无权修改此物品");
        }
        
        // 只允许更新部分字段
        originalItem.setTitle(item.getTitle());
        originalItem.setDescription(item.getDescription());
        originalItem.setFeatures(item.getFeatures());
        originalItem.setImages(item.getImages());
        originalItem.setLocation(item.getLocation());
        originalItem.setEventTime(item.getEventTime());
        originalItem.setContact(item.getContact());
        originalItem.setStorageLocation(item.getStorageLocation());
        originalItem.setUpdateTime(LocalDateTime.now());
        
        int result = itemMapper.update(originalItem);
        if (result > 0) {
            return UserService.Result.success("更新成功", originalItem);
        } else {
            return UserService.Result.error("更新失败");
        }
    }
    
    /**
     * 删除物品
     *
     * @param itemId 物品ID
     * @param userId 用户ID（用于权限验证）
     * @return 删除结果
     */
    @Transactional
    public UserService.Result deleteItem(Long itemId, Long userId) {
        if (itemId == null) {
            return UserService.Result.error("物品ID不能为空");
        }
        
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return UserService.Result.error("物品不存在");
        }
        
        // 权限验证：只有发布人或管理员可以删除
        if (!item.getPublisherId().equals(userId)) {
            // TODO: 这里可以添加管理员权限检查
            return UserService.Result.error("无权删除此物品");
        }
        
        int result = itemMapper.delete(itemId);
        if (result > 0) {
            return UserService.Result.success("删除成功");
        } else {
            return UserService.Result.error("删除失败");
        }
    }
    
    /**
     * 根据物品ID查询物品详情（增加浏览次数）
     *
     * @param itemId 物品ID
     * @return 物品详情
     */
    @Transactional
    public UserService.Result getItemDetail(Long itemId) {
        if (itemId == null) {
            return UserService.Result.error("物品ID不能为空");
        }
        
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return UserService.Result.error("物品不存在");
        }
        
        // 增加浏览次数
        itemMapper.incrementViewCount(itemId);
        item.setViewCount(item.getViewCount() + 1);
        
        return UserService.Result.success("查询成功", item);
    }
    
    /**
     * 分页查询物品列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param publishType 发布类型（可选）
     * @param itemType 物品类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    public PageInfo<Item> getItemList(int pageNum, int pageSize, Integer publishType, Integer itemType, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        
        List<Item> items;
        if (publishType != null && itemType != null) {
            // TODO: 需要实现组合查询，这里简化处理
            items = itemMapper.selectByPublishType(publishType);
        } else if (publishType != null) {
            items = itemMapper.selectByPublishType(publishType);
        } else if (itemType != null) {
            items = itemMapper.selectByItemType(itemType);
        } else if (status != null) {
            items = itemMapper.selectByStatus(status);
        } else {
            items = itemMapper.selectAll();
        }
        
        return new PageInfo<>(items);
    }
    
    /**
     * 搜索物品
     *
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Item> searchItems(String keyword, int pageNum, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getItemList(pageNum, pageSize, null, null, null);
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Item> items = itemMapper.search(keyword.trim());
        return new PageInfo<>(items);
    }
    
    /**
     * 查询用户发布的物品
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Item> getUserItems(Long userId, int pageNum, int pageSize) {
        if (userId == null) {
            return new PageInfo<>();
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Item> items = itemMapper.selectByPublisherId(userId);
        return new PageInfo<>(items);
    }
    
    /**
     * 更新物品状态
     *
     * @param itemId 物品ID
     * @param status 状态（0-未处理，1-已处理）
     * @param userId 用户ID（用于权限验证）
     * @return 更新结果
     */
    @Transactional
    public UserService.Result updateItemStatus(Long itemId, Integer status, Long userId) {
        if (itemId == null || status == null) {
            return UserService.Result.error("参数不完整");
        }
        
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return UserService.Result.error("物品不存在");
        }
        
        // 权限验证：只有发布人或管理员可以更新状态
        if (!item.getPublisherId().equals(userId)) {
            // TODO: 这里可以添加管理员权限检查
            return UserService.Result.error("无权更新此物品状态");
        }
        
        if (status != 0 && status != 1) {
            return UserService.Result.error("状态值无效");
        }
        
        item.setStatus(status);
        item.setUpdateTime(LocalDateTime.now());
        
        int result = itemMapper.updateStatus(itemId, status, LocalDateTime.now());
        if (result > 0) {
            return UserService.Result.success("状态更新成功");
        } else {
            return UserService.Result.error("状态更新失败");
        }
    }
    
    /**
     * 增加收藏次数
     *
     * @param itemId 物品ID
     * @return 操作结果
     */
    @Transactional
    public UserService.Result incrementCollectCount(Long itemId) {
        if (itemId == null) {
            return UserService.Result.error("物品ID不能为空");
        }
        
        int result = itemMapper.incrementCollectCount(itemId);
        if (result > 0) {
            return UserService.Result.success("收藏成功");
        } else {
            return UserService.Result.error("收藏失败");
        }
    }
    
    /**
     * 减少收藏次数
     *
     * @param itemId 物品ID
     * @return 操作结果
     */
    @Transactional
    public UserService.Result decrementCollectCount(Long itemId) {
        if (itemId == null) {
            return UserService.Result.error("物品ID不能为空");
        }
        
        int result = itemMapper.decrementCollectCount(itemId);
        if (result > 0) {
            return UserService.Result.success("取消收藏成功");
        } else {
            return UserService.Result.error("取消收藏失败");
        }
    }
    
    /**
     * 获取物品统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    public UserService.Result getItemStats(Long userId) {
        if (userId == null) {
            return UserService.Result.error("用户ID不能为空");
        }
        
        int itemCount = itemMapper.countByPublisherId(userId);
        
        // 可以添加更多统计信息
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("itemCount", itemCount);
        stats.put("publishedItems", itemCount);
        
        return UserService.Result.success("查询成功", stats);
    }
    
    /**
     * 获取平台统计信息
     *
     * @return 平台统计数据
     */
    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取物品总数
            int totalItems = itemMapper.countAll();
            stats.put("totalItems", totalItems);
            
            // 获取已找回物品数量
            int resolvedItems = itemMapper.countResolved();
            stats.put("resolvedItems", resolvedItems);
            
            // 计算找回率（如果有物品）
            double recoveryRate = totalItems > 0 ? (double) resolvedItems / totalItems * 100 : 0;
            stats.put("recoveryRate", Math.round(recoveryRate * 10.0) / 10.0);
            
            // 计算平均找回时间
            Double avgRecoveryTime = itemMapper.calculateAverageRecoveryTime();
            if (avgRecoveryTime != null && resolvedItems > 0) {
                stats.put("avgRecoveryTime", Math.round(avgRecoveryTime * 10.0) / 10.0);
            } else {
                stats.put("avgRecoveryTime", 0.0);
            }
        } catch (Exception e) {
            // 如果查询出错，返回默认值
            stats.put("totalItems", 0);
            stats.put("resolvedItems", 0);
            stats.put("recoveryRate", 0.0);
            stats.put("avgRecoveryTime", 0.0);
        }
        
        return stats;
    }
}
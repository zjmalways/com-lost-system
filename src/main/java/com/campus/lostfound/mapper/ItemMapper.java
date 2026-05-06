package com.campus.lostfound.mapper;

import com.campus.lostfound.entity.Item;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 物品Mapper接口
 */
@Mapper
public interface ItemMapper {

    /**
     * 插入物品
     */
    @Insert("INSERT INTO items (item_id, item_type, publish_type, title, description, features, images, location, " +
            "event_time, publisher_id, publisher_name, contact, storage_location, status, view_count, " +
            "collect_count, comment_count, create_time, update_time) " +
            "VALUES (SEQ_ITEMS.NEXTVAL, #{itemType, jdbcType=INTEGER}, #{publishType, jdbcType=INTEGER}, #{title, jdbcType=VARCHAR}, #{description, jdbcType=VARCHAR}, #{features, jdbcType=VARCHAR}, #{images, jdbcType=VARCHAR}, " +
            "#{location, jdbcType=VARCHAR}, #{eventTime, jdbcType=TIMESTAMP}, #{publisherId, jdbcType=BIGINT}, #{publisherName, jdbcType=VARCHAR}, #{contact, jdbcType=VARCHAR}, #{storageLocation, jdbcType=VARCHAR}, #{status, jdbcType=INTEGER}, " +
            "#{viewCount, jdbcType=INTEGER}, #{collectCount, jdbcType=INTEGER}, #{commentCount, jdbcType=INTEGER}, #{createTime, jdbcType=TIMESTAMP}, #{updateTime, jdbcType=TIMESTAMP})")
    @Options(useGeneratedKeys = false, keyProperty = "itemId")
    int insert(Item item);
    
    /**
     * 根据物品ID更新物品信息
     */
    @Update("UPDATE items SET title = #{title, jdbcType=VARCHAR}, description = #{description, jdbcType=VARCHAR}, features = #{features, jdbcType=VARCHAR}, images = #{images, jdbcType=VARCHAR}, " +
            "location = #{location, jdbcType=VARCHAR}, event_time = #{eventTime, jdbcType=TIMESTAMP}, contact = #{contact, jdbcType=VARCHAR}, storage_location = #{storageLocation, jdbcType=VARCHAR}, " +
            "status = #{status, jdbcType=INTEGER}, update_time = #{updateTime, jdbcType=TIMESTAMP} WHERE item_id = #{itemId}")
    int update(Item item);

    /**
     * 根据物品ID删除物品
     */
    @Delete("DELETE FROM items WHERE item_id = #{itemId}")
    int delete(Long itemId);

    /**
     * 根据物品ID查询物品
     */
    @Select("SELECT * FROM items WHERE item_id = #{itemId}")
    Item selectById(Long itemId);

    /**
     * 查询所有物品（按创建时间倒序）
     */
    @Select("SELECT * FROM items ORDER BY create_time DESC")
    List<Item> selectAll();

    /**
     * 根据发布人ID查询物品
     */
    @Select("SELECT * FROM items WHERE publisher_id = #{publisherId} ORDER BY create_time DESC")
    List<Item> selectByPublisherId(Long publisherId);

    /**
     * 根据物品类型查询物品
     */
    @Select("SELECT * FROM items WHERE item_type = #{itemType} ORDER BY create_time DESC")
    List<Item> selectByItemType(Integer itemType);

    /**
     * 根据发布类型查询物品
     */
    @Select("SELECT * FROM items WHERE publish_type = #{publishType} ORDER BY create_time DESC")
    List<Item> selectByPublishType(Integer publishType);

    /**
     * 根据状态查询物品
     */
    @Select("SELECT * FROM items WHERE status = #{status} ORDER BY create_time DESC")
    List<Item> selectByStatus(Integer status);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE items SET view_count = view_count + 1 WHERE item_id = #{itemId}")
    int incrementViewCount(Long itemId);

    /**
     * 增加收藏次数
     */
    @Update("UPDATE items SET collect_count = collect_count + 1 WHERE item_id = #{itemId}")
    int incrementCollectCount(Long itemId);

    /**
     * 减少收藏次数
     */
    @Update("UPDATE items SET collect_count = collect_count - 1 WHERE item_id = #{itemId}")
    int decrementCollectCount(Long itemId);

    /**
     * 增加评论次数
     */
    @Update("UPDATE items SET comment_count = comment_count + 1 WHERE item_id = #{itemId}")
    int incrementCommentCount(Long itemId);

    /**
     * 减少评论次数
     */
    @Update("UPDATE items SET comment_count = comment_count - 1 WHERE item_id = #{itemId}")
    int decrementCommentCount(Long itemId);

    /**
     * 更新物品状态
     */
    @Update("UPDATE items SET status = #{status, jdbcType=INTEGER}, update_time = #{updateTime, jdbcType=TIMESTAMP} WHERE item_id = #{itemId}")
    int updateStatus(@Param("itemId") Long itemId, @Param("status") Integer status, @Param("updateTime") java.time.LocalDateTime updateTime);
    
    /**
     * 搜索物品（标题、描述、特征）
     */
    @Select("SELECT * FROM items WHERE title LIKE '%' || #{keyword} || '%' OR description LIKE '%' || #{keyword} || '%' OR features LIKE '%' || #{keyword} || '%' ORDER BY create_time DESC")
    List<Item> search(String keyword);
    
    /**
     * 统计用户发布的物品数量
     */
    @Select("SELECT COUNT(*) FROM items WHERE publisher_id = #{publisherId}")
    int countByPublisherId(Long publisherId);
    
    /**
     * 统计所有物品数量
     */
    @Select("SELECT COUNT(*) FROM items")
    int countAll();
    
    /**
     * 统计已找回/已归还的物品数量
     */
    @Select("SELECT COUNT(*) FROM items WHERE status = 1")
    int countResolved();
    
    /**
     * 计算平均找回时间（仅限已处理物品）
     * 返回天数的平均值
     */
    @Select("SELECT AVG(EXTRACT(DAY FROM (update_time - event_time))) FROM items WHERE status = 1")
    Double calculateAverageRecoveryTime();
}
package com.campus.lostfound.mapper;

import com.campus.lostfound.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 插入用户
     */
    @Insert("INSERT INTO users (user_id, username, password, nickname, contact, avatar, role, create_time, update_time, status) " +
            "VALUES (SEQ_USERS.NEXTVAL, #{username}, #{password}, #{nickname}, #{contact, jdbcType=VARCHAR}, #{avatar, jdbcType=VARCHAR}, #{role}, #{createTime, jdbcType=TIMESTAMP}, #{updateTime, jdbcType=TIMESTAMP}, #{status})")
    @Options(useGeneratedKeys = false, keyProperty = "userId")
    int insert(User user);
    
    /**
     * 根据用户ID更新用户信息
     */
    @Update("UPDATE users SET nickname = #{nickname}, contact = #{contact, jdbcType=VARCHAR}, avatar = #{avatar, jdbcType=VARCHAR}, update_time = #{updateTime, jdbcType=TIMESTAMP} " +
            "WHERE user_id = #{userId}")
    int update(User user);

    /**
     * 根据用户ID删除用户
     */
    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int delete(Long userId);

    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User selectById(Long userId);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM users ORDER BY create_time DESC")
    List<User> selectAll();

    /**
     * 根据用户名和密码查询用户（用于登录）
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password} AND status = 0")
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 更新用户密码
     */
    @Update("UPDATE users SET password = #{password}, update_time = #{updateTime, jdbcType=TIMESTAMP} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password, @Param("updateTime") java.time.LocalDateTime updateTime);
    
    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET status = #{status}, update_time = #{updateTime,jdbcType=TIMESTAMP} WHERE user_id = #{userId}")
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status, @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 检查用户名是否已存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);
    
    /**
     * 统计所有用户数量
     */
    @Select("SELECT COUNT(*) FROM users")
    int countAll();
}
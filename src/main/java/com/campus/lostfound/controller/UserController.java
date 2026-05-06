package com.campus.lostfound.controller;

import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * 用户控制器
 */
@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 跳转到注册页面
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "用户注册 - 校园失物招领平台");
        return "user/register";
    }
    
    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    @ResponseBody
    public UserService.Result register(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam(required = false) String nickname,
                                      @RequestParam(required = false) String contact) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setContact(contact);
        
        return userService.register(user);
    }
    
    /**
     * 跳转到登录页面
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "用户登录 - 校园失物招领平台");
        return "user/login";
    }
    
    /**
     * 处理登录请求
     */
    @PostMapping("/login")
    @ResponseBody
    public UserService.Result login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {
        UserService.Result result = userService.login(username, password);
        if (result.isSuccess()) {
            // 登录成功，将用户信息存入session
            User user = (User) result.getData();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("nickname", user.getNickname());
            session.setAttribute("role", user.getRole());
            
            // 更新结果消息，包含用户信息
            return UserService.Result.success("登录成功", user);
        }
        return result;
    }
    
    /**
     * 处理退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除session
        return "redirect:/";
    }
    
    /**
     * 跳转到个人中心页面
     */
    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            session.invalidate();
            return "redirect:/user/login";
        }
        
        model.addAttribute("title", "个人中心 - 校园失物招领平台");
        model.addAttribute("user", user);
        return "user/profile";
    }
    
    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    @ResponseBody
    public UserService.Result updateProfile(@RequestParam(required = false) String nickname,
                                           @RequestParam(required = false) String contact,
                                           @RequestParam(required = false) String avatar,
                                           HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("请先登录");
        }
        
        User user = new User();
        user.setUserId(userId);
        user.setNickname(nickname);
        user.setContact(contact);
        user.setAvatar(avatar);
        
        UserService.Result result = userService.updateUser(user);
        if (result.isSuccess()) {
            // 更新session中的用户信息
            User updatedUser = (User) result.getData();
            session.setAttribute("nickname", updatedUser.getNickname());
            session.setAttribute("contact", updatedUser.getContact());
            session.setAttribute("avatar", updatedUser.getAvatar());
        }
        return result;
    }
    
    /**
     * 修改密码页面
     */
    @GetMapping("/change-password")
    public String changePasswordPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        
        model.addAttribute("title", "修改密码 - 校园失物招领平台");
        return "user/change-password";
    }
    
    /**
     * 处理修改密码请求
     */
    @PostMapping("/change-password")
    @ResponseBody
    public UserService.Result changePassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword,
                                            @RequestParam String confirmPassword,
                                            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("请先登录");
        }
        
        if (!newPassword.equals(confirmPassword)) {
            return UserService.Result.error("两次输入的新密码不一致");
        }
        
        return userService.changePassword(userId, oldPassword, newPassword);
    }
    
    /**
     * 检查用户是否登录（用于AJAX验证）
     */
    @GetMapping("/check-login")
    @ResponseBody
    public UserService.Result checkLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("未登录");
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            session.invalidate();
            return UserService.Result.error("用户不存在");
        }
        
        return UserService.Result.success("已登录", user);
    }
    
    /**
     * 检查用户名是否已存在（用于注册时的实时验证）
     */
    @GetMapping("/check-username")
    @ResponseBody
    public UserService.Result checkUsername(@RequestParam String username) {
        if (username == null || username.isEmpty()) {
            return UserService.Result.error("用户名不能为空");
        }
        
        // 这里需要调用Mapper检查用户名是否存在
        // 暂时返回一个简单的响应
        return UserService.Result.success("检查完成");
    }
}
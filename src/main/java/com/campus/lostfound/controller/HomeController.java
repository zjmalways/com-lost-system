package com.campus.lostfound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.campus.lostfound.service.ItemService;
import com.campus.lostfound.service.UserService;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * 首页控制器
 */
@Controller
public class HomeController {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("title", "校园失物招领平台");
        model.addAttribute("welcome", "欢迎使用校园失物招领平台");
        
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("role", session.getAttribute("role"));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        // 获取平台统计数据
        Map<String, Object> platformStats = itemService.getPlatformStats();
        
        // 获取用户总数
        int totalUsers = userService.countAllUsers();
        
        // 添加统计数据到模型
        model.addAttribute("totalItems", platformStats.get("totalItems"));
        model.addAttribute("resolvedItems", platformStats.get("resolvedItems"));
        model.addAttribute("recoveryRate", platformStats.get("recoveryRate"));
        model.addAttribute("avgRecoveryTime", platformStats.get("avgRecoveryTime"));
        model.addAttribute("totalUsers", totalUsers);
        
        return "index";
    }
    
    /**
     * 关于页面
     */
    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("title", "关于我们 - 校园失物招领平台");
        
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("role", session.getAttribute("role"));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        return "about";
    }
}
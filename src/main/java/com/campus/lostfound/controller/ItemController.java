package com.campus.lostfound.controller;

import com.campus.lostfound.entity.Item;
import com.campus.lostfound.service.ImageService;
import com.campus.lostfound.service.ItemService;
import com.campus.lostfound.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品控制器
 */
@Controller
@RequestMapping("/items")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ImageService imageService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    /**
     * 物品列表页面
     */
    @GetMapping("")
    public String listPage(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Integer publishType,
                          @RequestParam(required = false) Integer itemType,
                          Model model,
                          HttpSession session) {
        // 检查用户是否登录（可选，因为列表页可以公开访问）
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userId", userId);
            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("role", session.getAttribute("role"));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        // 获取物品列表
        PageInfo<Item> pageInfo = itemService.getItemList(page, size, publishType, itemType, null);
        
        // 添加分页信息
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("items", pageInfo.getList());
        model.addAttribute("publishType", publishType);
        model.addAttribute("itemType", itemType);
        
        // 物品类型映射（用于前端显示）
        Map<Integer, String> itemTypeMap = new HashMap<>();
        itemTypeMap.put(1, "证件");
        itemTypeMap.put(2, "钥匙");
        itemTypeMap.put(3, "电子设备");
        itemTypeMap.put(4, "衣物");
        itemTypeMap.put(5, "钱包");
        itemTypeMap.put(6, "其他");
        model.addAttribute("itemTypeMap", itemTypeMap);
        
        // 格式化时间
        model.addAttribute("dateFormatter", DATE_FORMATTER);
        
        model.addAttribute("title", "失物招领 - 校园失物招领平台");
        return "items/list";
    }
    
    /**
     * 物品详情页面
     */
    @GetMapping("/{itemId}")
    public String detailPage(@PathVariable Long itemId, Model model, HttpSession session) {
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userId", userId);
            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("role", session.getAttribute("role"));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        // 获取物品详情
        UserService.Result result = itemService.getItemDetail(itemId);
        if (!result.isSuccess()) {
            // 如果物品不存在，重定向到列表页
            return "redirect:/items";
        }
        
        Item item = (Item) result.getData();
        model.addAttribute("item", item);
        
        // 检查当前用户是否为物品发布者
        if (userId != null && item.getPublisherId().equals(userId)) {
            model.addAttribute("isPublisher", true);
        } else {
            model.addAttribute("isPublisher", false);
        }
        
        // 格式化时间
        model.addAttribute("dateFormatter", DATE_FORMATTER);
        model.addAttribute("title", item.getTitle() + " - 校园失物招领平台");
        return "items/detail";
    }
    
    /**
     * 发布物品页面
     */
    @GetMapping("/publish")
    public String publishPage(@RequestParam(required = false) String type,
                             Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // 未登录，重定向到登录页
            return "redirect:/user/login";
        }
        
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("userId", userId);
        model.addAttribute("nickname", session.getAttribute("nickname"));
        model.addAttribute("role", session.getAttribute("role"));
        
        // 根据type参数设置默认发布类型
        // lost: 丢失 (publishType=0), found: 捡到 (publishType=1)
        if ("lost".equalsIgnoreCase(type)) {
            model.addAttribute("defaultPublishType", 0);
        } else if ("found".equalsIgnoreCase(type)) {
            model.addAttribute("defaultPublishType", 1);
        } else {
            // 默认设置为丢失
            model.addAttribute("defaultPublishType", 0);
        }
        
        // 预设当前时间为丢失/捡到时间
        model.addAttribute("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        
        model.addAttribute("title", "发布信息 - 校园失物招领平台");
        return "items/publish";
    }
    
    /**
     * 我丢了东西 - 快速发布丢失物品
     */
    @GetMapping("/lost")
    public String lostPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // 未登录，重定向到登录页
            return "redirect:/user/login";
        }
        
        // 重定向到发布页面，设置类型为丢失
        return "redirect:/items/publish?type=lost";
    }
    
    /**
     * 我捡到东西 - 快速发布捡到物品
     */
    @GetMapping("/found")
    public String foundPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // 未登录，重定向到登录页
            return "redirect:/user/login";
        }
        
        // 重定向到发布页面，设置类型为捡到
        return "redirect:/items/publish?type=found";
    }
    
    /**
     * 处理发布物品请求
     */
    @PostMapping("/publish")
    @ResponseBody
    public UserService.Result publish(@RequestParam Integer publishType,
                                     @RequestParam Integer itemType,
                                     @RequestParam String title,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) String features,
                                     @RequestParam(required = false) String images,
                                     @RequestParam String location,
                                     @RequestParam String eventTime,
                                     @RequestParam(required = false) String storageLocation,
                                     HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("请先登录");
        }
        
        // 创建物品对象
        Item item = new Item();
        item.setPublishType(publishType);
        item.setItemType(itemType);
        item.setTitle(title);
        item.setDescription(description);
        item.setFeatures(features);
        item.setImages(images);
        item.setLocation(location);
        
        // 解析时间
        try {
            LocalDateTime eventDateTime = LocalDateTime.parse(eventTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            item.setEventTime(eventDateTime);
        } catch (Exception e) {
            item.setEventTime(LocalDateTime.now());
        }
        
        item.setStorageLocation(storageLocation);
        
        // 调用服务发布物品
        return itemService.publishItem(item, userId);
    }
    
    /**
     * 编辑物品页面
     */
    @GetMapping("/edit/{itemId}")
    public String editPage(@PathVariable Long itemId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        
        // 获取物品详情
        UserService.Result result = itemService.getItemDetail(itemId);
        if (!result.isSuccess()) {
            return "redirect:/items";
        }
        
        Item item = (Item) result.getData();
        
        // 检查权限
        if (!item.getPublisherId().equals(userId)) {
            // 不是发布者，重定向到详情页
            return "redirect:/items/" + itemId;
        }
        
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("userId", userId);
        model.addAttribute("nickname", session.getAttribute("nickname"));
        model.addAttribute("role", session.getAttribute("role"));
        
        model.addAttribute("item", item);
        
        // 格式化时间
        String formattedTime = item.getEventTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        model.addAttribute("formattedEventTime", formattedTime);
        
        model.addAttribute("title", "编辑信息 - 校园失物招领平台");
        return "items/edit";
    }
    
    /**
     * 处理更新物品请求
     */
    @PostMapping("/update")
    @ResponseBody
    public UserService.Result update(@RequestParam Long itemId,
                                    @RequestParam String title,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) String features,
                                    @RequestParam(required = false) String images,
                                    @RequestParam String location,
                                    @RequestParam String eventTime,
                                    @RequestParam(required = false) String storageLocation,
                                    @RequestParam(required = false) String contact,
                                    HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("请先登录");
        }
        
        // 创建物品对象
        Item item = new Item();
        item.setItemId(itemId);
        item.setTitle(title);
        item.setDescription(description);
        item.setFeatures(features);
        item.setImages(images);
        item.setLocation(location);
        
        // 解析时间
        try {
            LocalDateTime eventDateTime = LocalDateTime.parse(eventTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            item.setEventTime(eventDateTime);
        } catch (Exception e) {
            item.setEventTime(LocalDateTime.now());
        }
        
        item.setStorageLocation(storageLocation);
        item.setContact(contact);
        
        // 调用服务更新物品
        return itemService.updateItem(item, userId);
    }
    
    /**
     * 处理删除物品请求
     */
    @PostMapping("/delete")
    @ResponseBody
    public UserService.Result delete(@RequestParam Long itemId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("请先登录");
        }
        
        return itemService.deleteItem(itemId, userId);
    }
    
    /**
     * 搜索物品
     */
    @GetMapping("/search")
    public String searchPage(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model,
                            HttpSession session) {
        // 检查用户是否登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userId", userId);
            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("role", session.getAttribute("role"));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        // 搜索物品
        PageInfo<Item> pageInfo = itemService.searchItems(keyword, page, size);
        
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("items", pageInfo.getList());
        model.addAttribute("keyword", keyword);
        
        // 物品类型映射
        Map<Integer, String> itemTypeMap = new HashMap<>();
        itemTypeMap.put(1, "证件");
        itemTypeMap.put(2, "钥匙");
        itemTypeMap.put(3, "电子设备");
        itemTypeMap.put(4, "衣物");
        itemTypeMap.put(5, "钱包");
        itemTypeMap.put(6, "其他");
        model.addAttribute("itemTypeMap", itemTypeMap);
        
        // 格式化时间
        model.addAttribute("dateFormatter", DATE_FORMATTER);
        
        model.addAttribute("title", "搜索: " + keyword + " - 校园失物招领平台");
        return "items/search";
    }
    
    /**
     * 更新物品状态（找回/归还）
     */
    @PostMapping("/update-status")
    @ResponseBody
    public UserService.Result updateStatus(@RequestParam Long itemId,
                                          @RequestParam Integer status,
                                          HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return UserService.Result.error("请先登录");
        }
        
        return itemService.updateItemStatus(itemId, status, userId);
    }
    
    /**
     * 我的发布页面
     */
    @GetMapping("/my")
    public String myItemsPage(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("userId", userId);
        model.addAttribute("nickname", session.getAttribute("nickname"));
        model.addAttribute("role", session.getAttribute("role"));
        
        // 获取用户发布的物品
        PageInfo<Item> pageInfo = itemService.getUserItems(userId, page, size);
        
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("items", pageInfo.getList());
        
        // 物品类型映射
        Map<Integer, String> itemTypeMap = new HashMap<>();
        itemTypeMap.put(1, "证件");
        itemTypeMap.put(2, "钥匙");
        itemTypeMap.put(3, "电子设备");
        itemTypeMap.put(4, "衣物");
        itemTypeMap.put(5, "钱包");
        itemTypeMap.put(6, "其他");
        model.addAttribute("itemTypeMap", itemTypeMap);
        
        // 格式化时间
        model.addAttribute("dateFormatter", DATE_FORMATTER);
        
        model.addAttribute("title", "我的发布 - 校园失物招领平台");
        return "items/my";
    }
    
    /**
     * 上传图片
     */
    @PostMapping("/upload-image")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file,
                                          HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        try {
            // 上传图片
            String imageUrl = imageService.uploadImage(file);
            
            result.put("success", true);
            result.put("message", "图片上传成功");
            result.put("imageUrl", imageUrl);
            return result;
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "图片上传失败：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 上传多张图片
     */
    @PostMapping("/upload-images")
    @ResponseBody
    public Map<String, Object> uploadImages(@RequestParam("files") List<MultipartFile> files,
                                           HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        try {
            // 上传图片
            String imageUrls = imageService.uploadImages(files);
            
            result.put("success", true);
            result.put("message", "图片上传成功");
            result.put("imageUrls", imageUrls);
            return result;
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "图片上传失败：" + e.getMessage());
            return result;
        }
    }
}
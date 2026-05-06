package com.campus.lostfound.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 图片上传服务
 */
@Service
@Slf4j
public class ImageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * 上传单个图片文件
     * @param file 上传的文件
     * @return 图片访问URL
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        
        // 验证文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String extension = getExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("不支持的文件格式，仅支持: " + ALLOWED_EXTENSIONS);
        }
        
        try {
            // 生成唯一文件名
            String filename = generateFilename(extension);
            
            // 创建日期目录
            String datePath = LocalDateTime.now().format(DATE_FORMATTER);
            Path targetDir = Paths.get(uploadDir, datePath);
            
            // 确保目录存在
            Files.createDirectories(targetDir);
            
            // 保存文件
            Path targetPath = targetDir.resolve(filename);
            file.transferTo(targetPath.toFile());
            
            // 生成访问URL
            String imageUrl = "/upload/" + datePath + "/" + filename;
            log.info("图片上传成功: {}, 大小: {} bytes", imageUrl, file.getSize());
            
            return imageUrl;
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传多个图片文件
     * @param files 上传的文件列表
     * @return 图片访问URL列表（逗号分隔）
     */
    public String uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }
        
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String imageUrl = uploadImage(file);
                    imageUrls.add(imageUrl);
                } catch (Exception e) {
                    log.warn("上传图片失败: {}", e.getMessage());
                    // 跳过失败的文件，继续上传其他文件
                }
            }
        }
        
        return String.join(",", imageUrls);
    }
    
    /**
     * 删除图片文件
     * @param imageUrl 图片URL
     * @return 是否删除成功
     */
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取文件路径
            // URL格式: /upload/2024/01/01/uuid.jpg
            String filePath = imageUrl.replaceFirst("^/upload/", "");
            Path imagePath = Paths.get(uploadDir, filePath);
            
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                log.info("图片删除成功: {}", imageUrl);
                return true;
            } else {
                log.warn("图片文件不存在: {}", imagePath);
                return false;
            }
        } catch (IOException e) {
            log.error("图片删除失败", e);
            return false;
        }
    }
    
    /**
     * 删除多个图片文件
     * @param imageUrls 逗号分隔的图片URL列表
     * @return 是否全部删除成功
     */
    public boolean deleteImages(String imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return true;
        }
        
        String[] urls = imageUrls.split(",");
        boolean allSuccess = true;
        
        for (String url : urls) {
            if (!url.trim().isEmpty()) {
                boolean success = deleteImage(url.trim());
                if (!success) {
                    allSuccess = false;
                }
            }
        }
        
        return allSuccess;
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFilename(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + extension;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }
    
    /**
     * 检查上传目录是否存在，如果不存在则创建
     */
    @PostConstruct
    public void initUploadDir() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            throw new RuntimeException("创建上传目录失败", e);
        }
    }
}
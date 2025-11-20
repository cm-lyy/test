package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

/**
 * 文件相关操作接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    // 表示本地磁盘文件的存储路径
    private static final String filePath = System.getProperty("user.dir") + "/upload/";
    @Value("${fileBaseUrl}")
    private String fileBaseUrl;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        // 定义文件的唯一标识
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        // 拼接完整的文件存储路径
        String realFilePath = filePath + fileName;
        try {
            if (!FileUtil.isDirectory(filePath)) {
                FileUtil.mkdir(filePath);
            }
            FileUtil.writeBytes(file.getBytes(), realFilePath);
        } catch (IOException e) {
            System.out.println("文件上传错误");
        }
        String url = fileBaseUrl + "/files/upload/" + fileName;
        System.out.println(fileName);
        System.out.println(realFilePath);
        System.out.println(url);
        return Result.success(url);
    }

    /**
     * 文件下载
     */
    @GetMapping("/upload/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        System.out.println("Download method called for file: " + fileName); // 打印请求的文件名
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
            String realFilePath = filePath + decodedFileName;
            System.out.println("Attempting to access file: " + realFilePath); // 打印文件路径

            // 读取文件并返回响应
            byte[] bytes = FileUtil.readBytes(realFilePath);
            // 写出文件数据到响应流
            ServletOutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            System.out.println("File not found: " + fileName);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);  // 返回 404 错误
            e.printStackTrace();
        }
    }

}

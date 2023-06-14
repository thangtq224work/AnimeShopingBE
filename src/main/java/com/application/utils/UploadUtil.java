package com.application.utils;

import com.application.inject.UploadConfig;
import com.application.entity.ProductImage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Component
@Slf4j
public class UploadUtil {
    UploadConfig uploadConfig;
    UploadResolve uploadResolve;
    HttpServletRequest request;

    private Path root;

    public UploadUtil(UploadConfig uploadConfig, HttpServletRequest request, UploadResolve uploadResolve) {
        this.uploadConfig = uploadConfig;
        this.request = request;
        this.uploadResolve = uploadResolve;
        this.root = Paths.get(uploadConfig.getDirectory());
    }

    public String[] upload(MultipartFile[] files) throws IOException {
        String[] filesReturn = new String[files.length];
        try {
            for (int i = 0; i < filesReturn.length; i++) {
                try {
                    if (Files.exists(root) == false) {
                        Files.createDirectories(root);
                    }
                    String fileOriginal = files[i].getOriginalFilename();
                    String contentType = fileOriginal.substring(fileOriginal.lastIndexOf("."));
                    String fileName = (files[i].getOriginalFilename() + UUID.randomUUID().toString()).substring(0, 8);
                    String url = Calendar.getInstance().getTime().getTime() + UUID.randomUUID().toString() + fileName + contentType;
                    filesReturn[i] = url;
                    Files.copy(files[i].getInputStream(), this.root.resolve(url), StandardCopyOption.REPLACE_EXISTING);
                } catch (IllegalStateException | IOException e) {
                    log.error(e.getMessage());
                    throw e;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return filesReturn;
    }

    public Map<String, Object> show(String url) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Resource resource = uploadResolve.loadFile(url);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            System.out.println(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            throw new RuntimeException("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        map.put("contentType", contentType);
        map.put("image", Files.readAllBytes(this.root.resolve(url)));
        return map;


    }

    public void deleteImage(ProductImage images) throws IOException {
        Resource resource = uploadResolve.loadFile(images.getUrl());
        Files.delete(resource.getFile().toPath());
    }

}

package com.application.utils;

import com.application.inject.UploadConfig;
import com.application.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class UploadResolve {
    private final Path fileStorageLocation;

    public UploadResolve(UploadConfig uploadConfig) {
        this.fileStorageLocation = Paths.get(uploadConfig.getDirectory())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info(fileStorageLocation.toString());
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Resource loadFile(String fileName) {
        Path path = null;
        try{
            path = fileStorageLocation.resolve(fileName).normalize();
        }catch (InvalidPathException exception){
            log.warn("invalid path : "+fileName);
            throw new NotFoundException("File not found");
        }
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new NotFoundException("Image not found");
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found");
        }

    }
}

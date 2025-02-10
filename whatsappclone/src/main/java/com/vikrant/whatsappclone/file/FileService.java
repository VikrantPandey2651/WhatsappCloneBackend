package com.vikrant.whatsappclone.file;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
public class FileService {

    @Value("${application.file.uploads.media.output-path}")
    private String fileUploadPath;

    public String saveFile(
                           @NonNull String senderId,
                           @NonNull MultipartFile file){
        
        final String fileUploadSubPath = "users" + File.separator + senderId;
        return fileUpload(file, fileUploadSubPath);
      
    }

    private String fileUpload(@NonNull MultipartFile file, @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Failed to create the target folder at the given location. {}",targetFolder);

            }
        }

        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + currentTimeMillis() + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try{
            Files.write(targetPath,file.getBytes());
            log.info("File saved to {}",targetPath);
            return targetFilePath;

        } catch (Exception e) {
            log.error("File was not saved "+ e.getMessage());
        }
        return null;


    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename == null || originalFilename.isEmpty()){
            return "";
        }

        int lastDotIndex = originalFilename.lastIndexOf(".");
        if(lastDotIndex == -1){
            return "";
        }
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }


}

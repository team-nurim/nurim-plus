//package org.nurim.nurim.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//public class FileStorageService {
//
//    @Value("${org.yeolmae.upload.path}")
//    private String uploadPath;
//
//    // 파일 저장 로직
//    public String storeFile(MultipartFile file) throws IOException {
//        // 파일명을 UUID로 생성하여 중복 방지
//        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//        Path filePath = Paths.get(uploadPath + fileName);
//        Files.copy(file.getInputStream(), filePath);
//
//        return filePath.toString();
//    }
//
//}

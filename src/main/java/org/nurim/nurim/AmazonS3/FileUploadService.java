package org.nurim.nurim.AmazonS3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileUploadService {

    private final AmazonS3ResourceStorage amazonS3ResourceStorage;

    private final AmazonS3 amazonS3; // Amazon S3 클라이언트

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public FileDetail save(MultipartFile multipartFile) {
        FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
        amazonS3ResourceStorage.store(fileDetail.getPath(), multipartFile);

        return fileDetail;
    }

    public boolean deleteFile(String uuid) {
        try {
            // S3에서 파일 삭제
//            amazonS3.deleteObject(bucket, uuid);
//            amazonS3.delete
            amazonS3Client.deleteObject(bucket, uuid);
            return true;
        } catch (AmazonServiceException e) {
            // 삭제 실패 시 에러 로그 출력
            log.error("Failed to delete file from S3: " + e.getMessage());
            return false;
        }
    }

    // 이미지 저장 및 url, key 값(uuid 포함) 반환
    public Map<String, String> saveUrlAndKey(MultipartFile multipartFile) {
        // S3에 저장
        FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
        amazonS3ResourceStorage.store(fileDetail.getPath(), multipartFile);

        // 업로드된 파일의 URL 생성
        String url = String.valueOf(amazonS3Client.getUrl(bucket, fileDetail.getPath().toString()));

        // 파일을 S3에 업로드하고 UUID가 포함된 키 값을 받아옴
        String key = fileDetail.getPath();

        // URL과 UUID가 포함된 키 값을 Map에 저장하여 반환
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("key", key);

        return result;
    }

}


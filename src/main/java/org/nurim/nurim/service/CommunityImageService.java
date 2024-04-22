package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.dto.post.upload.UploadFileRequest;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.CommunityImage;
import org.nurim.nurim.repository.CommunityImageRepository;
import org.nurim.nurim.repository.CommunityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CommunityImageService {

    private final CommunityImageRepository communityImageRepository;

    private final CommunityRepository communityRepository;

    private final FileUploadService fileUploadService;

    @Transactional
    public void saveImages(Long communityId, List<CommunityImage> communityImages){

        Community community = communityRepository.findById(communityId)
                .orElseThrow(()-> new EntityNotFoundException("커뮤니티 아이디를 조회할 수 없습니다." + communityId));

        for (CommunityImage communityImage : communityImages){
            communityImage.setCommunity(community);

            communityImageRepository.saveAll(communityImages);
        }
    }
    @Transactional
    public boolean deleteCommunityImage(Long communityImageId){

        CommunityImage communityImage = communityImageRepository.findById(communityImageId)
                .orElseThrow(()-> new EntityNotFoundException("게시물 이미지 아이디를 찾을 수 없습니다" + communityImageId));

        fileUploadService.deleteFile(communityImage.getFileKey());

        communityImageRepository.deleteById(communityImageId);

        return true;

    }
}

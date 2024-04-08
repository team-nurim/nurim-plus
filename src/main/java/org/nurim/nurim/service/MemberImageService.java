package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.image.DeleteMemberImageResponse;
import org.nurim.nurim.domain.dto.image.UpdateMemberImageRequest;
import org.nurim.nurim.domain.dto.image.UpdateMemberImageResponse;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;

    // 프로필 이미지 수정
    @Transactional
    public UpdateMemberImageResponse updateMemberImage(Long memberId, MultipartFile profileImage) {

        // 파일 처리 로직(파일 저장)
        String imageUrl = saveProfileImage(file); // 가상의 메소드, 실제 파일 처리 로직 필요

        MemberImage foundProfile = memberImageRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("😑해당 profileImageId로 조회된 이미지 정보가 없습니다."));

        foundProfile.update(imageUrl);

        return new UpdateMemberImageResponse(foundProfile.getProfileImageId(), foundProfile.getMemberProfileImage());
    }

//    @Transactional
//    public UpdateMemberImageResponse updateMemberImage(Long profileImageId, UpdateMemberImageRequest request) {
//
//        // 파일 처리 로직(파일 저장, url)
////        String imageUrl = saveProfileImage(file); // 가상의 메소드, 실제 파일 처리 로직 필요
//
//        MemberImage foundProfile = memberImageRepository.findById(profileImageId)
//                .orElseThrow(() -> new EntityNotFoundException("😑해당 profileImageId로 조회된 이미지 정보가 없습니다."));
//
//        foundProfile.update(request.getMemberProfileImage());
//
//        return new UpdateMemberImageResponse(foundProfile.getProfileImageId(), foundProfile.getMemberProfileImage());
//    }

    @Transactional
    public DeleteMemberImageResponse deleteMemberImage(Long memberId) {

        return null;

    }
}

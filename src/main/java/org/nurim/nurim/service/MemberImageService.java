package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.member.UpdateMemberImageRequest;
import org.nurim.nurim.domain.dto.member.UpdateMemberImageResponse;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;

    // 프로필 이미지 수정
    @Transactional
    public UpdateMemberImageResponse updateMemberImage(Long profileImageId, UpdateMemberImageRequest request) {

        MemberImage foundProfile = memberImageRepository.findById(profileImageId)
                .orElseThrow(() -> new EntityNotFoundException("😑해당 profileImageId로 조회된 이미지 정보가 없습니다."));

        foundProfile.update(request.getMemberProfileImage());

        return new UpdateMemberImageResponse(foundProfile.getProfileImageId(), foundProfile.getMemberProfileImage());
    }

}

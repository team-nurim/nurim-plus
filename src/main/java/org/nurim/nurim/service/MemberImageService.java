package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.MemberImage;
import org.nurim.nurim.repository.MemberImageRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;
    private final MemberRepository memberRepository;

    // 프로필 이미지 업로드
    @Transactional
    public void saveImage(Long memberId, String memberProfileImage, String profileName) {
        // 해당 memberId에 대한 MemberImage가 이미 존재하는지 확인
        Optional<MemberImage> existingImage = memberImageRepository.findByMember_MemberId(memberId);

        if (existingImage.isPresent()) {
            // 이미지가 존재하면 업데이트
            MemberImage memberImage = existingImage.get();
            memberImage.setMemberProfileImage(memberProfileImage);
            memberImage.setProfileName(profileName);

            // memberId를 사용하여 해당하는 Member 엔티티를 가져와서 설정
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with id : " + memberId));
            memberImage.setMember(member);

            memberImageRepository.save(memberImage);
        } else {
            // 이미지가 존재하지 않으면 예외 throw
            throw new RuntimeException("Member image not found for memberId: " + memberId);
        }

    }

    // 프로필 이미지 삭제
    @Transactional
    public Map<String, Boolean> deleteImage(Long memberId) {

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemovedFromDatabase = false;
        boolean isRemovedFromS3 = false;

        if (memberId != null) {
            try {
                // 데이터베이스에서 이미지 정보 삭제
                memberImageRepository.deleteByMemberId(memberId);
                isRemovedFromDatabase = true; // 삭제 성공 시, true

                // S3에서 이미지 삭제

                // 초기 프로필 이미지로 변경

            } catch (Exception e) {
                // 데이터베이스에서 삭제 실패 시, 에러
                log.error("😀Failed to delete imagge from the database: " + e.getMessage());
            }
        } else {
            log.warn("해당 memberId가 존재하지 않아 삭제할 수 없습니다.");
        }

        response.put("result", isRemovedFromDatabase);
        return response;

    }

    public boolean setDefaultImage (Long memberId) {
        // 기본 이미지 uuid
        String defaultImage = "8590a967-d772-4872-9a98-4c7e3ad434f9";

        try {
            memberImageRepository.updateByMemberId(defaultImage, memberId);
            return true;
        } catch (Exception e) {
            log.error("기본 이미지로 반환 실패" + e.getMessage());
            return false;
        }
    }

}

package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.AmazonS3.FileUploadService;
import org.nurim.nurim.domain.entity.Expert;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.ExpertRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class ExpertService {

    private final ExpertRepository expertRepository;
    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;

    // 자격증 이미지 업로드
    @Transactional
    public void saveImage(Long memberId, String expertFile, String expertFileName) {

        Optional<Expert> existingImage = expertRepository.findByMember_MemberId(memberId);

        if (existingImage.isPresent()) {
            // 이미지가 존재하면 업데이트
            Expert expert = existingImage.get();
            expert.setExpertFile(expertFile);
            expert.setExpertFileName(expertFileName);

            // memberId를 사용하여 해당하는 Member 엔티티 가져와서 설정
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with id : " + memberId));
            expert.setMember(member);

            expertRepository.save(expert);
        } else {
            // 이미지가 존재하지 않으면 예외 throw
            throw new RuntimeException("Expert image not found for memberId: " + memberId);
        }

    }

    // 자격증 이미지 삭제
    @Transactional
    public boolean deleteAndSetDefault(Long memberId) {
        Optional<Expert> expertOptional = expertRepository.findByMember_MemberId(memberId);
        if (expertOptional.isPresent()) {
            Expert expert = expertOptional.get();

            // S3에서 파일 삭제
            boolean isRemovedFromS3 = fileUploadService.deleteFile(expert.getExpertFileName());

            // S3에서 이미지 삭제에 성공하면 DB에서 기본값으로 변경
            if (isRemovedFromS3) {
                // 기본값
                String defaultExpert = "증빙서류가 등록되지 않았습니다.";

                // DB에서 해당 회원의 이미지 기본값으로 변경
                expert.setExpertFile(defaultExpert);
                expert.setExpertFileName(defaultExpert);
                expertRepository.save(expert);

                return true;
            } else {
                // S3에서의 이미지 삭제 실패 시
                return false;
            }
        }  else {
            // 해당 회원의 이미지가 존재하지 않을 경우
            return false;
        }
    }
}

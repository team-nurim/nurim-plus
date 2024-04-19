package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.entity.Expert;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.repository.ExpertRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class ExpertService {

    private final ExpertRepository expertRepository;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 자격증 이미지 등록
    @Transactional
    public void saveImage(Long memberId, String expertFile, String expertFileName) {

        Expert expert = new Expert();
        expert.setExpertFile(expertFile);
        expert.setExpertFileName(expertFileName);

        // memberId를 사용하여 해당하는 Member 엔티티를 가져와서 설정
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with id : " + memberId));
        expert.setMember(member);

        expertRepository.save(expert);

    }

//    // 자격증 이미지 조회
//    public String getExpertImageFileName(Long memberId) {
//
//        Optional<Expert> expertOptional = expertRepository.findByMember_MemberId(memberId);
//
//        return expertOptional.map(Expert::getExpertFile).orElse("자격증 이미지가 존재하지 않습니다.");
//    }

    // 자격증 이미지 삭제
    @Transactional
    public Map<String, Boolean> deleteExpertFile(Long memberId){

        Map<String, Boolean> response = new HashMap<>();
        boolean isRemovedFromDatabase = false;

        if (memberId != null) {
            try {
                // 데이터베이스에서 이미지 정보 삭제
                expertRepository.deleteByMemberID(memberId);
                isRemovedFromDatabase = true; // 삭제 성공 시, true

            } catch (Exception e) {
                // 데이터베이스에서 삭제 실패 시, 에러
                log.error("😀memberId로 데이터베이스 삭제 실패 : " + e.getMessage());
            }
        } else {
            log.warn("해당 memberId가 존재하지 않아 삭제할 수 없습니다.");
        }

        response.put("result", isRemovedFromDatabase);
        return response;

    }

}

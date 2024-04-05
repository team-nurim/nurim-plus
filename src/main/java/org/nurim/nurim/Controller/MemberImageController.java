package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.image.UpdateMemberImageResponse;
import org.nurim.nurim.service.MemberImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class MemberImageController {

//    private final MemberService memberService;
    private final MemberImageService memberImageService;

    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberImageResponse> updateProfileImage(@PathVariable Long memberId, @RequestParam("profileImage")MultipartFile profileImage) {

        UpdateMemberImageResponse response = memberImageService.updateMemberImage(memberId, profileImage);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteProfileImage(@PathVariable Long memberId) {
        memberImageService.deleteMemberImage(memberId);

        return new ResponseEntity<>();
    }

}

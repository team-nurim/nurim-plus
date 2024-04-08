package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.image.DeleteMemberImageResponse;
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
    public ResponseEntity<?> updateProfileImage(@PathVariable Long memberId, @RequestParam("profileImage")MultipartFile profileImage) {

        UpdateMemberImageResponse response = memberImageService.updateMemberImage(memberId, profileImage);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberImageResponse> deleteProfileImage(@PathVariable Long memberId) {

        DeleteMemberImageResponse response = memberImageService.deleteMemberImage(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

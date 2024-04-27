package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.nurim.nurim.service.HeartService;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HeartController {

    private final HeartService heartService;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/community/{communityId}/like/{accessToken}")
    @Operation(summary = "게시물 회원아이디당 1개의 좋아요 기능 구현")
    public ResponseEntity<String> likeCommunity(@PathVariable Long communityId, @PathVariable String accessToken) {
        try {
            heartService.recommendCommunity(communityId, accessToken);
            return ResponseEntity.ok("커뮤니티에 좋아요를 추가했습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/community/{communityId}/cancelLike/{accessToken}")
    @Operation(summary = "좋아요 취소")
    public ResponseEntity<String> cancelRecommendCommunity(@PathVariable Long communityId, @PathVariable String accessToken){
        try{
            heartService.cancelRecommendCommunity(communityId,accessToken);
            return ResponseEntity.ok("커뮤니티 좋아요를 취소했습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status((HttpStatus.NOT_FOUND)).body(e.getMessage());
        }
    }
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/community/{communityId}/isLiked/{accessToken}")
    @Operation(summary = "좋아요 확인")
    public ResponseEntity<Boolean> isCommunityLikedByMember(@PathVariable Long communityId, @PathVariable String accessToken){
        boolean isLiked = heartService.isCommunityLiked(communityId, accessToken);
        return ResponseEntity.ok(isLiked);
    }
}


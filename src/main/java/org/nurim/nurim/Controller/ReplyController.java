package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.CreateCommunityResponse;
import org.nurim.nurim.domain.dto.reply.*;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.repository.ReplyRepository;
import org.nurim.nurim.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reply", description = "댓글 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/community/{communityId}/replyCreate")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<CreateReplyResponse> createReply(@PathVariable Long communityId, @RequestHeader("Authorization") String token, @RequestBody CreateReplyRequest request) {
        CreateReplyResponse response = replyService.replyCreate(token, communityId, request);
        return ResponseEntity.ok().body(response);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/community/{communityId}/replyRead")
    @Operation(summary = "댓글 리스트 조회")
    public ResponseEntity<List<ReadReplyResponse>>readReplyList(@PathVariable Long communityId) {
        List<ReadReplyResponse> response = replyService.getRepliesByCommunityId(communityId);
        return ResponseEntity.ok().body(response);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/replyUpdate/{replyId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<UpdateReplyResponse> updateReply(@PathVariable Long replyId,@RequestBody UpdateReplyRequest request){
        UpdateReplyResponse response = replyService.replyUpdate(replyId, request);
        return ResponseEntity.ok().body(response);
    }


    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/replyDelete/{replyId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<DeleteReplyResponse> deleteReply(@PathVariable Long replyId){
        DeleteReplyResponse response = replyService.replyDelete(replyId);
        return ResponseEntity.ok().body(response);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/myPage/{memberId}")
    @Operation(summary = "회원 아이디 별 댓글조회")
    public ResponseEntity<List<ReadReplyResponse>> ReadReply(@PathVariable Long memberId){
        List<ReadReplyResponse> readReplyResponses = replyService.getRepliesByMemberId(memberId);
        return ResponseEntity.ok().body(readReplyResponses);
    }
}
package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.CreateCommunityResponse;
import org.nurim.nurim.domain.dto.reply.*;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.repository.ReplyRepository;
import org.nurim.nurim.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/community/{communityId}/replyCreate")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<CreateReplyResponse> createReply(@PathVariable Long communityId, @RequestBody CreateReplyRequest request) {
        CreateReplyResponse response = replyService.replyCreate(communityId, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/community/{communityId}/replyRead")
    @Operation(summary = "댓글 리스트 조회")
    public ResponseEntity<List<ReadReplyResponse>>readReplyList(@PathVariable Long communityId) {
        List<ReadReplyResponse> response = replyService.getRepliesByCommunityId(communityId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/replyUpdate/{replyId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<UpdateReplyResponse> updateReply(@PathVariable Long replyId,@RequestBody UpdateReplyRequest request){
        UpdateReplyResponse response = replyService.replyUpdate(replyId, request);
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping("/replyDelete/{replyId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<DeleteReplyResponse> deleteReply(@PathVariable Long replyId){
        DeleteReplyResponse response = replyService.replyDelete(replyId);
        return ResponseEntity.ok().body(response);
    }
}

package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.CreateCommunityResponse;
import org.nurim.nurim.domain.dto.reply.*;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.repository.ReplyRepository;
import org.nurim.nurim.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/community/{communityId}/replyCreate")
    public ResponseEntity<CreateReplyResponse> createReply(@PathVariable Long communityId, @RequestBody CreateReplyRequest request) {
        CreateReplyResponse response = replyService.replyCreate(communityId, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/community/{communityId}/reply")
    public ResponseEntity<ReadReplyResponse> readReplyList(@PathVariable Long communityId) {
        ReadReplyResponse response = (ReadReplyResponse) replyService.getRepliesByCommunityId(communityId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/replyUpdate/{replyId}")
    public ResponseEntity<UpdateReplyResponse> updateReply(@PathVariable Long replyId,@RequestBody UpdateReplyRequest request){
        UpdateReplyResponse response = replyService.replyUpdate(replyId, request);
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping("/community/{communityId}/reply/{replyId}")
    public ResponseEntity<DeleteReplyResponse> deleteReply(@PathVariable Long replyId){
        DeleteReplyResponse response = replyService.replyDelete(replyId);
        return ResponseEntity.ok().body(response);
    }
}

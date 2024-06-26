package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.reply.*;
import org.nurim.nurim.service.MemberService;
import org.nurim.nurim.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "Reply", description = "댓글 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final MemberService memberService;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/replyCreate/{communityId}/{accessToken}")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<CreateReplyResponse> createReply(@PathVariable Long communityId,@PathVariable String accessToken, @RequestBody CreateReplyRequest request) {
        CreateReplyResponse response = replyService.replyCreate(communityId,accessToken, request);
        return ResponseEntity.ok().body(response);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/community/{communityId}/replyRead")
    @Operation(summary = "댓글 리스트 조회")
    public ResponseEntity<List<ReadReplyResponse>>readReplyList(@PathVariable Long communityId) {
        List<ReadReplyResponse> response = replyService.getRepliesByCommunityId(communityId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/replyUpdate/{replyId}/{accessToken}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<UpdateReplyResponse> updateReply(@PathVariable Long replyId,@PathVariable String accessToken,@RequestBody UpdateReplyRequest request) {
        try {
            UpdateReplyResponse response = replyService.replyUpdate(replyId, accessToken, request);
            return ResponseEntity.ok().body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/replyDelete/{communityId}/{replyId}/{accessToken}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<DeleteReplyResponse> deleteReply(@PathVariable Long communityId,@PathVariable Long replyId, @PathVariable String accessToken){
        try{DeleteReplyResponse response = replyService.replyDelete(communityId,replyId,accessToken);
            return ResponseEntity.ok().body(response);
        }catch(AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/myPage/{memberId}")
    @Operation(summary = "회원 아이디 별 댓글조회")
    public ResponseEntity<List<ReadReplyResponse>> ReadReply(@PathVariable Long memberId){
        List<ReadReplyResponse> readReplyResponses = replyService.getRepliesByMemberId(memberId);
        return ResponseEntity.ok().body(readReplyResponses);
    }
}
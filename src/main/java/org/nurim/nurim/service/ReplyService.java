package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.reply.*;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.Reply;
import org.nurim.nurim.repository.CommunityRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.repository.ReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final CommunityRepository communityRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public CreateReplyResponse replyCreate(Long communityId,Long memberId, CreateReplyRequest request){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new EntityNotFoundException("memberId 가 없어요"));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Id 없어요"));
        Reply reply = Reply.builder()
                .member(member)
                .community(community)
                .replyText(request.getReplyText())
                .replyer(request.getReplyer())
                .build();
        Reply saveReply = replyRepository.save(reply);

        return new CreateReplyResponse(
                saveReply.getReplyId(),
                saveReply.getCommunity().getCommunityId(),
                saveReply.getReplyer(),
                saveReply.getReplyText(),
                saveReply.getReplyRegisterDate());
    }
    public List<ReadReplyResponse> getRepliesByCommunityId(Long communityId){
        List<Reply> replyList = replyRepository.findByCommunityCommunityId(communityId);
        return replyList.stream()
                .map(reply -> new ReadReplyResponse(
                        reply.getReplyId(),
                        reply.getCommunity().getCommunityId(),
                        reply.getReplyText(),
                        reply.getReplyer(),
                        reply.getReplyRegisterDate(),
                        reply.getReplyModifyDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public DeleteReplyResponse replyDelete(Long replyId){
        Reply findreply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Id 없어요"));

        replyRepository.delete(findreply);

        return new DeleteReplyResponse(findreply.getReplyId());
    }
    @Transactional
    public UpdateReplyResponse replyUpdate(Long replyId, UpdateReplyRequest request){
        Reply findReply = replyRepository.findById(replyId)
                .orElseThrow(()-> new EntityNotFoundException("Id 없어요"));

        findReply.update(request.getReplrText());

        return new UpdateReplyResponse(
                findReply.getReplyId(),
                findReply.getReplyer(),
                findReply.getReplyText(),
                findReply.getReplyModifyDate());
    }

    /**
     *멤버 아이디당 댓글 목록
     */
    public List<ReadReplyResponse> getRepliesByMemberId(Long memberId){
        List<Reply> replyList = replyRepository.findByMemberMemberId(memberId);
        return replyList.stream()
                .map(reply -> new ReadReplyResponse(
                        reply.getReplyId(),
                        reply.getMember().getMemberId(),
                        reply.getReplyText(),
                        reply.getReplyer(),
                        reply.getReplyRegisterDate(),
                        reply.getReplyModifyDate()))
                .collect(Collectors.toList());
    }
}

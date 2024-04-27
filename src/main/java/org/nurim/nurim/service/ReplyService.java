package org.nurim.nurim.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.reply.*;
import org.nurim.nurim.domain.entity.Community;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.domain.entity.Reply;
import org.nurim.nurim.repository.CommunityRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.nurim.nurim.repository.ReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final CommunityRepository communityRepository;

    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;

    @Transactional
    public CreateReplyResponse replyCreate(Long communityId,String memberEmail, CreateReplyRequest request){
        String token =  tokenProvider.getUsernameFromToken(memberEmail);
        Member member = memberRepository.findMemberByMemberEmail(token).orElseThrow(() -> new EntityNotFoundException("이메일이 없습니다."));
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
                saveReply.getMember().getMemberNickname(),
                saveReply.getMember().getMemberEmail(),
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
                        reply.getMember().getMemberNickname(),
                        reply.getMember().getMemberEmail(),
                        reply.getReplyRegisterDate(),
                        reply.getReplyModifyDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public DeleteReplyResponse replyDelete(Long communityId, Long replyId, String memberEmail) throws AccessDeniedException{
        String accessToken = tokenProvider.getUsernameFromToken(memberEmail);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(()-> new EntityNotFoundException("해당 커뮤니티 아이드를 찾을 수 없습니다."));

        Reply findreply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글 아이디를 찾을 수 없습니다."));

        Member member = memberRepository.findMemberByMemberEmail(accessToken)
                        .orElseThrow(()->new EntityNotFoundException("멤버 이메일이 없습니다."));

        if(Objects.equals(findreply.getMember().getMemberEmail(),member.getMemberEmail())){
            replyRepository.delete(findreply);
            return new DeleteReplyResponse(findreply.getReplyId());
        }else{
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }
    }
    @Transactional
    public UpdateReplyResponse replyUpdate(Long replyId,String memberEmail, UpdateReplyRequest request) throws AccessDeniedException{
        String accessToken = tokenProvider.getUsernameFromToken(memberEmail);

        Member member = memberRepository.findMemberByMemberEmail(accessToken)
                .orElseThrow(()->new EntityNotFoundException("회원 이메일이 없습니다."));

        Reply findReply = replyRepository.findById(replyId)
                .orElseThrow(()-> new EntityNotFoundException("Id 없어요"));
    if (Objects.equals(findReply.getMember().getMemberEmail(),member.getMemberEmail())){
        findReply.update(request.getReplyText());
        return new UpdateReplyResponse(
                findReply.getReplyId(),
                findReply.getReplyer(),
                findReply.getReplyText(),
                findReply.getReplyModifyDate());
    }else{
        throw new AccessDeniedException("댓글을 수정 할 권한이 없습니다.");
    }
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
                        reply.getMember().getMemberEmail(),
                        reply.getReplyText(),
                        reply.getReplyer(),
                        reply.getReplyRegisterDate(),
                        reply.getReplyModifyDate()))
                .collect(Collectors.toList());
    }
}

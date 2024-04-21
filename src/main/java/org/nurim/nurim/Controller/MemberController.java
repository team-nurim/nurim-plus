package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Members", description = "회원 정보 API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="*")
@RequestMapping("/api/v1/members")
@Log4j2
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "일반 회원 등록")
    @PostMapping("/user")
    public ResponseEntity<CreateMemberResponse> memberCreate(@RequestBody @Valid CreateMemberRequest request){

        CreateMemberResponse response = memberService.createMember(request);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Operation(summary = "관리자 회원 등록")
    @PostMapping("/admin")
    public ResponseEntity<CreateMemberResponse> adminCreate(@RequestBody @Valid CreateMemberRequest request){

        CreateMemberResponse response = memberService.createAdmin(request);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // 다른 회원 프로필 조회
    @Operation(summary = "회원 정보 단건 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<ReadMemberResponse> memberReadById(@PathVariable Long memberId) {

        ReadMemberResponse response = memberService.readMemberById(memberId);

        return  new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "JWT를 통한 Mypage 정보 불러오기")
    @GetMapping("/mypage")
    public ResponseEntity<ReadMemberResponse> getMyInfo(HttpServletRequest request){

        Member accessMember = memberService.getMember(request);
        ReadMemberResponse response = memberService.readMemberById(accessMember.getMemberId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> memberUpdate(@RequestBody UpdateMemberRequest request, HttpServletRequest httpRequest) {

        Member accessMember = memberService.getMember(httpRequest);
        UpdateMemberResponse response = memberService.updateMember(accessMember.getMemberId(), request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "회원 정보 삭제") // 회원가입이 이뤄지면 email에 대한 정보로 탈퇴 처리해야 할 듯
    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberResponse> memberDelete(@RequestBody HttpServletRequest httpRequest){

        Member accessMember = memberService.getMember(httpRequest);
        DeleteMemberResponse response = memberService.deleteMember(accessMember.getMemberId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

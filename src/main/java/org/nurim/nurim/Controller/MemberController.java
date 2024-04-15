package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "members", description = "회원 정보 API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="*")
@RequestMapping("/api/v1/members")
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

    @Operation(summary = "회원 정보 입력")
    @PostMapping("/memberInfo")
    public ResponseEntity<CreateMemberResponse> memberInfoCreate(@RequestBody @Valid CreateMemberInfoRequest request) {

        CreateMemberResponse response = memberService.createMemberInfo(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "회원 정보 단건 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<ReadMemberResponse> memberReadById(@PathVariable Long memberId) {

        ReadMemberResponse response = memberService.readMemberById(memberId);

        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 💌 검토 필요 (추가)
    @Operation(summary = "JWT를 통한 Mypage 정보 불러오기")
    @GetMapping("/mypage")
    public ResponseEntity<ReadMemberResponse> getMyInfo(){

        Member accessMember = memberService.getMember();

        ReadMemberResponse response = memberService.readMemberById(accessMember.getMemberId());

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // 다른 회원 프로필 조회
//    @Operation(summary = "다른 회원 프로필 정보 조회")
//    @GetMapping("/user/{username}")
//    public ResponseEntity<ReadMemberResponse> readMemberByMemberEmail(@PathVariable String memberEmail) {
//
//        Member targetMember = memberService.readMemberByMemberEmail(memberEmail);
//
//        ReadMemberResponse response = memberService.readMemberById(targetMember.getMemberId());
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//
//    }

//    @Operation(summary = "회원 정보 삭제") // 회원가입이 이뤄지면 email에 대한 정보로 탈퇴 처리해야 할 듯
//    @DeleteMapping("/{memberId}")
//    public ResponseEntity<DeleteMemberResponse> memberInfoDelete(@PathVariable Long memberId){
//
//        DeleteMemberResponse response = memberService.deleteMember(memberId);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//
//    }

    // 💌 검토 필요 (로그인한 사용자만 회원탈퇴 가능)
    @Operation(summary = "회원 정보 삭제") // 회원가입이 이뤄지면 email에 대한 정보로 탈퇴 처리해야 할 듯
    @DeleteMapping
    public ResponseEntity<DeleteMemberResponse> memberDelete(){

        Member accessMember = memberService.getMember();

        DeleteMemberResponse response = memberService.deleteMember(accessMember.getMemberId());

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // 💌 검토 필요 (로그인한 사용자가 본인 정보만 수정 가능)
    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> memberUpdate(@RequestBody UpdateMemberRequest request) {

        Member accessMember = memberService.getMember();

        UpdateMemberResponse response = memberService.updateMember(accessMember.getMemberId(), request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

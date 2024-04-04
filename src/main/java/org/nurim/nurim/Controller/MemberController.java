package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "members", description = "회원 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "개인 정보 입력")
    @PostMapping
    public ResponseEntity<CreateMemberResponse> memberInfoCreate(@RequestBody CreateMemberRequest request){

        CreateMemberResponse response = memberService.createMemberInfo(request);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Operation(summary = "개인 정보 단건 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<ReadMemberResponse> memberReadById(@PathVariable Long memberId) {
        ReadMemberResponse response = memberService.readMemberById(memberId);

        return  new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "개인 정보 수정")
    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> memberInfoUpdate(@PathVariable Long memberId, @RequestBody UpdateMemberRequest request){

        UpdateMemberResponse response = memberService.updateMemberInfo(memberId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "개인 정보 삭제") // 회원가입이 이뤄지면 email에 대한 정보로 탈퇴 처리해야 할 듯
    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberResponse> memberInfoDelete(@PathVariable Long memberId){

        DeleteMemberResponse response = memberService.deleteMemberInfo(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<PatchMemberResponse> memberPartUpdate(@PathVariable Long memberId, @RequestBody PatchMemberRequest request) {

        PatchMemberResponse response = memberService.updateMemberPart(memberId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

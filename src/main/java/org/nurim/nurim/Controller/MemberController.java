package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.member.*;
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

    @Operation(summary = "회원 정보 등록")
    @PostMapping
    public ResponseEntity<CreateMemberResponse> memberInfoCreate(@RequestBody @Valid CreateMemberRequest request){

        CreateMemberResponse response = memberService.createMember(request);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Operation(summary = "회원 정보 단건 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<ReadMemberResponse> memberReadById(@PathVariable Long memberId) {
        ReadMemberResponse response = memberService.readMemberById(memberId);

        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> memberInfoUpdate(@PathVariable Long memberId, @RequestBody UpdateMemberRequest request){

        UpdateMemberResponse response = memberService.updateMember(memberId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "회원 정보 삭제") // 회원가입이 이뤄지면 email에 대한 정보로 탈퇴 처리해야 할 듯
    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberResponse> memberInfoDelete(@PathVariable Long memberId){

        DeleteMemberResponse response = memberService.deleteMember(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

//    @Operation(summary = "개인 정보 일부 수정") // 아직 미완
//    @PatchMapping("/{memberId}")
//    public ResponseEntity<PatchMemberResponse> memberPartUpdate(@PathVariable Long memberId, @RequestBody PatchMemberRequest request) {
//
//        PatchMemberResponse response = memberService.updateMemberPart(memberId, request);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

}

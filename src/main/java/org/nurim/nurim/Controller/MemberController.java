package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.nurim.nurim.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "members", description = "회원 정보 API")
@RestController
@ResponseBody
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrincipalDetailsService principalDetailsService;

    @GetMapping
    @Operation(summary = "회원가입 화면 반환 메소드")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping
    @Operation(summary = "신규 회원 등록 메소드", description = "memberEmail 기준으로 중복가입 방지")
    public ResponseEntity<CreateMemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        // 생성된 회원 정보를 Response DTO로 변환하여 반환
        CreateMemberResponse response = memberService.createMember(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 정보 조회 메소드", description = "memberId 기준으로 조회")
    public ResponseEntity<ReadMemberResponse> readMember(@PathVariable Long memberId) {
        ReadMemberResponse response = memberService.readMemberById(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "회원 정보 수정 메소드", description = "memberId 기준으로 ")
    public ResponseEntity<UpdateMemberResponse> updateMember(@PathVariable Long memberId, @RequestBody UpdateMemberRequest request) {

        UpdateMemberResponse response = memberService.updateMember(memberId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "회원 탈퇴 메소드", description = "회원 탈퇴 처리")
    public ResponseEntity<DeleteMemberResponse> deleteMember(@PathVariable Long memberId) {
        DeleteMemberResponse response = memberService.deleteMember(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.config.auth.TokenProvider;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Members", description = "회원 정보 API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="*")
@RequestMapping("/api/v1/members")
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

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

        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("🍎 authentication name : " + authentication.getName());

        if(authentication == null || !authentication.isAuthenticated()) {
            log.info("인증 객체를 찾을 수 없습니다.");
        } else {
            // UserDetails 객체 추출
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                // UserDetails 정보 로그 출력
                log.info("UserDetails: {}", userDetails);
                log.info("Username: {}", userDetails.getUsername());
                log.info("Authorities: {}", userDetails.getAuthorities());
            } else {
                log.info("인증 객체가 UserDetails 타입이 아닙니다.");
            }
        }

        String username = authentication.getName();

        Member accessMember = memberService.readMemberByMemberEmail(username);
        ReadMemberResponse response = memberService.readMemberById(accessMember.getMemberId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> memberUpdate(@RequestBody UpdateMemberRequest request, HttpServletRequest httpRequest) {

        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("🍎 authentication name : " + authentication.getName());

        if(authentication == null || !authentication.isAuthenticated()) {
            log.info("인증 객체를 찾을 수 없습니다.");
        }

        String username = authentication.getName();

        Member accessMember = memberService.readMemberByMemberEmail(username);
        UpdateMemberResponse response = memberService.updateMember(accessMember.getMemberId(), request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping("/memberInfo/{memberId}")
    public ResponseEntity<UpdateMemberResponse> memberInfoUpdate(@RequestBody UpdateMemberInfoRequest request, HttpServletRequest httpRequest) {

        Member accessMember = memberService.getMember(httpRequest);
        UpdateMemberResponse response = memberService.updateMemberInfo(accessMember.getMemberId(), request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "회원 정보 삭제") // 회원가입이 이뤄지면 email에 대한 정보로 탈퇴 처리해야 할 듯
    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberResponse> memberDelete(@RequestBody HttpServletRequest httpRequest){

        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("🍎 authentication name : " + authentication.getName());

        if(authentication == null || !authentication.isAuthenticated()) {
            log.info("인증 객체를 찾을 수 없습니다.");
        }

        String username = authentication.getName();

        Member accessMember = memberService.readMemberByMemberEmail(username);
        DeleteMemberResponse response = memberService.deleteMember(accessMember.getMemberId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

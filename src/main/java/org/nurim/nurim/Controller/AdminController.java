package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.member.DeleteMemberResponse;
import org.nurim.nurim.domain.dto.member.ReadMemberResponse;
import org.nurim.nurim.domain.dto.member.UpdateMemberRequest;
import org.nurim.nurim.domain.dto.member.UpdateMemberResponse;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.AdminService;
import org.nurim.nurim.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    @GetMapping("/")
    public List<Member> getAllMembers() {
        return adminService.getAllMembers();
    }
    @GetMapping("/{memberId}")
    public ResponseEntity<ReadMemberResponse> getMemberById(@PathVariable Long memberId) {
        ReadMemberResponse response = adminService.readMemberById(memberId);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> updateMemberInfo(@PathVariable Long memberId, @RequestBody UpdateMemberRequest request) {
        UpdateMemberResponse response = adminService.updateMemberInfo(memberId, request);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberResponse> deleteMemberInfo(@PathVariable Long memberId) {
        DeleteMemberResponse response = adminService.deleteMemberInfo(memberId);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/paged")
    public Page<Member> getAllMembersPaged(@RequestParam(defaultValue = "0") int page) {
        return adminService.getAllMembersPaged(page);
    }


}

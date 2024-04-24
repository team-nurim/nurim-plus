package org.nurim.nurim.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.member.*;
import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.AdminService;
import org.nurim.nurim.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public List<Member> getAllMembers() {
        return adminService.getAllMembers();
    }
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/{memberId}")
    public ResponseEntity<ReadMemberResponse> getMemberById(@PathVariable Long memberId) {
        ReadMemberResponse response = adminService.readMemberById(memberId);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/{memberId}")
    public ResponseEntity<UpdateMemberResponse> updateMemberInfo(@PathVariable Long memberId, @RequestBody UpdateMemberInfoRequest request) {
        UpdateMemberResponse response = adminService.updateMemberInfo(memberId, request);

        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<DeleteMemberResponse> deleteMemberInfo(@PathVariable Long memberId) {
        DeleteMemberResponse response = adminService.deleteMemberInfo(memberId);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/paged")
    public Page<Member> getAllMembersPaged(@RequestParam(defaultValue = "0") int page) {
        return adminService.getAllMembersPaged(page);
    }

}

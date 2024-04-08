package org.nurim.nurim.Controller;

import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/members")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/")
    public List<Member> getAllMembers() {
        return adminService.getAllMembers();
    }

    @GetMapping("/paged")
    public Page<Member> getAllMembersPaged(@RequestParam(defaultValue = "0") int page) {
        return adminService.getAllMembers(page);
    }

    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return adminService.getMemberById(id);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Member memberToUpdate = adminService.getMemberById(id);
        if (memberToUpdate != null) {
            // 클라이언트가 전달한 필드만을 업데이트
            updates.forEach((key, value) -> {
                switch (key) {
                    case "memberPw":
                        memberToUpdate.setMemberPw((String) value);
                        break;
                    case "memberNickname":
                        memberToUpdate.setMemberNickname((String) value);
                        break;
                    // 필요한 다른 필드들도 추가할 수 있습니다.
                    default:
                        // 알 수 없는 필드는 무시
                        break;
                }
            });

            // 업데이트된 회원을 저장하고 반환
            return adminService.updateMember(id, memberToUpdate);
        } else {
            // 회원이 존재하지 않는 경우
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        adminService.deleteMember(id);
    }

    //회원 전환
    @PutMapping("/{id}/grade")
    public Member updateMemberGrade(@PathVariable Long id, @RequestParam String grade) {
        return adminService.updateMemberGrade(id, grade);
        }
    }


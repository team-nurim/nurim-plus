package org.nurim.nurim.Controller;

import org.nurim.nurim.domain.entity.Member;
import org.nurim.nurim.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}

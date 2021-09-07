package TeamCamp.demo.controller;


import TeamCamp.demo.dto.UserDto.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.service.AdminService;

import static TeamCamp.demo.dto.UserDto.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping("/users")
    public Page<UserListResponse> findByUsers(UserSearchCondition requestDto, Pageable pageable) {
        return adminService.findUsers(requestDto, pageable);
    }

    @GetMapping("/users/{id}")
    public UserDetailResponse getUserDetails(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PostMapping("/users/ban")
    public void restrictUsers(@RequestBody UserBanRequest requestDto) {
        adminService.updateBanUser(requestDto);
    }
}
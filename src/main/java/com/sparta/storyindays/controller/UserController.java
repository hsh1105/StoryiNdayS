package com.sparta.storyindays.controller;

import com.sparta.storyindays.dto.CommonResDto;
import com.sparta.storyindays.dto.user.*;
import com.sparta.storyindays.dto.user.admin.*;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ResourceBundle;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<CommonResDto<ProfileResDto>> getProfile(@PathVariable Long userId) {
        ProfileResDto responseDto = userService.getProfile(userId);
        return ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
                , "프로필 조회에 성공하였습니다!"
                , responseDto));
    }

    @PutMapping("/users/{userId}/profile")
    public ResponseEntity<CommonResDto<ProfileUpdateResDto>> updateProfile(@PathVariable Long userId, @RequestBody @Valid ProfileUpdateReqDto reqDto) {
        ProfileUpdateResDto responseDto = userService.updateProfile(userId, reqDto);
        return ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
            ,"프로필 수정에 성공하였습니다!"
            , responseDto));
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<CommonResDto<Void>> updatePassword(@PathVariable Long userId, @RequestBody @Valid PasswordUpdateReqDto reqDto) {
        userService.updatePassword(userId, reqDto);
        return  ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
        ,"비밀번호가 변경되었습니다."
        ,null));
    }

    @PutMapping("/admins/users/{userId}/auth")
    public ResponseEntity<CommonResDto<AdminAuthResDto>> updateAuth(@PathVariable Long userId, @RequestBody @Valid AdminAuthReqDto reqDto) {
        AdminAuthResDto responseDto = userService.updateAuth(userId, reqDto);
        return ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
        , "해당 유저의 권한이 변경되었습니다."
        , responseDto));
    }

    @PutMapping("/admins/users/{userId}/state")
    public ResponseEntity<CommonResDto<AdminStateResDto>> updateState(@PathVariable Long userId, @RequestBody @Valid AdminStateReqDto reqDto) {
        AdminStateResDto responseDto = userService.updateState(userId, reqDto);
        return ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
        ,"해당 유저계정의 상태가 변경되었습니다."
        , responseDto));
    }

    @PutMapping("/admins/users/{userId}/profile")
    public ResponseEntity<CommonResDto<ProfileUpdateResDto>> adminUpdateProfile(@PathVariable Long userId, @RequestBody @Valid ProfileUpdateReqDto reqDto) {

        ProfileUpdateResDto responseDto = userService.adminUpdateProfile(userId, reqDto);
        return ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
                ,userService.findById(userId).getUsername() + "회원님의 정보가 수정되었습니다."
                , responseDto));
    }

    @GetMapping("/admins/users/all")
    public ResponseEntity<CommonResDto<List<AdminUsersResDto>>> getAllUserProfile(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy",defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc
    ) {

        Page<AdminUsersResDto> responseDto = userService.getAllUserProfile(page-1, size, sortBy, isAsc);
        List<AdminUsersResDto> userList = responseDto.getContent();
        return ResponseEntity.ok().body(new CommonResDto<>(HttpStatus.OK.value()
                ,"전체 회원 조회에 성공하였습니다!"
                ,userList));
    }
}

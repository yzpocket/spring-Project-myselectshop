package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.FolderRequestDto;
import com.sparta.myselectshop.dto.FolderResponseDto;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController {
        private final FolderService folderService;
    // 폴더 추가
    @PostMapping("/folders")
    public void addFolders(@RequestBody FolderRequestDto folderRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        List<String> folderNames = folderRequestDto.getFolderNames();
        folderService.addFolders(folderNames, userDetails.getUser());
    }

    // 회원이 등록한 모든 폴더 조회
    @GetMapping("/folders")
    public List<FolderResponseDto> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) { //회원의 정보를 받아와야 한다(->회원의 정보를 userDetails에 담고, UserDetailsImpl에 담아서 인증객체를 넘겨준다.).
        return folderService.getFolders(userDetails.getUser()); //getFolders에서 이미 User user로 로그인한 회원의 유저로 폴더를 얻어오는 것을 구현해두었기 때문에 그대로 사용한다.
    }
}

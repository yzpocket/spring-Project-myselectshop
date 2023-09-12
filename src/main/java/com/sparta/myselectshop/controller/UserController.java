package com.sparta.myselectshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.dto.UserInfoDto;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import com.sparta.myselectshop.service.KakaoService;
import com.sparta.myselectshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final FolderService folderService;

    private final KakaoService kakaoService;

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRoleEnum role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRoleEnum.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }

    // 로그인 한 유저가 메인 페이지를 요청할 때 가지고있는 폴더를 반환
    @GetMapping("/user-folder")//오버로딩으로 구현할것임
    public String getUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("folders", folderService.getFolders(userDetails.getUser()));

        return "index :: #fragment";// #fragment thymeleaf 회원별로 등록한 폴더를 가져와서 index에서 동적으로 폴더명이 쭉 나올것임. 해당부분 깊히 보지 말것.
    }

    // 카카오 로그인 API
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException { // 쿠키 직접만들어서 SET하는거라 HttpServletResponse로
        // code: 카카오 서버로부터 받은 인가 코드 Service 전달 후 인증 처리 및 JWT 반환
        String token = kakaoService.kakaoLogin(code);

        // Cookie 생성 및 직접 브라우저에 Set
        //Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7)); //substring(7)한이유?
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";
    }

}
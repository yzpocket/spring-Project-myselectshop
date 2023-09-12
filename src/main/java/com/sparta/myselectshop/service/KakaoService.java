package com.sparta.myselectshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.myselectshop.dto.KakaoUserInfoDto;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // <- RestTemplate 빈 수동등록 필요함 config.RestTemplateConfig에서 타임아웃 수동 등록함.
    private final JwtUtil jwtUtil;

    //카카오 로그인 구현
    public String kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        return null;
    }

    //[1] 액세스 토큰 요청과 받기 메소드
    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "cc5bf8349dd6d3a5f49b3f7c541b1652"); // <-Rest API 키
        body.add("redirect_uri", "http://localhost:8080/api/user/kakao/callback"); // Redirect URI
        body.add("code", code); // 인가코드
        log.info("카카오 인가 코드: " + code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class //받아올 타입
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody()); //JSON으로 변환
        return jsonNode.get("access_token").asText(); //액세스 토큰 획득 및 반환
    }

    //[2] 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기 메소드
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("카카오 액세스 토큰: " + accessToken);

        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        //{
        //  "id": 1632335751,
        //  "properties": {
        //    "nickname": "르탄이",
        //    "profile_image": "http://k.kakaocdn.net/...jpg",
        //    "thumbnail_image": "http://k.kakaocdn.net/...jpg"
        //  },
        //  "kakao_account": {
        //    "profile_needs_agreement": false,
        //    "profile": {
        //      "nickname": "르탄이",
        //      "thumbnail_image_url": "http://k.kakaocdn.net/...jpg",
        //      "profile_image_url": "http://k.kakaocdn.net/...jpg"
        //    },
        //    "has_email": true,
        //    "email_needs_agreement": false,
        //    "is_email_valid": true,
        //    "is_email_verified": true,
        //    "email": "letan@sparta.com"
        //  }
        //}
        //들어오는 JSON형태 문자열이 위 같은 형태라서 id는 바로가져올수있고 마치 2차원배열처럼 폴더 형식이라 받아오는 폴더 이동하는 것이 달랐던것.
        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, nickname, email);
    }

}
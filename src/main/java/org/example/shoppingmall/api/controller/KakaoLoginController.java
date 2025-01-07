package org.example.shoppingmall.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingmall.api.dto.response.KakaoUserInfoResponseDto;
import org.example.shoppingmall.api.service.KakaoService;
import org.example.shoppingmall.api.service.UserService;
import org.example.shoppingmall.domain.entity.User;
import org.example.shoppingmall.domain.repository.jpa.UserJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserJpaRepository userJpaRepository;
    private final UserService userService;

    // https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=16b8e7815028ef50efac5e176c9d65e3&redirect_uri=http://localhost:8080/login/kakao

    @Operation(summary = "카카오 로그인 콜백 - code받기")
    @GetMapping("/kakao")
    public ResponseEntity<String> callback(@RequestParam("code") String code)throws IOException {

        log.info("Received Kakao code: {}", code);

        return ResponseEntity.ok("Received Kakao code: " + code);
    }

    @Operation(summary = "카카오 로그인 후 사용자 정보 가져오기")
    @GetMapping("/user-info")
    public ResponseEntity<KakaoUserInfoResponseDto> getUserInfo(@RequestParam("code") String code) throws IOException {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
        KakaoUserInfoResponseDto registerUser = userService.loginUser(accessToken);

        return ResponseEntity.ok(userInfo);
    }

    @Operation(summary = "카카오 로그인 시 db 저장")
    @GetMapping("/save")
    public ResponseEntity<?> saveUser(@RequestParam("code") String code) throws IOException {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        Optional<User> existingUser = userJpaRepository.findByKakaoId(userInfo.getId());

        if (existingUser.isEmpty()){
            User user = User.createUser(
                    userInfo.getId(),
                    userInfo.getKakaoAccount().getProfile().getNickName(),
                    userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
            );

            userJpaRepository.save(user);
            return ResponseEntity.ok("사용자 등록 완료");
        }
        else {
            return ResponseEntity.ok("이미 존재하는 사용자");
        }
    }
}

package org.example.shoppingmall.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingmall.api.dto.response.KakaoUserInfoResponseDto;
import org.example.shoppingmall.domain.entity.User;
import org.example.shoppingmall.domain.repository.jpa.UserJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final KakaoService kakaoService;
    private final UserJpaRepository userJpaRepository;

    public KakaoUserInfoResponseDto loginUser(String accessToken){
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
        Long kakaoId = userInfo.getId();

        // 카카오고유 아이디로 사용자 조회
        Optional<User> existingUser = userJpaRepository.findByKakaoId(kakaoId);

        if (existingUser.isPresent()){
            log.info("유저가 이미 존재합니다: {} " , kakaoId);
        }
        else {
            registerUser(userInfo);
        }
        return userInfo;
    }

    public void registerUser(KakaoUserInfoResponseDto userInfo){
        String nickname = userInfo.getKakaoAccount().getProfile().getNickName();
        String profileImage = userInfo.getKakaoAccount().getProfile().getProfileImageUrl();
        Long kakaoId = userInfo.getId();

        User newUser = User.createUser(kakaoId,nickname,profileImage);

        userJpaRepository.save(newUser);
        log.info("새로운 유저가 등록되었습니다 : {}",kakaoId);

    }
}

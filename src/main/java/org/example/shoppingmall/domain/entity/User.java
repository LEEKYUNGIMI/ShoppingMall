package org.example.shoppingmall.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED comment '회원 고유 번호'")
    private Long id;

    private Long kakaoId;

    @Column(name = "name", columnDefinition = "varchar(45) comment '회원 이름'")
    private String name;

    @Column(name = "nickname", nullable = false, columnDefinition = "varchar(45) comment '닉네임'")
    private String nickname;

    @Column(name = "profile_image", columnDefinition = "varchar(255) comment '프로필 이미지'")
    private String profileImage;

    @Column(name = "password", columnDefinition = "varchar(255) comment '비밀번호'")
    private String password;

    @Column(name = "created_at", columnDefinition = "datetime comment '가입 일시'")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at", columnDefinition = "datetime comment '탈퇴 일시'")
    private LocalDateTime deletedAt;

    // 생성 시
    @Builder
    public User(Long id, Long kakaoId, String name, String nickname, String profileImage, String password, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.kakaoId =kakaoId;
        this.name = name;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.password = password;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static User createUser(Long kakaoId, String nickname, String profileImage){
        return User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImage(profileImage)
                .createdAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

}

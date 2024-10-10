package com.vincentrungogh.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id; //기본 아이디

    @Column(name = "email", nullable = false)
    private String email; //이메일

    @Column(name = "password", nullable = false)
    private String password; //인코딩된 비밀번호

    @Column(name = "nickname", unique = true, length = 10, nullable = false)
    private String nickname; //닉네임 한글, 영어, 숫자 포함 길이 최대 10글자

    @Column(name = "gender", nullable = false)
    private int gender; //0은 남자, 1은 여자

    @Column(name = "birth", nullable = false)
    private LocalDate birth; //1999-01-01의 형태

    @Column(name = "is_changed", nullable = false)
    private Boolean isChanged; //비밀번호 재발급 여부 default는 false

    @Column(name = "profile", nullable = false)
    private String profile; //프로필 사진 s3 url

    @Column(name = "height", nullable = false)
    private double height; //키

    @Column(name = "weight", nullable = false)
    private double weight; //몸무게

    @Builder
    private User(String email, String password, String nickname, int gender, LocalDate birth, Boolean isChanged, String profile, double height, double weight) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.isChanged = isChanged;
        this.profile = profile;
        this.height = height;
        this.weight = weight;
    }

    public static User createUser(String email, String password, String nickname, int gender, LocalDate birth, double height, double weight) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birth(birth)
                .isChanged(false) //비밀번호 재발급 default false
                .profile("https://vincentrungogh.s3.ap-northeast-2.amazonaws.com/profile/profile-default.png") //추후 기본 프로필로 변경
                .height(height)
                .weight(weight)
                .build();
    }

    //비밀번호 재발급시 사용
    public void updateRandomPassword(String password) {
        this.password = password;
        this.isChanged = true;
    }

    //비밀번호 변경시 사용
    public void updatePassword(String password) {
        this.password = password;
        this.isChanged = false;
    }

    // 프로필 수정 시 사용
    public void updateProfile(String nickname, double weight, double height){
        this.nickname = nickname;
        this.weight = weight;
        this.height = height;
    }

    // 프로필 이미지 수정 시 사용
    public void updateProfileImage(String profile){
        this.profile = profile;
    }
}

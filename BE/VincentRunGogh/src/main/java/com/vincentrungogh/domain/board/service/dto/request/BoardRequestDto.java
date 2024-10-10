package com.vincentrungogh.domain.board.service.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class BoardRequestDto {

    private double lat; // 현재 내 위치 gps 위도
    private double lng; // 현재 내 위치 gps 위도
    private String type; // all, mine, myliked

}

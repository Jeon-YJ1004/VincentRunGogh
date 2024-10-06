package com.vincentrungogh.domain.board.service.dto.common;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.global.util.DistanceCalculator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindBoard {
    private int boardId;
    private String nickname;
    private String profile;
    private String title;
    private String drawingImage;
    private String artImage;
    private String comment;
    private int likeCount;
    private Boolean isLiked;
    private double distance;
    private int time;
    private LocalDateTime created;
    private double distanceFromUser;

    @Builder
    private FindBoard(int boardId, String nickname, String profile, String title, String drawingImage, String artImage, String comment, boolean isLiked, int likeCount, double distance, int time, LocalDateTime created, double distanceFromUser) {
        this.boardId = boardId;
        this.nickname = nickname;
        this.profile = profile;
        this.title = title;
        this.drawingImage = drawingImage;
        this.artImage = artImage;
        this.comment = comment;
        this.isLiked = isLiked;
        this.likeCount = likeCount;
        this.distance = distance;
        this.time = time;
        this.created = created;
        this.distanceFromUser = distanceFromUser;
    }

    public static FindBoard createFindBoard(Board board, Double lat, Double lng, boolean isLiked){

        // 해당 루트와 연결된 유저 정보 가져오기
        User writer = board.getRoute().getUser();

        // 떨어진 거리
        Double centerLat = board.getRoute().getCenterLat();
        Double centerLng = board.getRoute().getCenterLng();

        double distanceUser = DistanceCalculator.calculateDistance(centerLat, centerLng, lat, lng);

        return FindBoard.builder()
                .boardId(board.getId())
                .nickname(writer.getNickname())
                .profile(writer.getProfile())
                .title(board.getRoute().getTitle())
                .drawingImage(board.getRoute().getAccumulatedDrawingImage())
                .artImage(board.getRoute().getArtImage())
                .comment(board.getComment())
                .isLiked(isLiked)
                .likeCount(board.getLikeCount())
                .distance(Math.round((double) board.getRoute().getDistance() / 1000.0 * 100.0) / 100.0)
                .time(300) //드로잉 디테일 집계 작성 후 추가
                .created(board.getCreated())
                .distanceFromUser(distanceUser)
                .build();
    }

}
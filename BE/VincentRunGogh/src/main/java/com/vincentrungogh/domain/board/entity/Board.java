package com.vincentrungogh.domain.board.entity;

import com.vincentrungogh.domain.route.entity.Route;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "comment", nullable = false)
    private String comment;

    // 삭제 여부
    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete;

    // 좋아요 수
    @Column(name = "like_count", nullable = false)
    private int likeCount;

    // 생성 날짜
    @Column(name = "created", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime created;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Builder
    private Board(LocalDateTime created, String comment, boolean isDelete, int likeCount, Route route) {
        this.comment = comment;
        this.isDelete = isDelete;
        this.likeCount = likeCount;
        this.created = created;
        this.route = route;
    }

    public static Board createBoard(String comment, Route route) {
        return Board.builder()
                .comment(comment)
                .isDelete(false)
                .likeCount(0)
                .created(LocalDateTime.now())
                .route(route)
                .build();
    }

    // 게시글 삭제
    public void deleteBoard(){
        this.isDelete = true;
    }

    // 좋아요 추가
    public void addLike(){
        this.likeCount++;
    }

    // 좋아요 삭제
    public void deleteLike(){
        this.likeCount--;
    }
}

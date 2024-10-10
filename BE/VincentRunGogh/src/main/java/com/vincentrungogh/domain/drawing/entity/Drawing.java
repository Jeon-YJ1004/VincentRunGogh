package com.vincentrungogh.domain.drawing.entity;

import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "drawings")
public class Drawing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // 드로잉 이름
    @Column(name = "title")
    private String title;

    // 드로잉 완성 여부
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    // 게시글 생성 여부
    @Column(name = "is_created_board", nullable = false)
    private Boolean isCreatedBoard;

    // 누적 드로잉 이미지
    @Column(name = "accumulated_drawing_image")
    private String accumulatedDrawingImage;

    // 생성 날짜
    @Column(name = "created", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime created;

    // 수정 날짜
    @Column(name = "updated", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Builder
    private Drawing(String title, Boolean isCompleted, Boolean isCreatedBoard, String accumulatedDrawingImage, LocalDateTime created, LocalDateTime updated, User user, Route route) {
        this.title = title;
        this.isCompleted = isCompleted;
        this.isCreatedBoard = isCreatedBoard;
        this.accumulatedDrawingImage = accumulatedDrawingImage;
        this.created = created;
        this.updated = updated;
        this.user = user;
        this.route = route;
    }

    public static Drawing createDrawing(String title, User user, Route route) {
        return Drawing.builder()
                .title(title)
                .isCompleted(false)
                .isCreatedBoard(false)
                .accumulatedDrawingImage(null)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .user(user)
                .route(route)
                .build();
    }

    public static Drawing createFreeRunning(User user) {
        return Drawing.builder()
                .title(null)
                .isCompleted(false)
                .isCreatedBoard(false)
                .accumulatedDrawingImage(null)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .user(user)
                .route(null)
                .build();
    }

    public void changeAccumulatedDrawingImage(String accumulatedDrawingImage){
        this.accumulatedDrawingImage = accumulatedDrawingImage;
        this.updated = LocalDateTime.now();
    }


    // 드로잉이 완성되었을 때 사용
    public void completeDrawing(String title, String accumulatedDrawingImage) {
        this.title = title;
        this.isCompleted = true;
        this.accumulatedDrawingImage = accumulatedDrawingImage;
        this.updated = LocalDateTime.now();
    }

    // 게시글에 드로잉을 올릴 때 사용
    public void createBoard(){
        this.isCreatedBoard = true;
//        this.updated = LocalDateTime.now();
    }
}

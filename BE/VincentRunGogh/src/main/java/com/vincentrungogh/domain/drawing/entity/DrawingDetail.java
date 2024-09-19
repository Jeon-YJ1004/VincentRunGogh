package com.vincentrungogh.domain.drawing.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "drawings_detail")
public class DrawingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // 완료 여부
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    // 걸린 시간
    @Column(name = "time")
    private int time;

    // 거리
    @Column(name = "distance")
    private int distance;

    // 속도
    @Column(name = "speed")
    private double speed;

    // 걸음 수
    @Column(name = "step")
    private int step;

    // 현재 드로잉 이미지
    @Column(name = "current_drawing_image")
    private String currentDrawingImage;

    // 생성 날짜
    @Column(name = "created", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drawing_id")
    private Drawing drawing;

    @Builder
    private DrawingDetail(Boolean isCompleted, int time, int distance, double speed, int step, String currentDrawingImage, LocalDateTime created, Drawing drawing) {
        this.isCompleted = isCompleted;
        this.time = time;
        this.distance = distance;
        this.speed = speed;
        this.step = step;
        this.currentDrawingImage = currentDrawingImage;
        this.created = created;
    }

    public static DrawingDetail createDrawingDetail(Drawing drawing){
        return DrawingDetail.builder()
                .isCompleted(false)
                .time(0)
                .distance(0)
                .speed(0)
                .step(0)
                .currentDrawingImage(null)
                .created(LocalDateTime.now())
                .drawing(drawing)
                .build();
    }

    // 드로잉이 일시 정지 되었을 때
    public void stopDrawing(int time, int distance, int speed, int step, String currentDrawingImage){
        this.time = time;
        this.distance = distance;
        this.speed = speed;
        this.step = step;
        this.currentDrawingImage = currentDrawingImage;
    }

    // 드로잉이 완료되었을 때
    public void completeDrawing(int time, int distance, int speed, int step, String currentDrawingImage){
        this.time = time;
        this.distance = distance;
        this.speed = speed;
        this.step = step;
        this.currentDrawingImage = currentDrawingImage;
        this.isCompleted = true;
    }
}

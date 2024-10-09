package com.vincentrungogh.domain.drawing.entity;

import com.vincentrungogh.domain.drawing.service.dto.request.SaveDrawingRequest;
import com.vincentrungogh.domain.drawing.service.dto.response.DataSaveDrawingDetailResponse;
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
    @Column(name = "id")
    private String id;

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
    private DrawingDetail(String id, Boolean isCompleted, int time, int distance, double speed, int step, String currentDrawingImage, LocalDateTime created, Drawing drawing) {
        this.id = id;
        this.isCompleted = isCompleted;
        this.time = time;
        this.distance = distance;
        this.speed = speed;
        this.step = step;
        this.currentDrawingImage = currentDrawingImage;
        this.created = created;
        this.drawing = drawing;
    }

    public static DrawingDetail createDrawingDetail(DataSaveDrawingDetailResponse data,
                                                    String image, Drawing drawing, int step){
        return DrawingDetail.builder()
                .id(data.getDrawingDetailId())
                .isCompleted(false)
                .time(data.getTime())
                .distance(data.getDistance())
                .speed(data.getSpeed())
                .step(step)
                .currentDrawingImage(image)
                .created(LocalDateTime.now())
                .drawing(drawing)
                .build();

    }

    public static DrawingDetail completeDrawingDetail(DataSaveDrawingDetailResponse data,
                                                    String image, Drawing drawing, int step){
        return DrawingDetail.builder()
                .id(data.getDrawingDetailId())
                .isCompleted(true)
                .time(data.getTime())
                .distance(data.getDistance())
                .speed(data.getSpeed())
                .step(step)
                .currentDrawingImage(image)
                .created(LocalDateTime.now())
                .drawing(drawing)
                .build();

    }
}

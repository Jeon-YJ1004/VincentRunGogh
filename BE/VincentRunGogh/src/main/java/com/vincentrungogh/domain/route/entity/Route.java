package com.vincentrungogh.domain.route.entity;

import com.vincentrungogh.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "center_lat")
    private Double centerLat;

    @Column(name = "center_lng")
    private Double centerLng;

    @Column(name = "distance")
    private int distance;

    @Column(name = "art_image")
    private String artImage;

    @Column(name = "created", columnDefinition = "TIMESTAMP")
    private LocalDateTime  created;

    @Column(name = "accumulated_drawing_image")
    private String accumulatedDrawingImage;

    @Builder
    private Route(User user, String title, Double centerLat, Double centerLng, int distance, String artImage, LocalDateTime created, String accumulatedDrawingImage) {
        this.user = user;
        this.title = title;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.distance = distance;
        this.artImage = artImage;
        this.created = created;
        this.accumulatedDrawingImage = accumulatedDrawingImage;
    }

    public static Route createRoute(User user, String title, String artImage) {
        return Route.builder()
                .user(user)
                .title(title)
                .centerLat(null)
                .centerLng(null)
                .distance(0)
                .artImage(artImage)
                .created(LocalDateTime.now())
                .accumulatedDrawingImage(null)
                .build();
    }

    public void updateCenter(Double centerLat, Double centerLng, int distance) {
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.distance = distance;
    }

    public void updateAccumulatedDrawingImage(String accumulatedDrawingImage) {
        this.accumulatedDrawingImage = accumulatedDrawingImage;
    }

}

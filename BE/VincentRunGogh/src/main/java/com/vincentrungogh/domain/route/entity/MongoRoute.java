package com.vincentrungogh.domain.route.entity;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "route")
public class MongoRoute {
    @Id
    private String id;

    @Field(name = "positionList") //column 대신 mongodb에서 사용
    private List<Position> positionList;
}

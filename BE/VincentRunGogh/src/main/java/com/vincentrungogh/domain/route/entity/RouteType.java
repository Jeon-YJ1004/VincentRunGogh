package com.vincentrungogh.domain.route.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RouteType {
    MINE("findMineRoute"),
    OTHERS("findOthersRoute"),
    LIKE("findLikeRoute");

    private final String typeName;
}

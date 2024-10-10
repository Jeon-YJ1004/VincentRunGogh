package com.vincentrungogh.domain.drawing.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GetDrawingType {
    DONE("doneDrawingStrategy"),
    ONGOING("ongoingDrawingStrategy"),
    COMMUNITY("communityDrawingStrategy");

    private final String typeName;
}

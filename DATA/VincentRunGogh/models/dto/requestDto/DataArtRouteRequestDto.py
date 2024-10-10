from pydantic import BaseModel
from typing import List
from models.entity import Position

class DataArtRouteRequestDto(BaseModel):
    positionList: List[Position]
    leftLat: float  # 좌상단 위도
    leftLng: float  # 좌상단 경도
    rightLat: float  # 우하단 위도
    rightLng: float  # 우하단 경도
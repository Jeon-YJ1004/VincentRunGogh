from fastapi import APIRouter
from pyspark.sql import SparkSession

from core.CalculateCenterAndDistance import calculate_center_and_distance

api_router = APIRouter()

from db import mongodb

from models import Route
from models.dto.requestDto import DataSaveRouteRequestDto
from models.dto.responseDto import ResponseDto

@api_router.post("/rootings", response_model=ResponseDto)
async def save_route(request: DataSaveRouteRequestDto):
    spark = SparkSession.builder.appName("SaveRoutePyspark").getOrCreate()

    # Position 객체를 dict로 변환
    position_list_dicts = [position.dict() for position in request.positionList]

    # Route 모델에 positionList를 설정하여 생성
    route = Route(positionList=position_list_dicts)
    await mongodb.engine.save(route)
    print("생성되었습니다.")
    # route의 정보를 계산한 값 넣기
    center_lat, center_lng, distance = calculate_center_and_distance(route)

    spark.stop()

    # 저장된 route의 ID를 응답 데이터로 포함
    response_data = {
        "status": 200,
        "message": "루트를 성공적으로 저장했습니다.",
        "data": {
            "routeId": str(route.id),  # route.id는 MongoDB에서 자동 생성된 ObjectId입니다.
            "centerLat": center_lat,
            "centerLng": center_lng,
            "distance": distance
        }
    }
    return response_data
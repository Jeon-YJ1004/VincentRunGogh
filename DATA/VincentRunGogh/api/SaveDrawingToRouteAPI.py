from fastapi import APIRouter
api_router = APIRouter()
from db import mongodb
from bson import ObjectId

from models import DrawingDetail, Position, Route
from models.dto.requestDto import DataDrawingRouteRequestDto
from models.dto.responseDto import ResponseDto


@api_router.post("/rootings/drawings/routes", response_model=ResponseDto)
async def save_drawing_to_route(request: DataDrawingRouteRequestDto):

    print("테스트 ")

    id_list = request.drawingDetailList

    # 문자열을 ObjectId로 변환
    object_ids = [ObjectId(id_str) for id_str in id_list]

    # $in 연산자를 사용하여 해당 id들을 가진 모든 문서를 검색
    results = await mongodb.engine.find(DrawingDetail, {"_id": {"$in": object_ids}})

    # 드로잉 디테일 리스트를 통해서 드로잉을 만들어서 루트 만들기
    #results 활용
    # 테스트용 Position 객체 생성
    positionList = []

    test_position = Position(lat=0.0, lng=0.0)  # 예시로 lat, lng 값을 지정

    # Position 객체를 dict로 변환하여 positionList에 추가
    positionList.extend([test_position.dict()])

    # 루트 만든 것 저장
    route = Route(positionList=positionList)
    await mongodb.engine.save(route)
    print("생성되었습니다.")

    # 저장된 route의 ID를 응답 데이터로 포함
    response_data = {
        "status": 200,
        "message": "루트를 성공적으로 저장했습니다.",
        "data": {
            "routeId": str(route.id),  # route.id는 MongoDB에서 자동 생성된 ObjectId입니다.
            "centerLat": 0.0,
            "centerLng": 0.0,
            "distance": 0
        }
    }
    return response_data
from fastapi import APIRouter
api_router = APIRouter()

from db import mongodb
from bson import ObjectId

from models import DrawingDetail
from models.dto.requestDto import DataSaveDrawingDetailRequestDto
from models.dto.responseDto import ResponseDto


@api_router.post("/rootings/drawings", response_model=ResponseDto)
async def save_drawing_detail(request: DataSaveDrawingDetailRequestDto):
    for positionTime in request.positionTimeList:
        # 필요한 계산 수행
        print(f"Using time: {positionTime.time} for calculations")

        # 최종적으로 lat과 lng만 추출하여 dict로 변환
    position_list_dicts = [{"lat": positionTime.lat, "lng": positionTime.lng} for positionTime in
                           request.positionTimeList]

    # DrawingDetail 모델에 positionList를 설정하여 생성
    drawingDetail = DrawingDetail(positionList=position_list_dicts)
    await mongodb.engine.save(drawingDetail)
    print("생성되었습니다.")

    # 저장된 드로잉의 ID를 응답 데이터로 포함
    response_data = {
        "status": 200,
        "message": "루트를 성공적으로 저장했습니다.",
        "data": {
            "drawingDetailId": str(drawingDetail.id),  # drawingDetail.id는 MongoDB에서 자동 생성된 ObjectId입니다.
            "time": 1000,
            "speed": 1000,
            "distance": 1000
        }
    }
    return response_data
from fastapi import APIRouter
api_router = APIRouter()

from db import mongodb
from bson import ObjectId

from core.CalculateTime import calculate_time

from models import DrawingDetail
from models.dto.requestDto import DataSaveDrawingDetailRequestDto
from models.dto.responseDto import ResponseDto

from pySpark.RouteTotalDistance import haversine, calculate_total_distance

@api_router.post("/rootings/drawings", response_model=ResponseDto)
async def save_drawing_detail(request: DataSaveDrawingDetailRequestDto):
    # 1. 시간 계산 : 처음과 끝만 계산하기(5초마다 보내기 때문)
    # 중간 일시정지가 된다면 데이터가 들어오지 않음. 그러면 결론.
    # 만약 다음 노드와의 time 차이가 30초이상이 나는 경우는 일시정지로 간주

    # hh:
    draws = request.positionTimeList
    total_time = 0
    temp_stop_distance = 0
    drawing_detail_coords = [(draws[0].lat, draws[0].lng)] # 초기값 설정
    # 만약 each_time이 유효하다면, 거리계산 해야함.
    # 유효하지 않다면 거리계산 필요 x (이 경우 거리 따로 빼서 계산하기)
    print(f"draws {len(draws)}")
    for i in range(1, len(draws)):
        each_time = calculate_time(draws[i - 1].time, draws[i].time)
        drawing_detail_coords.append((draws[i].lat, draws[i].lng)) # 일단 다 받고, spark로 싹 다 계산하고, 나중에 일시정지만 빼기
        if each_time == 0: # 일시 정지일 때 뺄 값 따로 계산하기
            temp_stop_distance += haversine(draws[i-1].lat,draws[i-1].lng,draws[i].lat,draws[i].lng)
            # print("12초 이상인 경우 ", i)
        total_time += each_time

    # 전체 pyspark 계산한 값에서 일시정지 distance값 빼기

    # print("일시정지 거리 계산 합" ,temp_stop_distance)
    # m
    total_distance = calculate_total_distance(drawing_detail_coords) - temp_stop_distance

    # km/h
    total_speed = (total_distance*0.001)/(total_time*60*60)

        # 최종적으로 lat과 lng만 추출하여 dict로 변환
    position_list_dicts = [{"lat": positionTime.lat, "lng": positionTime.lng} for positionTime in
                           request.positionTimeList]

    # DrawingDetail 모델에 positionList를 설정하여 생성
    drawingDetail = DrawingDetail(positionList=position_list_dicts)
    await mongodb.engine.save(drawingDetail)
    print("생성되었습니다.")

    # 저장된 route의 ID를 응답 데이터로 포함
    response_data = {
        "status": 200,
        "message": "드로잉 디테일을 성공적으로 저장했습니다.",
        "data": {
            "drawingDetailId": str(drawingDetail.id),  # drawingDetail.id는 MongoDB에서 자동 생성된 ObjectId입니다.
            "time": total_time,
            "speed": total_speed,
            "distance": total_distance
        }
    }
    return response_data
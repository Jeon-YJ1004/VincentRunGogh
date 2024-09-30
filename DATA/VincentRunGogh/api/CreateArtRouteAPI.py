from fastapi import APIRouter

from core.GenerateRoute import generate_route
from models.dto.requestDto import DataArtRouteRequestDto
from models.dto.responseDto import ResponseDto

api_router = APIRouter()

@api_router.post("/rootings/art", response_model=ResponseDto)
async def create_art_route(request: DataArtRouteRequestDto):
    # 요청에서 position_list 받기
    # print(ArtRouteRequest.leftLat)
    user_drawn_path = [(each.lat, each.lng) for each in request.positionList]
    position_list = generate_route(user_drawn_path,request.leftLat,request.leftLng,request.rightLat,request.rightLng)

    response_data = {
        "status": 200,
        "message": "루트화에 성공했습니다.",
        "data": {
            "positionList": position_list
        }
    }
    return response_data
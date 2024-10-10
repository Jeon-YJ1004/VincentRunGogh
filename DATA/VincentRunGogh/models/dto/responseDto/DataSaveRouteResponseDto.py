from pydantic import BaseModel

class DataSaveRouteResponseDto(BaseModel):
    centerLat: float
    centerLng: float
    distance: int

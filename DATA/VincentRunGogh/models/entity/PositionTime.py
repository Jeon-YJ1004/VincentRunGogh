from pydantic import BaseModel

class PositionTime(BaseModel):
    lat: float
    lng: float
    time: str
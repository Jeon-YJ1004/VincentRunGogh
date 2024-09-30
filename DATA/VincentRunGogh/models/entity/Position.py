from pydantic import BaseModel

class Position(BaseModel):
    lat: float
    lng: float
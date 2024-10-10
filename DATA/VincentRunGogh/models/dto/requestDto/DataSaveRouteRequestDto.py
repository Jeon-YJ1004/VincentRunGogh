from pydantic import BaseModel
from typing import List
from models.entity import Position

class DataSaveRouteRequestDto(BaseModel):
    positionList: List[Position]
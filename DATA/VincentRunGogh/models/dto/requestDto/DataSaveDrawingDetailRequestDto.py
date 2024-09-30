from pydantic import BaseModel
from typing import List
from models.entity.PositionTime import PositionTime


class DataSaveDrawingDetailRequestDto(BaseModel):
    positionTimeList: List[PositionTime]
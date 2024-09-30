from pydantic import BaseModel
from typing import List

class DataDrawingRouteRequestDto(BaseModel):
    drawingDetailList: List[str]
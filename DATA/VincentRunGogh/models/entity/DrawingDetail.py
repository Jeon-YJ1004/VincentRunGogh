from odmantic import Model
from typing import List

class DrawingDetail(Model):
    positionList: List[dict]
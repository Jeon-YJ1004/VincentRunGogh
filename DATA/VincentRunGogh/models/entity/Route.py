from odmantic import Model
from typing import List

class Route(Model):
    positionList: List[dict]
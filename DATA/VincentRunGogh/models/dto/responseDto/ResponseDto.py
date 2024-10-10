from pydantic import BaseModel

class ResponseDto(BaseModel):
    status: int
    message: str
    data: dict
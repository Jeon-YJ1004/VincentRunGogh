from contextlib import asynccontextmanager
from fastapi import FastAPI
import uvicorn
from api import SaveDrawingToRouteAPI, SaveRouteAPI, DrawingDetailSaveAPI, CreateArtRouteAPI
from fastapi.middleware.cors import CORSMiddleware
from db import mongodb


@asynccontextmanager
async def lifespan(app: FastAPI):
    # 애플리케이션 시작 시 실행
    mongodb.connect()
    yield  # 애플리케이션의 수명 동안 실행됨
    # 애플리케이션 종료 시 실행
    await mongodb.close()

app = FastAPI(lifespan=lifespan)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용 ("*" 대신 특정 도메인만 허용하려면 ["http://example.com"] 처럼 설정)
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
    allow_headers=["*"],  # 모든 헤더 허용
)

# "/api/v1"를 기본 경로로 설정하여 라우터 등록
# app.include_router(api_router, prefix="/api/v1")
app.include_router(SaveDrawingToRouteAPI.api_router, prefix="/api/v1")  # users API 등록
app.include_router(SaveRouteAPI.api_router, prefix="/api/v1")  # items API 등록
app.include_router(DrawingDetailSaveAPI.api_router, prefix="/api/v1")  # items API 등록
app.include_router(CreateArtRouteAPI.api_router, prefix="/api/v1")  # items API 등록

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")

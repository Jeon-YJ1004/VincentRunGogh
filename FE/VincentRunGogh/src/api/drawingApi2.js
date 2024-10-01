import { Axios, loginAxios } from '@/api/http-commons';

const http = Axios();
const loginHttp = loginAxios();

// 드로잉 조회 (타입별)
export function getDrawingList(type) {
  return loginHttp
    .get(`/drawings?type=${type}`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 특정 드로잉 조회 (홈)
export function getDrawingInfo(drawingId) {
  return loginHttp
    .get(`/drawings/${drawingId}`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

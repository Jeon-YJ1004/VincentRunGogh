import { Axios, loginAxios } from '@/api/http-commons';

const http = Axios();
const loginHttp = loginAxios();

// 아트 루트화
export function sendArtLine(drawForm) {
  return loginHttp
    .post('/routes', drawForm)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 루트 최종 생성
export function makeRoute(makeRouteForm) {
  return loginHttp
    .post('/routes/end', makeRouteForm)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 루트 조회(타입별)
export function getRouteList(routeListParams) {
  return loginHttp
    .get(
      `/routes?type=${routeListParams.type}&lng=${routeListParams.lng}&lat=${routeListParams.lat}`
    )
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

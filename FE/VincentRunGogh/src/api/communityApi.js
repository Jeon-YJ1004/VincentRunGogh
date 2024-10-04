import { Axios, loginAxios } from '@/api/http-commons';

const http = Axios();
const loginHttp = loginAxios();

// 루트 조회
export function getArticleList(articleListParams) {
  return loginHttp
    .get(
      `/boards?lat=${articleListParams.lat}&lng=${articleListParams.lng}&type=${articleListParams.type}`
    )
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

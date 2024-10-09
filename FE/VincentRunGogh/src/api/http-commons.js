import axios from 'axios';
import { successAlert, errorAlert } from '@/utils/notificationAlert';
/** Axios Response 데이터 형식
 *  config : 요청에 대한 axios 구성 설정
 *  data 서버가 제공한 응답 데이터
 *  headers : 헤더 정보
 *  request : 요청
 *  status : 응답 HTTP 상태 코드
 *  statusText : 응답 HTTP 상태 메시지
 */
// 서버에서 내려주는 응답 구조
// interface APIResponse<T> {
//   status: number; // 상태코드
//   message: string; // 메시지
//   data: T; // 데이터 내용
// }

function Axios() {
  const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
      'Content-Type': 'application/json;charset=utf-8',
    },
    withCredentials: true,
  });

  // instance.defaults.headers.post['Content-Type'] = 'application/json';
  // instance.defaults.headers.put['Content-Type'] = 'application/json';

  instance.interceptors.response.use(
    (response) => response,
    (error) => {
      errorAlert(error.response.data.message);
      return Promise.reject(error);
    }
  );
  return instance;
}

async function tokenRegeneration() {
  const refreshToken = localStorage.getItem('refreshToken');
  const response = await Axios().post('/auth/token/reissue');
  return response;
}
function loginAxios(isMultipart) {
  const refreshToken = localStorage.getItem('refreshToken');

  const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
      // 'Content-Type': 'application/json;charset=utf-8',
    },
    withCredentials: true,
  });

  instance.defaults.headers.post['Content-Type'] = 'application/json';

  instance.interceptors.request.use(
    (config) => {
      const accessToken = localStorage.getItem('accessToken');
      config.headers['Content-Type'] = 'application/json';
      config.headers['Authorization'] = `Bearer ${accessToken}`;
      if (isMultipart && (config.method === 'post' || config.method === 'put')) {
        config.headers['Content-Type'] = 'multipart/form-data';
      } else {
        config.headers['Content-Type'] = 'application/json';
      }
      return config;
    },
    (error) => {
      console.log(error.toJSON());
      return Promise.reject(error);
    }
  );

  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      if (error.response.status === 401) {
        try {
          const response = await tokenRegeneration();
          const newAccessToken = response.data.data.accessToken;
          localStorage.setItem('accessToken', response.data.accessToken);
          axios.defaults.headers.common.Authorization = `Bearer ${newAccessToken}`;
          // localStorage.setItem('refreshToken', response.data.refreshToken);
          //진행중이던 요청 이어서하기
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return axios(originalRequest);
        } catch (error) {
          successAlert('다시 로그인 해주세요', () => {
            window.location.replace('/#/login');
            localStorage.removeItem('userStore');
            return new Promise(() => {});
          });
        }
      } else {
        if (error.response.status !== 501) {
          errorAlert(error.response.data.message);
        }

        return Promise.reject(error);
      }
    }
  );
  return instance;
}

export { Axios, loginAxios };

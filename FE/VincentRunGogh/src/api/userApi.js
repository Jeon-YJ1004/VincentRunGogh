import { loginAxios } from '@/api/http-commons';
const http = loginAxios(false);
const multipartHttp = loginAxios(true);
async function getProfile(success, fail) {
  await http.get('/users').then(success).catch(fail);
}
async function updateProfileImg(image, success, fail) {
  await multipartHttp.put('/users/profile-image', { image: image }).then(success).catch(fail);
}
async function updateProfile(data, success, fail) {
  await http.put('/users', data).then(success).catch(fail);
}
async function updatePw(password, success, fail) {
  await http.put('/users/password', { password: password }).then(success).catch(fail);
}

async function logout(success, fail) {
  await http.post(`/auth/logout`).then(success).catch(fail);
}

// 일주일 운동 정보 조회
export function getWeeklyInfo() {
  return http
    .get('/users/week')
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

export { getProfile, updateProfileImg, updateProfile, updatePw, logout };

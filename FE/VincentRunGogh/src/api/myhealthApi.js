import { loginAxios } from '@/api/http-commons';
const http = loginAxios(false);
async function getMonthData(year, month, success, fail) {
  await http.get(`/calendar?year=${year}&month=${month}`).then(success).catch(fail);
}

async function getDrawingDetail(drawingId, date, success, fail) {
  await http.get(`/calendar/detail/${drawingId}?date=${date}`).then(success).catch(fail);
}
async function getDrawingProgress(year, success, fail) {
  await http.get(`/myhealth/drawings?year=${year}`).then(success).catch(fail);
}
async function updatePw(password, success, fail) {
  await http.put('/users/password', { password: password }).then(success).catch(fail);
}

async function logout(success, fail) {
  await http.post(`/auth/logout/${loginId}`).then(success).catch(fail);
}

// 내 운동정보 조회
export function getMyInfo() {
  return http
    .get(`/myhealth/group`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

export { getMonthData, getDrawingDetail, updatePw, logout };

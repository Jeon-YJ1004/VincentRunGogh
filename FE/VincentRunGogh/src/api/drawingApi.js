import { loginAxios } from '@/api/http-commons';
const http = loginAxios(false);
async function startDrawing(options, data, success, fail) {
  const { drawingId, routeId } = options;
  let url = '/drawings/start';
  if (drawingId) {
    url += `/${drawingId}`;
  } else {
    if (routeId) {
      url += '?type=route';
    } else {
      url += '?type=free';
    }
  }
  const body = routeId ? { routeId, ...data } : { ...data };
  await http.post(url, body).then(success).catch(fail);
}
async function saveDrawing(drawingId, data, success, fail) {
  await http.post(`/drawings/${drawingId}`, data).then(success).catch(fail);
}
async function completeDrawing(drawingId, data, success, fail) {
  await http.post(`/drawings/end/${drawingId}`, data).then(success).catch(fail);
}

async function reSaveDrawing(drawingId, data, success, fail) {
  await http.post(`/drawings/re/${drawingId}`, data).then(success).catch(fail);
}
async function reCompleteDrawing(drawingId, data, success, fail) {
  await http.post(`/drawings/re/end/${drawingId}`, data).then(success).catch(fail);
}
async function getDrawingList(type) {
  return http
    .get(`/drawings?type=${type}`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 특정 드로잉 조회 (홈)
async function getDrawingInfo(drawingId, isDetail) {
  let url = `/drawings/${drawingId}`;
  if (isDetail) {
    url += '/details';
  }
  return http
    .get(url)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

export {
  startDrawing,
  saveDrawing,
  completeDrawing,
  reCompleteDrawing,
  reSaveDrawing,
  getDrawingInfo,
  getDrawingList,
};

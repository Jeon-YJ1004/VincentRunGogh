import { loginAxios } from '@/api/http-commons';
const http = loginAxios(false);
async function startDrawing(options, success, fail) {
  const { drawingId, rootId } = options;
  let url = '/drawings/start';

  if (drawingId) {
    url += `/${drawingId}`;
  }
  const body = rootId ? { rootId } : {};
  await http.post(url, body).then(success).catch(fail);
}
async function saveDrawing(drawingId, data, success, fail) {
  await http.post(`/drawings/${drawingId}`, data).then(success).catch(fail);
}
async function completeDrawing(drawingId, data, success, fail) {
  await http.post(`/drawings/end/${drawingId}`, data).then(success).catch(fail);
}

export { startDrawing, saveDrawing, completeDrawing };

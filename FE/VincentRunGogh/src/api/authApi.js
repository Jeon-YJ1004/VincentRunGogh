import { Axios } from '@/api/http-commons';

const http = Axios();

async function signup(data, success, fail) {
  await http.post('/auth/signup', data).then(success).catch(fail);
}

async function login(email, pw, success, fail) {
  const data = {
    email: email,
    password: pw,
  };
  await http.post('/auth/login', data).then(success).catch(fail);
}

async function checkEmailDuplication(email, success, fail) {
  await http.get(`/auth/find-email?email=${email}`).then(success).catch(fail);
}
async function checkNickNameDuplication(nickname, success, fail) {
  await http.get(`/auth/find-nickname?nickname=${nickname}`).then(success).catch(fail);
}

async function sendEmailVerification(email, success, fail) {
  await http.post(`/auth/code/send`, { email: email }).then(success).catch(fail);
}
async function checkEmailVerification(email, code, success, fail) {
  const data = {
    email: email,
    code: code,
  };
  await http.post(`/auth/code/check`, data).then(success).catch(fail);
}

async function resetPw(email, birth, success, fail) {
  const data = {
    email: email,
    birth: birth,
  };
  await http.post('/auth/reset-password', data).then(success).catch(fail);
}

export {
  login,
  resetPw,
  signup,
  checkEmailDuplication,
  checkNickNameDuplication,
  checkEmailVerification,
  sendEmailVerification,
};

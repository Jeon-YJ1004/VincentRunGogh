import { Axios, loginAxios } from '@/api/http-commons';

const http = Axios();
const loginHttp = loginAxios();

// 게시글 조회
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

// 게시글 삭제
export function deleteArticle(boardId) {
  return loginHttp
    .delete(`/boards/${boardId}`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 게시글 생성
export function postArticle(postArticleForm) {
  return loginHttp
    .post('/boards', postArticleForm)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 게시글 좋아요
export function likeArticle(boardId) {
  return loginHttp
    .post(`/boards/likes/${boardId}`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

// 게시글 좋아요 취소
export function dislikeArticle(boardId) {
  return loginHttp
    .delete(`/boards/likes/${boardId}`)
    .then((response) => response.data)
    .catch((error) => {
      throw error;
    });
}

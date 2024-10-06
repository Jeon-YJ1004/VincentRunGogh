import Swal from 'sweetalert2';

export const errorAlert = (errorMsg, title) => {
  Swal.fire({
    title: title || 'Error!',
    text: errorMsg,
    confirmButtonText: '확인',
    confirmButtonColor: '#FFCC70',
  });
};
export const successAlert = (successMsg, didCloseFuc, confirmFuc) => {
  Swal.fire({
    text: successMsg,
    confirmButtonText: '확인',
    confirmButtonColor: '#FFCC70',
    didClose: didCloseFuc,
  });
};

export const yesorNoAlert = (title, confirmButtonText, cancelButtonText, confirmFuc) => {
  Swal.fire({
    title: title,
    showCancelButton: true,
    confirmButtonColor: '#b5b5b5',
    cancelButtonColor: '#FF4C4C',
    confirmButtonText: confirmButtonText,
    cancelButtonText: cancelButtonText,
  }).then(confirmFuc);
};

export const confirmAlert = (title, confirmButtonText, confirmFuc, text) => {
  Swal.fire({
    title: title,
    text: text,
    showCancelButton: true,
    confirmButtonText: `취소`,
    cancelButtonText: confirmButtonText,
    confirmButtonColor: '#b5b5b5',
    cancelButtonColor: '#FFCC70',
  }).then(confirmFuc);
};
const Toast = Swal.mixin({
  toast: true,
  position: 'top-end',
  showConfirmButton: false,
  timer: 2000,
  width: '30%',
  didOpen: (toast) => {
    toast.onmouseenter = Swal.stopTimer;
    toast.onmouseleave = Swal.resumeTimer;
  },
});

export const toastAlert = (title, width, isSuccess = true) => {
  Toast.fire({
    icon: isSuccess ? 'success' : 'error',
    title: title,
    width: width || '20em',
  });
};

export const loadingAlert = (title, imgUrl, didOpen, confirmFuc) => {
  Swal.fire({
    title: "<div class='text-lg'>" + `${title}` + '</div>',
    imageUrl: imgUrl,
    imageWidth: 300,
    imageHeight: 200,
    imageAlt: 'running',
    showConfirmButton: false,
    didOpen: didOpen
  }).then(confirmFuc);
}
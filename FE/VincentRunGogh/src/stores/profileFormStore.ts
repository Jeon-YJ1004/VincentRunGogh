import { writable, derived, get } from 'svelte/store';
import { checkNickNameDuplication } from '@/api/authApi';
interface FormValues {
  nickname: string;
  height: string;
  weight: string;
  profileImage: File | null;
}

interface HelperMessage {
  message: string;
  color: string;
}
function createFormStore() {
  const initialValues: FormValues = { nickname: '', height: '', weight: '', profileImage: null };
  const values = writable<FormValues>(initialValues);

  const initialHelpers = {
    nickname: { message: '영문, 한글, 숫자 10자 이하', color: 'gray' },
    height: { message: '키를 숫자로 입력해주세요.', color: 'gray' },
    weight: { message: '몸무게를 숫자로 입력해주세요.', color: 'gray' },
    profileImage: { message: 'SVG, PNG, JPG', color: 'gray' },
  };
  const helpers = writable<{ [K in keyof FormValues]: HelperMessage }>(initialHelpers);

  function reset() {
    values.set(initialValues);
    helpers.set(initialHelpers);
  }
  function validateNickname(value: string) {
    const regex = /^[A-Za-z가-힣0-9]{1,10}$/;
    if (!regex.test(value)) {
      helpers.update((h) => ({
        ...h,
        nickname: {
          message: '올바른 형식이 아닙니다. (영문, 한글, 숫자 10자 이하)',
          color: 'red',
        },
      }));
    } else {
      helpers.update((h) => ({
        ...h,
        nickname: {
          message: '중복 확인이 필요합니다.',
          color: 'red',
        },
      }));
    }
  }

  async function checkNicknameAvailability(value: string) {
    checkNickNameDuplication(
      value,
      (response) => {
        if (response.data.status === 200) {
          if (response.data.data.isDuplicated) {
            helpers.set({
              ...get(helpers),
              nickname: {
                message: '이미 사용 중인 닉네임입니다.',
                color: 'red',
              },
            });
          } else {
            helpers.set({
              ...get(helpers),
              nickname: {
                message: '사용 가능한 닉네임입니다',
                color: 'green',
              },
            });
          }
        }
      },
      (error) => {
        console.error('에러', error);
        helpers.set({
          ...get(helpers),
          nickname: {
            message: '닉네임 검사 중 오류가 발생했습니다.',
            color: 'red',
          },
        });
      }
    );
  }
  function validateHeight(value: string) {
    const regex = /^\d+$/;
    if (value === '0' || parseInt(value, 10) > 300) {
      helpers.update((h) => ({
        ...h,
        height: {
          message: '키는 0 초과 300 이하로 입력하세요.',
          color: 'red',
        },
      }));
    } else {
      helpers.update((h) => ({
        ...h,
        height: {
          message: regex.test(value) ? '' : '키는 숫자만 입력 가능합니다.',
          color: regex.test(value) ? 'green' : 'red',
        },
      }));
    }
  }

  function validateWeight(value: string) {
    const regex = /^\d+$/;
    if (value === '0' || parseInt(value, 10) > 300) {
      helpers.update((h) => ({
        ...h,
        weight: {
          message: '몸무게는 0 초과 300 이하로 입력하세요.',
          color: 'red',
        },
      }));
    } else {
      helpers.update((h) => ({
        ...h,
        weight: {
          message: regex.test(value) ? '' : '몸무게는 숫자만 입력 가능합니다.',
          color: regex.test(value) ? 'green' : 'red',
        },
      }));
    }
  }

  function validateProfileImage(file: File) {
    const validTypes = ['image/jpeg', 'image/png'];
    const isValidType = validTypes.includes(file.type);
    helpers.update((h) => ({
      ...h,
      profileImage: {
        message: isValidType ? '' : '지원하지 않는 파일 형식입니다.',
        color: isValidType ? 'green' : 'red',
      },
    }));
  }

  return {
    values,
    helpers,
    reset,
    validateNickname,
    validateHeight,
    validateWeight,
    validateProfileImage,
    checkNicknameAvailability,
  };
}

export const profileFormStore = createFormStore();

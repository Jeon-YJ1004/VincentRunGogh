// src/stores/authFormStore.ts
import { writable, derived } from 'svelte/store';

interface AuthFormValues {
  email: string;
  password: string;
  confirmPassword: string;
}

interface HelperMessage {
  message: string;
  color: string;
}

function createAuthFormStore() {
  const initialValues: AuthFormValues = { email: '', password: '', confirmPassword: '' };
  const initialHelpers = {
    email: { message: '이메일을 입력하세요', color: 'gray' },
    password: { message: '비밀번호를 입력하세요', color: 'gray' },
    confirmPassword: { message: '비밀번호를 다시 입력해주세요', color: 'gray' },
  };

  const values = writable<AuthFormValues>(loadFromLocalStorage('authFormValues', initialValues));
  const helpers = writable<{ [key in keyof AuthFormValues]: HelperMessage }>(initialHelpers);
  values.subscribe(($values) => {
    saveToLocalStorage('authFormValues', $values);
  });

  function reset() {
    values.set(initialValues);
    helpers.set(initialHelpers);
    localStorage.removeItem('authFormValues');
  }

  function loadFromLocalStorage(key: string, defaultValue: any) {
    const stored = localStorage.getItem(key);
    return stored ? JSON.parse(stored) : defaultValue;
  }

  function saveToLocalStorage(key: string, value: any) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  function validateEmail(value: string) {
    const emailRegex = /^\S+@\S+\.\S+$/;
    if (!emailRegex.test(value)) {
      helpers.update((h) => ({
        ...h,
        email: {
          message: '유효하지 않은 이메일 형식입니다.',
          color: 'red',
        },
      }));
    } else {
      helpers.update((h) => ({
        ...h,
        email: {
          message: '',
          color: 'green',
        },
      }));
    }
  }

  function validatePassword(value: string) {
    if (value.length < 8) {
      helpers.update((h) => ({
        ...h,
        password: {
          message: '비밀번호는 최소 8자 이상이어야 합니다.',
          color: 'red',
        },
      }));
    } else {
      helpers.update((h) => ({
        ...h,
        password: {
          message: '',
          color: 'green',
        },
      }));
    }
  }

  function validateConfirmPassword(value: string, password: string) {
    if (value !== password) {
      helpers.update((h) => ({
        ...h,
        confirmPassword: {
          message: '비밀번호와 일치하지 않습니다.',
          color: 'red',
        },
      }));
    } else {
      helpers.update((h) => ({
        ...h,
        confirmPassword: {
          message: '',
          color: 'green',
        },
      }));
    }
  }

  const allValid = derived(helpers, ($helpers) => {
    return (
      $helpers.email.color === 'green' &&
      $helpers.password.color === 'green' &&
      $helpers.confirmPassword.color === 'green'
    );
  });

  return {
    values,
    helpers,
    validateEmail,
    validatePassword,
    validateConfirmPassword,
    allValid,
    reset,
  };
}

export const authFormStore = createAuthFormStore();

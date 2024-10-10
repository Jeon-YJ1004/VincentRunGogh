import { writable } from 'svelte/store';

// Assuming UserAuth is defined somewhere if you're using it
interface UserAuth {
  id: string;
  email: string;
}

interface User extends UserAuth {
  nickname: string;
  birth: string;
  gender: number;
  profile: string;
  height: number;
  weight: number;
}

function createUserStore() {
  const { subscribe, set, update } = writable<User | null>(null);

  function login(userData: User) {
    try {
      localStorage.setItem('user', JSON.stringify(userData));
      set(userData);
    } catch (error) {
      console.error('Failed to save user to localStorage:', error);
    }
  }

  function logout() {
    try {
      localStorage.removeItem('user');
      localStorage.removeItem('accessToken');
      set(null);
    } catch (error) {
      console.error('Failed to remove user from localStorage:', error);
    }
  }
  async function updateUser(partialData: Partial<User>) {
    update((current) => {
      if (!current) {
        current = {
          id: '',
          email: '',
          nickname: '',
          birth: '',
          gender: 0,
          profile: '',
          height: 0,
          weight: 0,
        };
      }
      const updatedUser = { ...current, ...partialData };
      try {
        localStorage.setItem('user', JSON.stringify(updatedUser));
      } catch (error) {
        console.error('Failed to update user in localStorage:', error);
      }
      return updatedUser;
    });
  }

  function initialize() {
    try {
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        set(JSON.parse(storedUser));
      }
    } catch (error) {
      console.error('Failed to parse user from localStorage:', error);
    }
  }

  return {
    subscribe,
    login,
    logout,
    updateUser,
    initialize,
    clearProfile: logout,
  };
}

export const userStore = createUserStore();

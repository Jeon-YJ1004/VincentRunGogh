<script lang="ts">
  import { replace, link } from 'svelte-spa-router';
  import { Button, Modal, Label, Input, Checkbox, GradientButton } from 'flowbite-svelte';

  import { userStore } from '../stores/userStore';
  import { login } from '@/api/authApi';
  import { errorAlert } from '@/utils/notificationAlert';

  let id = '';
  let password = '';
  let loading = false;
  const handleLogin = async (event: SubmitEvent) => {
    event.preventDefault();
    if (loading) {
      return;
    }
    loading = true;
    login(
      id,
      password,
      async (response: Promise<Response>) => {
        if (response.data.status === 200) {
          loading = false;
          localStorage.setItem('accessToken', response.data.data.accessToken);
          await userStore.login({
            nickname: response.data.data.nickname,
            profile: response.data.data.profile,
          }); // 로그인 상태 업데이트
          if (response.data.data.isChanged) {
            replace('/changepassword');
          } else {
            console.log('로그인 성공');
            // replace('/'); // 여기서 페이지 변경
            replace('/');
          }
        }
      },
      (error) => {
        loading = false;
        console.log(error);
      }
    );
  };
</script>

<div class="relative">
  <div id="login-header">
    <h1 style="font-family: 'BRUSH'; font-size:30px; margin-top:auto; z-index: 10;">
      Vincent Run Gogh
    </h1>
  </div>
  <div id="login-form">
    <form on:submit={handleLogin} class="flex flex-col mt-2 space-y-6 w-[70%] z-10">
      <!-- <h3 class="mb-4 text-xl font-medium text-gray-900 dark:text-white">로그인</h3> -->
      <Label class="space-y-2">
        <span>아이디</span>
        <Input
          type="email"
          name="email"
          placeholder="이메일을 입력해주세요"
          required
          bind:value={id}
        />
      </Label>
      <Label class="space-y-2">
        <span>비밀번호</span>
        <Input type="password" name="password" required bind:value={password} />
      </Label>
      <div class="flex flex-col justify-center items-center">
        <a use:link href="/" class="text-sm text-primary-700 hover:underline dark:text-primary-500">
          비밀번호 찾기
        </a><br />
        <div class="text-sm font-medium text-gray-500 dark:text-gray-300">
          계정이 없으신가요? <a
            use:link
            href="/signup"
            class="text-primary-700 hover:underline dark:text-primary-500"
          >
            회원가입
          </a>
        </div>
      </div>
      <Button color="green" type="submit" class="w-full">로그인</Button>
    </form>
  </div>
  <div id="background">
    <svg width="200" height="100" xmlns="http://www.w3.org/2000/svg" style="overflow: visible;">
      <ellipse cx="100" cy="50" rx="220" ry="330" fill="url(#grad1)" filter="url(#blur-filter)" />
      <defs>
        <filter id="blur-filter" x="-50%" y="-50%" width="1000%" height="1000%">
          <feGaussianBlur stdDeviation="40" />
        </filter>
        <linearGradient id="grad1" x1="0%" y1="0%" x2="80%" y2="70%">
          <stop offset="0%" style="stop-color:rgb(255,184,0);stop-opacity:1" />
          <stop offset="100%" style="stop-color:rgb(255,218,115);stop-opacity:1" />
        </linearGradient>
      </defs>
    </svg>
  </div>
  <div id="background2">
    <svg width="200" height="100" xmlns="http://www.w3.org/2000/svg" style="overflow: visible;">
      <ellipse cx="100" cy="50" rx="220" ry="330" fill="url(#grad2)" filter="url(#blur-filter)" />
      <defs>
        <filter id="blur-filter" x="-50%" y="-50%" width="1000%" height="1000%">
          <feGaussianBlur stdDeviation="40" />
        </filter>
        <linearGradient id="grad2" x1="0%" y1="0%" x2="80%" y2="70%">
          <stop offset="0%" style="stop-color:rgb(94,131,88);stop-opacity:1" />
          <stop offset="100%" style="stop-color:rgb(154,186,149);stop-opacity:1" />
        </linearGradient>
      </defs>
    </svg>
  </div>
</div>

<style>
  #login-header {
    width: 100vw;
    height: 20vh;
    display: flex;
    justify-content: center;
    text-align: end;
    z-index: 2;
  }
  #login-form {
    font-family: 'Pretendard-Regular';
    width: 100vw;
    height: 80vh;
    display: flex;
    justify-content: center;
    align-items: start;
    padding-top: 8vh;
    z-index: 2;
  }
  #background {
    position: fixed;
    top: 6%;
    left: 26%;
    transform: translate(-50%, -50%);
    z-index: 1;
    opacity: 0.4;
    overflow: visible;
    animation: sway 5s ease-in-out infinite;
  }

  #background2 {
    position: fixed;
    bottom: -9%;
    right: -50%;
    transform: translate(-50%, -50%);
    z-index: 1;
    opacity: 0.4;
    overflow: visible;
    animation: sway 5s ease-in-out infinite;
  }

  @keyframes sway {
    0% {
      transform: translate(-100%, -120%); /* 두 변환을 함께 적용 */
    }
    50% {
      transform: translate(-10%, 0%); /* 살짝 오른쪽으로 이동 */
    }
    100% {
      transform: translate(-100%, -120%); /* 다시 원래 위치로 */
    }
  }
</style>

<script lang="ts">
  import { replace, link } from 'svelte-spa-router';
  import { Button, Modal, Label, Input, Checkbox } from 'flowbite-svelte';

  import { userStore } from '../stores/userStore';
  import { login } from '@/api/authApi';
  import { errorAlert } from '@/utils/notificationAlert';

  let id = '';
  let password = '';

  const handleLogin = async (event: SubmitEvent) => {
    event.preventDefault();
    login(
      id,
      password,
      async (response: Promise<Response>) => {
        if (response.data.status === 200) {
          localStorage.setItem('accessToken', response.data.data.accessToken);
          await userStore.login({ nickname: response.data.data.nickname }); // 로그인 상태 업데이트
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
        console.log(error);
      }
    );
  };
</script>

<form on:submit={handleLogin} class="flex flex-col space-y-6">
  <h3 class="mb-4 text-xl font-medium text-gray-900 dark:text-white">로그인</h3>
  <Label class="space-y-2">
    <span>아이디</span>
    <Input type="email" name="email" placeholder="name@company.com" required bind:value={id} />
  </Label>
  <Label class="space-y-2">
    <span>비밀번호</span>
    <Input type="password" name="password" required bind:value={password} />
  </Label>
  <div class="flex items-start">
    <a
      use:link
      href="/"
      class="ms-auto text-sm text-primary-700 hover:underline dark:text-primary-500"
    >
      비밀번호 찾기
    </a>
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
  <Button type="submit" class="w-full1">로그인</Button>
</form>

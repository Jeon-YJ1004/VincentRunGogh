<script lang="ts">
  import { Label, Input, Button, Helper, ButtonGroup, Checkbox } from 'flowbite-svelte';
  import { authFormStore } from '@/stores/authFormStore';
  import {
    checkEmailDuplication,
    sendEmailVerification,
    checkEmailVerification,
  } from '@/api/authApi';
  import { push } from 'svelte-spa-router';
  import { errorAlert } from '@/utils/notificationAlert';
  const { values, helpers, allValid, validateEmail, validatePassword, validateConfirmPassword } =
    authFormStore;

  let loading: boolean = false;
  let isVerifyEmail: boolean = false;
  let sendEmailCode: boolean = false;
  let emailCode: string = '';
  const onchangeEmail = (e) => {
    isVerifyEmail = false;
    sendEmailCode = false;
    validateEmail(e.target.value);
  };
  const handleCheckEmail = () => {
    if (!$values.email) {
      $helpers.email.color = 'red';
      $helpers.email.message = '이메일일을 입력해주세요.';
      return;
    }
    $helpers.email.color = '';
    $helpers.email.message = '';
    if (loading) return;
    loading = true;
    checkEmailDuplication(
      $values.email,
      (response) => {
        if (!response.data.data.isDuplicated) {
          //TODO - 사용 가능이면 이베일 인증 번호 전송 api
          sendEmailVerification(
            $values.email,
            (response) => {
              sendEmailCode = true;
              loading = false;
              $helpers.email.color = 'green';
              $helpers.email.message = '사용 가능한 이메일입니다';
            },
            (error) => {}
          );
        } else {
          $helpers.email.color = 'red';
          $helpers.email.message = '이미 사용중인 이메일입니다';
          loading = false;
        }
      },
      (error) => {
        loading = false;
        errorAlert('다시 시도해주세요');
      }
    );
  };
  const handleCheckCode = () => {
    checkEmailVerification(
      $values.email,
      emailCode,
      (response) => {
        if (response.data.data.isAvailable) {
          isVerifyEmail = true;
        } else {
          errorAlert('인증번호를 다시 확인해주세요');
        }
      },
      (error) => {}
    );
  };
</script>

<div class="relative">
  <div id="signup-header">
    <h1 style="font-size:30px; margin-top:auto; z-index: 10;">회원가입</h1>
  </div>
  <div id="signup-form">
    <div class="mb-6 w-[80%] z-10">
      <Label for="nickname" class="block mb-2">이메일</Label>
      <ButtonGroup class="w-full">
        <Input
          id="email"
          type="email"
          placeholder="name@site.com"
          class="h-full"
          bind:value={$values.email}
          on:input={onchangeEmail}
        />
        <Button class="p-2" on:click={handleCheckEmail}>인증번호 전송</Button>
      </ButtonGroup>
      <Helper style="color: {$helpers.email.color};">{$helpers.email.message}</Helper>
    </div>
    {#if sendEmailCode}
      <div class="mb-6 w-[80%] z-10">
        <Label for="input-addon" class="mb-2">해당 이메일로 인증번호를 전송했습니다.</Label>

        <ButtonGroup class="w-full">
          <Input
            type="text"
            placeholder="인증번호를 입력해주세요"
            size="sm"
            class="h-full"
            bind:value={emailCode}
          />
          <Button color="primary" class="p-1 whitespace-nowrap" on:click={handleCheckCode}
            >인증하기</Button
          >
        </ButtonGroup>
      </div>
    {/if}
    {#if isVerifyEmail}
      <div class="mb-6 w-[80%] z-10">
        <Label for="password" class="mb-2">비밀번호</Label>
        <ButtonGroup class="w-full">
          <Input
            id="password"
            type="password"
            bind:value={$values.password}
            required
            on:input={(e) => validatePassword(e.target.value)}
          />
        </ButtonGroup>
        <Helper style="color: {$helpers.password.color};">{$helpers.password.message}</Helper>
      </div>
      <div class="mb-6 w-[80%] z-10">
        <Label for="confirmPassword" class="mb-2">비밀번호 확인</Label>
        <ButtonGroup class="w-full">
          <Input
            id="confirmPassword"
            type="password"
            bind:value={$values.confirmPassword}
            required
            on:input={(e) => validateConfirmPassword(e.target.value, $values.password)}
          />
        </ButtonGroup>
        <Helper style="color: {$helpers.confirmPassword.color};"
          >{$helpers.confirmPassword.message}</Helper
        >
      </div>
    {/if}
    {#if $allValid}
      <div class="mt-4 w-[70%] z-10">
        <Button
          type="submit"
          class="w-full"
          on:click={() => {
            push('/signup/profile');
          }}>다음</Button
        >
      </div>
    {/if}
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
  #signup-header {
    width: 100vw;
    height: 10vh;
    display: flex;
    justify-content: center;
    text-align: end;
    z-index: 2;
  }
  #signup-form {
    font-family: 'Pretendard-Regular';
    width: 100vw;
    height: 80vh;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
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

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
    //TODO - 이메일 중복 조회
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
        }
      },
      (error) => {}
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

<div class="mb-6">
  <Label for="nickname" class="block mb-2">이메일</Label>
  <ButtonGroup class="w-full">
    <Input
      id="email"
      type="email"
      placeholder="name@site.com"
      bind:value={$values.email}
      on:input={onchangeEmail}
    />
    <Button on:click={handleCheckEmail}>인증번호 전송</Button>
  </ButtonGroup>
  <Helper style="color: {$helpers.email.color};">{$helpers.email.message}</Helper>
</div>
{#if sendEmailCode}
  <div class="mb-6">
    <Label for="input-addon" class="mb-2">해당 이메일로 인증번호를 전송했습니다.</Label>

    <ButtonGroup class="w-full">
      <Input type="text" placeholder="인증번호를 입력해주세요" size="sm" bind:value={emailCode} />
      <Button color="primary" on:click={handleCheckCode}>인증하기</Button>
    </ButtonGroup>
  </div>
{/if}
{#if isVerifyEmail}
  <div class="mb-6">
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
  <div class="mb-6">
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
  <div class="mt-4">
    <Button
      type="submit"
      class="w-full"
      on:click={() => {
        push('/signup/profile');
      }}>다음</Button
    >
  </div>
{/if}

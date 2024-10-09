<script lang="ts">
  import { Input, Label, Radio, Button } from 'flowbite-svelte';

  import ProfileForm from '@/components/forms/ProfileForm.svelte';
  import { signup } from '@/api/authApi';
  import { profileFormStore } from '@/stores/profileFormStore';
  import { authFormStore } from '@/stores/authFormStore';
  import { errorAlert, successAlert } from '@/utils/notificationAlert';
  import { replace } from 'svelte-spa-router';

  const { values: profileValues, helpers, reset: resetProfile } = profileFormStore;
  const { values: authValues, reset: resetAuth } = authFormStore;

  let birth: Date | null;
  let gender: 0 | 1 | null;

  const handleGenderChange = (selectedGender: 0 | 1) => {
    gender = selectedGender;
  };

  const handleSubmit = () => {
    console.log($authValues, $profileValues);
    if (
      !$authValues.email ||
      !$authValues.password ||
      birth === null ||
      gender === null ||
      $helpers.nickname.color !== 'green' ||
      $helpers.weight.color !== 'green' ||
      $helpers.height.color !== 'green'
    ) {
      errorAlert('입력을 확인해주세요');
      return;
    }
    const data = {
      email: $authValues.email,
      password: $authValues.password,
      birth: birth,
      gender,
      nickname: $profileValues.nickname,
      height: $profileValues.height,
      weight: $profileValues.weight,
    };
    console.log(data);
    signup(
      data,
      (response) => {
        if (response.status === 200) {
          successAlert('가입이 완료되었습니다!', (result) => {
            replace('/login');
            resetProfile(); // 프로필 폼 스토어를 리셋
            resetAuth(); // 인증 폼 스토어를 리셋
          });
        }
      },
      (error) => {}
    );
  };
</script>

<div class="relative">
  <div id="info-header">
    <h1 style="font-size:30px; margin-top:auto; z-index: 10;">회원가입</h1>
  </div>
  <div id="info-form">
    <ProfileForm />

    <div class="mb-6 w-[80%] z-10">
      <Label for="birth" class="pb-2">생년월일</Label>
      <Input type="date" bind:value={birth} />
    </div>
    <div class="mb-6 w-[80%] z-10">
      <Label for="gender">성별</Label>
      <ul class="flex gap-5 mt-5 items-center w-full rounded-lg">
        <li class="w-full">
          <Radio name="gender" checked={gender === 1} on:change={() => handleGenderChange(1)}
            >남자</Radio
          >
        </li>
        <li class="w-full">
          <Radio name="gender" checked={gender === 0} on:change={() => handleGenderChange(0)}
            >여자</Radio
          >
        </li>
      </ul>
    </div>
    <div class="z-10">
      <Button type="submit" class="w-full" on:click={handleSubmit}>회원가입</Button>
    </div>
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
  #info-header {
    width: 100vw;
    height: 10vh;
    display: flex;
    justify-content: center;
    text-align: end;
    z-index: 2;
  }
  #info-form {
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
</style>

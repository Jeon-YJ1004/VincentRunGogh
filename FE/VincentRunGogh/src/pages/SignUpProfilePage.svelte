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

<ProfileForm />

<div class="mb-6">
  <Label for="birth" class="pb-2">생년월일</Label>
  <Input type="date" bind:value={birth} />
</div>
<ul
  class="items-center w-full rounded-lg border border-gray-200 sm:flex dark:bg-gray-800 dark:border-gray-600 divide-x rtl:divide-x-reverse divide-gray-200 dark:divide-gray-600"
>
  <li class="w-full">
    <Radio name="gender" checked={gender === 1} on:change={() => handleGenderChange(1)}>남자</Radio>
  </li>
  <li class="w-full">
    <Radio name="gender" checked={gender === 0} on:change={() => handleGenderChange(0)}>여자</Radio>
  </li>
</ul>
<div class="mt-4">
  <Button type="submit" class="w-full" on:click={handleSubmit}>회원가입</Button>
</div>

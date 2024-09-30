<script lang="ts">
  import { writable, derived, get } from 'svelte/store';
  import { Card, Modal, Listgroup, Button } from 'flowbite-svelte';
  import { link } from 'svelte-spa-router';

  import { onMount, onDestroy } from 'svelte';
  import { EditOutline } from 'flowbite-svelte-icons';
  import {
    ChartLineUpOutline,
    ClockOutline,
    LockOutline,
    ArrowRightToBracketOutline,
    CalendarMonthOutline,
  } from 'flowbite-svelte-icons';
  import BMIChart from '@/components/myhealth/BMIChart.svelte';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import ProfileForm from '@/components/forms/ProfileForm.svelte';
  import ImageForm from '@/components/forms/ImageForm.svelte';
  import { profileFormStore } from '@/stores/profileFormStore';
  import { userStore } from '@/stores/userStore';
  import { getProfile, updateProfileImg, updateProfile } from '@/api/userApi';
  import { toastAlert } from '@/utils/notificationAlert';

  let profile = writable('');
  let nickname = writable('');
  let birth = writable('');
  let gender = writable(0);
  let height = writable(0);
  let weight = writable(0);
  let unsubscribe;
  let userInitialized: boolean = false;
  // userStore를 구독하여 사용자 데이터로 초기화
  userStore.subscribe((user) => {
    if (user) {
      profile.set(user.profile);
      nickname.set(user.nickname);
      birth.set(user.birth);
      gender.set(user.gender);
      height.set(user.height);
      weight.set(user.weight);
    } else {
      profile.set('');
      nickname.set('');
      birth.set('');
      gender.set(0);
      height.set(0);
      weight.set(0);
    }
  });
  function getGenderSymbol(gender: number): string {
    return gender === 0 ? '♂' : '♀';
  }
  const bmi = derived([height, weight], ([$height, $weight]) => {
    if ($height > 0 && $weight > 0) {
      let heightInMeters = $height / 100;
      return $weight / (heightInMeters * heightInMeters);
    }
    return 0;
  });

  function getBMICategory(bmi: number): string {
    if (bmi >= 30) return '비만';
    else if (bmi >= 25) return '과체중';
    else if (bmi > 18.5) return '정상';
    else return '저체중';
    return '';
  }

  interface MenuItem {
    name: string;
    icon: any; // any 대신 적절한 타입 지정
    href: any;
  }
  let dataMenuIcons: MenuItem[] = [
    { name: '캘린더', icon: CalendarMonthOutline, href: '/calendar' },
    { name: '기록', icon: ClockOutline, href: '/history' },
    { name: '통계', icon: ChartLineUpOutline, href: '/progress' },
  ];

  let accountMenuIcons: MenuItem[] = [
    { name: '비밀번호 변경', icon: LockOutline, href: '/changepassword' },
    { name: '로그아웃', icon: ArrowRightToBracketOutline, href: '/logout' },
  ];

  let editFormModal: boolean = false;
  let editImageFormModal: boolean = false;
  let submitAttempt: boolean = false;

  function closeForm() {
    editFormModal = false;
    editImageFormModal = false;
    submitAttempt = false;
    profileFormStore.reset();
  }

  const onClickEditBtn = () => {
    editFormModal = true;
  };
  const onClickEditImageBtn = () => {
    editImageFormModal = true;
  };
  const setProfile = async () => {
    if (userInitialized) {
      return;
    }
    await getProfile(
      async (response) => {
        if (response.data.status == 200) {
          await userStore.updateUser(response.data.data);
          userInitialized = true;
        }
      },
      (error) => {}
    );
  };
  // 폼 제출 함수
  function handleSubmitProfileForm() {
    submitAttempt = true;
    const currentUser = get(userStore);
    let data = {};
    let allUnChanged = true;
    const allValid = Object.entries(get(profileFormStore.helpers)).every(([key, helper]) => {
      if (key === 'profileImage') return true;
      const currentValue = get(profileFormStore.values)[key];
      const valueIsChanged = currentUser && currentUser[key] !== currentValue;
      data[key] = currentValue;
      if (valueIsChanged) allUnChanged = false;
      return helper.color !== 'red';
    });

    if (Object.keys(data).length !== 0 && allValid && !allUnChanged) {
      updateProfile(
        data,
        (response) => {
          if (response.data.status === 200) {
            closeForm();
            toastAlert('프로필이 성공적으로 업데이트 되었습니다.!');
            setProfile();
          }
        },
        (error) => {}
      );
    }
  }
  const handleSubmitImageForm = async () => {
    const imgData = get(profileFormStore.values).profileImage;
    if (imgData && get(profileFormStore.helpers)['profileImage'].color === 'green') {
      // helpers 접근 수정 및 imgData 존재 확인
      updateProfileImg(
        imgData,
        (response) => {
          if (response.status === 200) {
            closeForm();
            toastAlert('프로필 이미지가 성공적으로 업데이트 되었습니다.');
            setProfile();
          }
        },
        (error) => {
          console.error('이미지 업데이트 실패:', error);
        }
      );
    }
  };
  onMount(async () => {
    await setProfile();
    unsubscribe = userStore.subscribe((user) => {
      if (user) {
        if (!userInitialized) {
          userInitialized = true;
        }
        profile.set(user.profile);
        nickname.set(user.nickname);
        birth.set(user.birth);
        gender.set(user.gender);
        height.set(user.height);
        weight.set(user.weight);
      } else {
        profile.set('');
        nickname.set('');
        birth.set('');
        gender.set(0);
        height.set(0);
        weight.set(0);
      }
    });
  });
  //FIXME - Uncaught (in promise) TypeError: fn is not a function
  onDestroy(unsubscribe);
</script>

<BackButton />
<!-- // SECTION - profile -->
<div class="flex items-center space-x-4 rtl:space-x-reverse">
  <div class="wrap">
    <img src={$profile} alt="프로필 이미지" class="main-profile-img" />
    <span class="icon" on:click={onClickEditImageBtn}><EditOutline /></span>
  </div>
  <div class="space-y-1 font-medium dark:text-white">
    <div>{$nickname}</div>
    <div class="text-sm text-gray-500 dark:text-gray-400">{$birth} {getGenderSymbol($gender)}</div>
    <span class="icon" on:click={onClickEditBtn}><EditOutline /></span>
  </div>
</div>
<Modal title="프로필 변경" bind:open={editFormModal}>
  <ProfileForm />
  <svelte:fragment slot="footer">
    <Button on:click={handleSubmitProfileForm}>수정</Button>
    <Button color="alternative" on:click={closeForm}>취소</Button>
  </svelte:fragment>
</Modal>
<Modal title="프로필 이미지 변경" bind:open={editImageFormModal}>
  <ImageForm />
  <svelte:fragment slot="footer">
    <Button on:click={handleSubmitImageForm}>수정</Button>
    <Button color="alternative" on:click={closeForm}>취소</Button>
  </svelte:fragment>
</Modal>
<!-- //SECTION - bmi -->
<Card padding="md">
  <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">BMI</h5>
  <BMIChart bmi={$bmi} />

  <p class="font-normal text-gray-700 dark:text-gray-400 leading-tight">
    {$nickname}님의 체질량지수는 {getBMICategory($bmi)}입니다.
  </p>
  <p class="font-normal text-gray-400 dark:text-gray-200">
    BMI는 근육량, 유전적 원인, 다른 개인적 차이를 반영하지 않습니다.
  </p>
</Card>
<!-- //!SECTION - menu -->
<Listgroup active items={dataMenuIcons} let:item>
  <a use:link href={item.href}>
    <svelte:component this={item.icon} class="w-4 h-4 me-2.5" />
    {item.name}
  </a>
</Listgroup>

<Listgroup active items={accountMenuIcons} let:item>
  <svelte:component this={item.icon} class="w-4 h-4 me-2.5" />
  {item.name}
</Listgroup>

<style>
  .modal-header {
    visibility: hidden;
  }
  .main-profile-img {
    width: 20vw;
    height: 20vw;
    border-radius: 50%;
    border-style: solid;
    border-color: #ffffff;
    box-shadow: 0 0 8px 3px #b8b8b8;
    position: relative;
  }

  .wrap {
    position: relative;
  }

  .edit-button {
    position: absolute;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    border: 1px solid grey;
    top: 0;
    left: 113px;
    background: white;
    color: blue;
  }
  span.icon {
    position: absolute;
    top: 10px;
    right: 0;
    background: #e2e2e2;
    border-radius: 100%;
    width: 30px;
    height: 30px;
    line-height: 30px;
    vertical-align: middle;
    text-align: center;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>

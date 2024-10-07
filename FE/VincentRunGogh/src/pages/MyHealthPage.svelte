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
  import Tabbar from '@/components/common/Tabbar.svelte';
  import ProfileForm from '@/components/forms/ProfileForm.svelte';
  import ImageForm from '@/components/forms/ImageForm.svelte';
  import { profileFormStore } from '@/stores/profileFormStore';
  import { userStore } from '@/stores/userStore';
  import { getProfile, updateProfileImg, updateProfile, logout } from '@/api/userApi';
  import { toastAlert } from '@/utils/notificationAlert';

  let profile = writable('');
  let nickname = writable('');
  let birth = writable('');
  let gender = writable(0);
  let height = writable(0);
  let weight = writable(0);
  let unsubscribe = () => {}; // Define as a noop function initially
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
    { name: '캘린더', icon: CalendarMonthOutline, href: '/#/calendar' },
    { name: '기록', icon: ClockOutline, href: '/#/history' },
    { name: '통계', icon: ChartLineUpOutline, href: '/#/progress' },
  ];

  let accountMenuIcons: MenuItem[] = [
    { name: '비밀번호 변경', icon: LockOutline, href: '/#/changepassword' },
    { name: '로그아웃', icon: ArrowRightToBracketOutline, href: '/#/logout' },
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
  function handleLogout() {
    // 로그아웃 처리 로직
    console.log('로그아웃을 수행합니다.');
    logout(
      (response) => {
        if (response.status === 200) {
          toastAlert('로그아웃 성공');
          userStore.logout();
        }
      },
      (error) => {}
    );
  }

  // Listgroup 항목 클릭 핸들러
  function handleMenuClick(item) {
    if (item.href === '/#/logout') {
      handleLogout();
    } else {
      // 일반적인 라우팅으로 이동
      link(item.href);
    }
  }
  //FIXME - Uncaught (in promise) TypeError: fn is not a function
  // onDestroy(unsubscribe);
</script>

<div class="bg-bg-main h-screen">
  <div class="overflow-auto flex flex-col items-center gap-10 h-[85vh]">
    <!-- // SECTION - profile -->
    <div class="flex items-center space-x-4 mt-12 gap-4">
      <div class="wrap">
        <img src={$profile} alt="프로필 이미지" class="main-profile-img" />
        <button class="icon" on:click={onClickEditImageBtn}><EditOutline /></button>
      </div>
      <div class="space-y-1 font-medium dark:text-white wrap">
        <div>
          <div>{$nickname}</div>
          <div class="text-sm text-gray-500 dark:text-gray-400">
            {$birth}
            {getGenderSymbol($gender)}
          </div>
        </div>
        <div class="absolute top-[-1.00rem] right-[-2.00rem]">
          <button on:click={onClickEditBtn}><EditOutline /></button>
        </div>
      </div>
    </div>

    <!-- //SECTION - bmi -->
    <Card class="w-[80vw] pb-1 pt-3 flex flex-col items-center rounded-3xl bg-[#F0F8EC] shadow-md">
      <h5 class="mb-2 ttext-3xl font-semibold">BMI</h5>
      <BMIChart bmi={$bmi.toFixed(0)} />
      <div class="flex flex-col justify-center items-center gap-2">
        <span class="flex gap-1">
          <p class="font-normal text-gray-700 dark:text-gray-400 leading-tight">
            {$nickname}님의 체질량지수는
          </p>
          <p class="text-gray-800 dark:text-gray-400 leading-tight font-bold">
            {getBMICategory($bmi)}
          </p>
          <p class="font-normal text-gray-700 dark:text-gray-400 leading-tight">입니다.</p>
        </span>
        <p
          class="font-normal text-gray-400 dark:text-gray-200 overflow-hidden text-ellipsis text-center break-keep"
        >
          BMI는 근육량, 유전적 원인, 다른 개인적 차이를 반영하지 않습니다.
        </p>
      </div>
    </Card>
    <!-- //!SECTION - menu -->
    <div class="w-full pl-12 pr-12">
      <Listgroup active items={dataMenuIcons} let:item>
        <div class="flex flex-row items-center content-center justify-start">
          <svelte:component this={item.icon} class="w-4 h-4 me-2.5" />
          {item.name}
        </div>
      </Listgroup>
    </div>
    <div class="w-full pl-12 pr-12">
      <Listgroup active items={accountMenuIcons} let:item>
        <div
          on:click={() => handleMenuClick(item)}
          class="flex flex-row items-center content-center justify-start"
        >
          <svelte:component this={item.icon} class="w-4 h-4 me-2.5" />
          {item.name}
        </div>
      </Listgroup>
    </div>
  </div>
  <div>
    <Tabbar />
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
  }
  button.icon {
    position: absolute;
    top: 0;
    right: -1rem;
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

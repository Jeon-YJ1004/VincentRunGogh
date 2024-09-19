<script lang="ts">
  import { createEventDispatcher, onMount } from 'svelte';
  import { Button } from 'flowbite-svelte';

  export let modalType: string;

  const dispatch = createEventDispatcher();
  const modalData = {
    pause: {
      title: '일시정지',
      buttons: [
        {
          label: '완성',
          color: 'red',
          action: () => dispatch('confirm', { modalType: 'complete' }),
        },
        //FIXME - 자유 드로잉일경우 임시저장 없어야함
        {
          label: '임시저장',
          color: 'yellow',
          action: () => dispatch('confirm', { modalType: 'save' }),
        },
        { label: '계속', color: 'green', action: () => dispatch('continue') },
      ],
    },
    save: {
      title: '드로잉 임시저장 모달',
      buttons: [
        { label: '뒤로가기', color: 'gray', action: () => dispatch('cancel') },
        { label: '임시저장', color: 'yellow', action: () => dispatch('save') },
      ],
    },
    complete: {
      title: '드로잉 완료 모달',
      buttons: [
        { label: '뒤로가기', color: 'gray', action: () => dispatch('cancel') },
        { label: '완성', color: 'red', action: () => dispatch('complete') },
      ],
    },
  };

  let currentModal = modalData[modalType];
  $: currentModal = modalData[modalType]; // modalType이 변경되면 업데이트
</script>

<!-- Flowbite 모달 UI -->
<div class="text-center">
  <h3 class="mb-5 text-lg font-normal text-gray-500 dark:text-gray-400">{currentModal.title}</h3>
  <!-- <Button color="red" class="me-2"
    ><svg xmlns="http://www.w3.org/2000/svg" width="76" height="76" viewBox="0 0 76 76" fill="none">
      <circle cx="38" cy="38" r="38" fill="url(#paint0_linear_249_1372)" />
      <defs>
        <linearGradient
          id="paint0_linear_249_1372"
          x1="18.5"
          y1="9.5"
          x2="66"
          y2="100"
          gradientUnits="userSpaceOnUse"
        >
          <stop stop-color="#FF9693" />
          <stop offset="1" stop-color="#FF4040" />
        </linearGradient>
      </defs>
    </svg></Button
  > -->
  {#each currentModal.buttons as button}
    <Button
      type="button"
      class={`text-white focus:ring-4 focus:outline-none font-medium rounded-lg text-sm px-5 py-2.5 text-center mr-2 ${button.color === 'red' ? 'bg-red-500 hover:bg-red-600' : ''} ${button.color === 'yellow' ? 'bg-yellow-500 hover:bg-yellow-600' : ''} ${button.color === 'green' ? 'bg-green-500 hover:bg-green-600' : ''} ${button.color === 'gray' ? 'bg-gray-500 hover:bg-gray-600' : ''}`}
      on:click={button.action}
    >
      {button.label}
    </Button>
  {/each}
</div>

<style>
</style>

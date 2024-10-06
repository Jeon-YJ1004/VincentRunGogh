<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  import { PlayOutline, StopOutline, PauseOutline, ArrowLeftOutline } from 'flowbite-svelte-icons';
  import { get } from 'svelte/store';
  import { drawingStore } from '@/stores/drawingStore';

  export let modalType: string;
  const dispatch = createEventDispatcher();
  const modalData = {
    pause: {
      title: '일시정지',
      buttons: [
        {
          icon: StopOutline,
          action: () => dispatch('confirm', { modalType: 'complete' }),
          color: 'red',
          text: '완성',
        },
        get(drawingStore).routePositionList > 0 && {
          icon: PauseOutline,
          action: () => dispatch('confirm', { modalType: 'save' }),
          color: 'yellow',
          text: '임시저장',
        },
        { icon: PlayOutline, action: () => dispatch('continue'), color: 'green', text: '계속' },
      ].filter(Boolean),
    },
    save: {
      title: '드로잉 임시저장 모달',
      buttons: [
        {
          icon: ArrowLeftOutline,
          action: () => dispatch('cancel'),
          color: 'gray',
          text: '뒤로가기',
        },
        { icon: PauseOutline, action: () => dispatch('save'), color: 'yellow ', text: '임시저장' },
      ],
    },
    complete: {
      title: '드로잉 완료 모달',
      buttons: [
        {
          icon: ArrowLeftOutline,
          action: () => dispatch('cancel'),
          color: 'gray',
          text: '뒤로가기',
        },
        { icon: StopOutline, action: () => dispatch('complete'), color: 'red', text: '완성' },
      ],
    },
  };

  let currentModal = modalData[modalType];
  $: currentModal = modalData[modalType]; // modalType이 변경되면 업데이트
</script>

<div class="text-center">
  <h3 class="mb-5 text-lg font-normal text-gray-500 dark:text-gray-400">{currentModal.title}</h3>
  <div class="flex items-center justify-evenly flex-row gap-1">
    {#each currentModal.buttons as { icon, action, color, text }}
      <div class="flex content-center flex-col justify-center gap-2">
        <button on:click={action} class={`rounded-full p-1 ${color}`}>
          <svelte:component this={icon} class={`w-16 h-16 text-[#1f1f1f78]`} />
        </button>
        <span class="text-base text-[var(--gray-color-400)]">{text}</span>
      </div>
    {/each}
  </div>
</div>

<style>
  /* Additional styles */
  .yellow {
    background: var(--yellow-linear-gradient);
  }
  .red {
    background: var(--red-linear-gradient);
  }
  .green {
    background: var(--green-linear-gradient);
  }
</style>

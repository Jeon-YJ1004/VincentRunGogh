<script lang="ts">
  import { onMount } from 'svelte';
  import { createEventDispatcher } from 'svelte';
  import {
    isLockScreen,
    isPause,
    elapsedTime,
    totalDistance,
    currentPace,
    stepCount,
  } from '@/stores/drawingStore';
  import { get } from 'svelte/store';
  import { formatSecToMS } from '@/utils/formatter';

  const dispatch = createEventDispatcher<{
    'click-pause': boolean;
    'click-mute': boolean;
    'click-lockScreen': boolean;
  }>();
  $: $isLockScreen = $isLockScreen;
  $: $isPause = $isPause;

  let isMute = false;
  let hasRoute = false;

  function clickPause() {
    dispatch('click-pause', $isPause);
  }

  function clickMute() {
    isMute = !isMute;
    dispatch('click-mute', isMute);
  }

  function clickLockScreen() {
    dispatch('click-lockScreen', $isLockScreen);
  }
</script>

<div class="flex flex-col items-center w-[60vw] h-[25vh] bg-white shadow-lg rounded-lg p-4">
  <div>
    <button
      class="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-yellow-main p-2 rounded-full focus:outline-none h-14 w-14"
      on:click={clickPause}
      aria-label="Pause Button"
    >
      <!-- Pause icon -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class=""
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 9v6m4-6v6" />
      </svg>
    </button>
  </div>
  <div class="flex flex-row items-center w-[inherit] justify-around">
    <button class="text-green-500 focus:outline-none" on:click={clickMute} aria-label="Mute Button">
      <!-- Mute icon -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        {#if isMute}
          <path
            fill="#FF4040"
            fill-rule="evenodd"
            d="M17.47 9.47a.75.75 0 0 1 1.06 0L20 10.94l1.47-1.47a.75.75 0 0 1 1.06 1.06L21.061 12l1.47 1.47a.75.75 0 0 1-1.061 1.06L20 13.06l-1.47 1.47a.75.75 0 0 1-1.06-1.06L18.94 12l-1.47-1.47a.75.75 0 0 1 0-1.06m-4.433-6.074c1.163-.767 2.713.068 2.713 1.461v14.286c0 1.394-1.55 2.228-2.713 1.461l-6-3.955a.25.25 0 0 0-.137-.042H4a2.75 2.75 0 0 1-2.75-2.75v-3.714A2.75 2.75 0 0 1 4 7.393h2.9a.25.25 0 0 0 .138-.041z"
            clip-rule="evenodd"
          />
        {:else}
          <path
            fill="currentColor"
            fill-rule="evenodd"
            d="M18.97 6.97a.75.75 0 0 1 1.06 0l-.53.53l.53-.53h.001l.001.002l.003.002l.007.007l.02.02l.062.069c.05.057.12.138.201.241A6.87 6.87 0 0 1 21.75 11.5a6.87 6.87 0 0 1-1.425 4.189a5 5 0 0 1-.264.31l-.02.02l-.006.007l-.003.002v.001h-.001l-.51-.508l.51.51 a.75.75 0 1 1-1.061-1.061l.53.53l-.53-.53h-.001v.001l-.002.001l.005-.005l.033-.036q.048-.052.139-.167a5.37 5.37 0 0 0 .448-5.843a5 5 0 0 0-.448-.685a3 3 0 0 0-.172-.203l-.005-.005a.75.75 0 0 1 .003-1.058m-5.933-3.574c1.163-.767 2.713.068 2.713 1.461v14.286c0 1.394-1.55 2.228-2.713 1.461l-6-3.955a.25.25 0 0 0-.137-.042H4a2.75 2.75 0 0 1-2.75-2.75v-3.714A2.75 2.75 0 0 1 4 7.393h2.9a.25.25 0 0 0 .138-.041z"
            clip-rule="evenodd"
          />
        {/if}
      </svg>
    </button>
    <button type="button" on:click={clickLockScreen} class="lock-btn">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 24 24" {...$$props}>
        <g
          fill="none"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="1.85"
          color="#ee2f2f"
        >
          <path
            fill="#ee2f2f"
            d="m8.5 14l-.543.517A.75.75 0 0 0 9.25 14zm-2.217-2.329l.544-.517l-.047-.045zM3.877 14.09l.599-.453l-.01-.011zm3.282 4.335l-.598.452zm-3.29-6.519l-.592-.46zM7.602 22a.75.75 0 0 0 1.5 0zm.61-1.884l.717-.223zm-.039-.119l.707-.251zM12 9.5h-.75a.75.75 0 0 0 .783.75zm6 2.56a.75.75 0 0 0 1-1.12zm-8.957 1.423l-2.216-2.329l-1.087 1.034l2.217 2.33zM3.28 14.54l3.282 4.335l1.196-.905l-3.281-4.335zm-.002-3.095a2.53 2.53 0 0 0 .011 3.107l1.179-.928a1.03 1.03 0 0 1-.005-1.26zm3.503-.337a2.4 2.4 0 0 0-3.503.337l1.185.919a.9.9 0 0 1 1.325-.132zM9.102 22c0-.913.008-1.526-.173-2.107l-1.432.445c.097.314.105.658.105 1.662zM6.56 18.876c.599.792.796 1.066.906 1.373l1.413-.503c-.205-.575-.58-1.056-1.123-1.775zm2.368 1.017l-.05-.147l-1.412.503l.03.09zM7.75 3.75V14h1.5V3.75zm3.5 0V9.5h1.5V3.75zm-2 0a1 1 0 0 1 1-1v-1.5a2.5 2.5 0 0 0-2.5 2.5zm1-1a1 1 0 0 1 1 1h1.5a2.5 2.5 0 0 0-2.5-2.5zm1.783 7.5c1.563-.07 4.089.13 5.967 1.81l1-1.12c-2.324-2.076-5.348-2.264-7.033-2.19z"
          />
          <path
            stroke="#ee2f2f"
            d="M14.5 19.5c0-.935 0-1.402.201-1.75a1.5 1.5 0 0 1 .549-.549C15.598 17 16.065 17 17 17h1c.935 0 1.402 0 1.75.201a1.5 1.5 0 0 1 .549.549c.201.348.201.815.201 1.75s0 1.402-.201 1.75a1.5 1.5 0 0 1-.549.549C19.402 22 18.935 22 18 22h-1c-.935 0-1.402 0-1.75-.201a1.5 1.5 0 0 1-.549-.549c-.201-.348-.201-.815-.201-1.75m1.5-4a1.5 1.5 0 0 1 3 0V17h-3z"
          />
        </g>
      </svg>
    </button>
  </div>

  <div class="text-center mt-4">
    <div class="text-4xl font-bold text-gray-800">{formatSecToMS($elapsedTime)}</div>
    <div class="mt-4 space-x-4 flex justify-center items-center">
      <div class=" flex flex-col items-center justify-center">
        <span class="text-sm"> 이동거리 </span>
        <div>
          <span class="text-lg text-gray-700 text-2xl font-bold"
            >{Math.round($totalDistance * 10) / 10}
          </span> km
        </div>
      </div>
      <div class=" flex flex-col items-center justify-center">
        <span class="text-sm"> 페이스 </span>
        <div>
          <span class="text-lg text-gray-700 text-2xl font-bold">{$currentPace} </span>
        </div>
      </div>
      <div class=" flex flex-col items-center justify-center">
        <span class="text-sm"> 걸음수 </span>
        <div>
          <span class="text-lg text-gray-700 text-2xl font-bold">
            <span class="text-lg text-gray-700 text-2xl font-bold">{$stepCount} </span>
          </span>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
</style>

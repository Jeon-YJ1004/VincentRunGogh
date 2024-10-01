<script lang="ts">
  import { onMount } from 'svelte';
  import { createEventDispatcher } from 'svelte';
  import {
    isLockScreen,
    isPause,
    elapsedTime,
    totalDistance,
    currentPace,
  } from '@/stores/drawingStore';
  import { get } from 'svelte/store';
  import { formatSecToMMSS } from '@/utils/formatter';

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

<div class="flex flex-col items-center w-[60vw] h-[30vh] bg-white shadow-lg rounded-lg p-4">
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
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="2.2em"
        height="2.2em"
        viewBox="0 0 24 24"
        {...$$props}
      >
        <path
          fill="black"
          d="M9.85 16q-.35 0-.6-.25t-.25-.6v-3.3q0-.35.25-.6t.6-.25H10v-1q0-.825.588-1.412T12 8t1.413.588T14 10v1h.15q.35 0 .6.25t.25.6v3.3q0 .35-.25.6t-.6.25zM11 11h2v-1q0-.425-.287-.712T12 9t-.712.288T11 10zM7 23q-.825 0-1.412-.587T5 21V3q0-.825.588-1.412T7 1h10q.825 0 1.413.588T19 3v18q0 .825-.587 1.413T17 23zm0-5h10V6H7z"
        />
      </svg>
    </button>
  </div>

  <div class="text-center mt-4">
    <div class="text-4xl font-bold text-gray-800">{formatSecToMMSS($elapsedTime)}</div>
    <div class="mt-4 space-x-4 flex justify-center items-center">
      <!-- Display dynamic data in an organized manner -->
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
          <span class="text-lg text-gray-700 text-2xl font-bold">{Math.round(3.4 * 10) / 10} </span>
          km/h
        </div>
      </div>
      {#if hasRoute}
        <div class=" flex flex-col items-center justify-center">
          <span class="text-sm"> 남은 거리 </span>
          <div>
            <span class="text-lg text-gray-700 text-2xl font-bold"
              >{Math.round(5.6 * 10) / 10}
            </span>km
          </div>
        </div>
      {/if}
    </div>
  </div>
</div>

<style>
</style>

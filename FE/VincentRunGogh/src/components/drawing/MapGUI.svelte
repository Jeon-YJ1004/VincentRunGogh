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
  //러닝 정보(시간, 총 길이, 평균 속도, 남은 거리)
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

<div class="leaflet-bottom-center">
  <div>
    <!-- 화면 잠금 -->
    {#if $isLockScreen}
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
    {:else}
      <!-- 네비 소리 버튼 -->
      <button type="button" on:click={clickMute}>
        {#if isMute}
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="2em"
            height="2em"
            viewBox="0 0 24 24"
            {...$$props}
          >
            <path
              fill="#FF4040"
              fill-rule="evenodd"
              d="M17.47 9.47a.75.75 0 0 1 1.06 0L20 10.94l1.47-1.47a.75.75 0 0 1 1.06 1.06L21.061 12l1.47 1.47a.75.75 0 0 1-1.061 1.06L20 13.06l-1.47 1.47a.75.75 0 0 1-1.06-1.06L18.94 12l-1.47-1.47a.75.75 0 0 1 0-1.06m-4.433-6.074c1.163-.767 2.713.068 2.713 1.461v14.286c0 1.394-1.55 2.228-2.713 1.461l-6-3.955a.25.25 0 0 0-.137-.042H4a2.75 2.75 0 0 1-2.75-2.75v-3.714A2.75 2.75 0 0 1 4 7.393h2.9a.25.25 0 0 0 .138-.041z"
              clip-rule="evenodd"
            />
          </svg>
        {:else}
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="2em"
            height="2em"
            viewBox="0 0 24 24"
            {...$$props}
          >
            <path
              fill="currentColor"
              fill-rule="evenodd"
              d="M18.97 6.97a.75.75 0 0 1 1.06 0l-.53.53l.53-.53h.001l.001.002l.003.002l.007.007l.02.02l.062.069c.05.057.12.138.201.241A6.87 6.87 0 0 1 21.75 11.5a6.87 6.87 0 0 1-1.425 4.189a5 5 0 0 1-.264.31l-.02.02l-.006.007l-.003.002v.001h-.001l-.51-.508l.51.51a.75.75 0 1 1-1.061-1.061l.53.53l-.53-.53h-.001v.001l-.002.001l.005-.005l.033-.036q.048-.052.139-.167a5.37 5.37 0 0 0 .448-5.843a5 5 0 0 0-.448-.685a3 3 0 0 0-.172-.203l-.005-.005a.75.75 0 0 1 .003-1.058m-5.933-3.574c1.163-.767 2.713.068 2.713 1.461v14.286c0 1.394-1.55 2.228-2.713 1.461l-6-3.955a.25.25 0 0 0-.137-.042H4a2.75 2.75 0 0 1-2.75-2.75v-3.714A2.75 2.75 0 0 1 4 7.393h2.9a.25.25 0 0 0 .138-.041z"
              clip-rule="evenodd"
            />
          </svg>
        {/if}
      </button>
      <!-- 일시정지 버튼 -->
      <button type="button" on:click={clickPause}>
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="2em"
          height="2em"
          viewBox="0 0 24 24"
          {...$$props}
        >
          <path
            fill="currentColor"
            fill-rule="evenodd"
            d="M8 5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2zm7 0a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2z"
            clip-rule="evenodd"
          />
        </svg>
      </button>
      <button type="button" on:click={clickLockScreen} class="lock-btn">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="2.2em"
          height="2.2em"
          viewBox="0 0 48 48"
          {...$$props}
        >
          <defs>
            <mask id="ipSUnlock0">
              <g fill="none" stroke-linejoin="round" stroke-width="4">
                <rect width="34" height="22" x="7" y="22.048" fill="#fff" stroke="#fff" rx="2" />
                <path
                  stroke="#fff"
                  stroke-linecap="round"
                  d="M14 22v-7.995c-.005-5.135 3.923-9.438 9.086-9.954S32.967 6.974 34 12.006"
                />
                <path stroke="#000" stroke-linecap="round" d="M24 30v6" />
              </g>
            </mask>
          </defs>
          <path fill="black" d="M0 0h48v48H0z" mask="url(#ipSUnlock0)" />
        </svg>
      </button>
    {/if}
  </div>

  <div>
    <!-- 이동 시간 -->
    {formatSecToMMSS($elapsedTime)}
    <div>
      <!-- 움직인 거리 -->
      이동거리:
      {$totalDistance}
      <!-- 평균 속도 -->
      페이스:
      {$currentPace}
      <!-- 남은 거리 -->
      {#if hasRoute}
        남은 거리:
      {/if}
    </div>
  </div>
</div>

<style>
  /* .leaflet-bottom-center {
    width: 100vw;
  }
  button {
    left: 50%;
    position: fixed;
    transform: translate(-50%, 0%);
  } */

  /* 지도 안의 버튼 스타일 */
  /* .lock-btn {
    position: relative;
    z-index: 100000; 
    pointer-events: auto; 
  } 
  */
  button {
    width: 4rem;
    height: 4rem;
    border: 0;
    background-color: transparent;
    transition-property: background-color, background-opacity;
    transition-duration: 250ms;
    border-radius: 0.375rem;
  }
</style>

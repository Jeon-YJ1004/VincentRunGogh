<script lang="ts">
  import { Button } from 'flowbite-svelte';
  import { replace } from 'svelte-spa-router';
  import { GradientButton } from 'flowbite-svelte';
  import { ArrowRightOutline, RedoOutline } from 'flowbite-svelte-icons';
  export let onClose: any;
  export let route;
  export let type;
  export let routeId: string | null = null;
  export let drawingId: number | null = null;

  // 너무 멀리 떨어진 루트 상세 접근시 시작버튼 비활성화
  let isFar: boolean = Math.floor(route.distanceFromUser / 1000) > 5.0;

  // 취소시  돌아가기
  function goBack() {
    if (onClose) onClose();
  }

  // 시작 시 이동
  function startDrawing() {
    let startURL = '';
    if (routeId && drawingId) {
      startURL = `/drawingmap?drawingId=${drawingId}&routeId=${routeId}`;
    } else if (drawingId) {
      startURL = `/drawingmap?drawingId=${drawingId}`;
    } else {
      startURL = `/drawingmap?routeId=${routeId}`;
    }
    console.log(startURL);
    replace(startURL);
    if (onClose) onClose();
  }
</script>

<div>
  <div id="canvas-img">
    <!-- SVG 프레임 -->
    <svg
      id="frame-svg"
      xmlns="http://www.w3.org/2000/svg"
      width="330"
      height="330"
      viewBox="0 0 369 369"
      fill="none"
    >
      <!-- 프레임 모양을 그리는 부분 -->
      <path d="M22.2695 346.728H369L346.73 368.994H0L22.2695 346.728Z" fill="#AD9D8D" />
      <path
        d="M22.2671 346.728L22.2671 0.00057665L-5.37221e-06 19.0858L-5.24521e-06 368.997L22.2671 346.728Z"
        fill="#AD9D8D"
      />
      <path
        d="M300.605 367.417C308.558 364.766 322.872 349.924 345.14 367.417L367.405 346.727H130.42H22.266L0.00371167 369.001C7.95625 366.35 14.3143 349.923 36.5814 367.416C44.5339 364.765 55.6674 349.923 77.9345 367.416C85.8871 364.765 100.202 349.923 122.469 367.416C130.421 364.765 144.736 349.924 167.003 367.417C174.955 364.766 189.27 349.922 211.537 367.414C219.49 364.764 233.804 349.924 256.071 367.417C264.024 364.766 278.338 349.924 300.605 367.417Z"
        fill="url(#paint0_linear_1029_931)"
      />
      <path
        d="M0.00125062 268.793C7.95378 266.143 11.1325 302.204 0.000171401 313.342C5.3017 321.819 12.7246 344.82 0.00371167 369.001L22.266 346.727L22.2679 0L0.00371167 19.0861C7.95625 16.4355 11.1323 36.5771 0 47.715C7.95253 45.0644 11.136 79.5204 0.00371167 90.6582C7.95625 88.0077 11.1325 122.464 0.000171401 133.602C7.95271 130.951 11.1342 168.595 0.00185584 179.733C7.95439 177.082 11.136 213.122 0.00371167 224.26C7.95625 221.609 11.1335 257.655 0.00125062 268.793Z"
        fill="url(#paint1_linear_1029_931)"
      />

      <!-- 클리핑 패스 정의 -->
      <clipPath id="clipShape">
        <!-- 여기서 프레임의 클리핑 영역을 정의합니다 -->
        <rect x="10" y="10" width="369" height="369" />
      </clipPath>

      <defs>
        <linearGradient
          id="paint0_linear_1029_931"
          x1="365.817"
          y1="-2.53864e-05"
          x2="0.00423596"
          y2="368.998"
          gradientUnits="userSpaceOnUse"
        >
          <stop stop-color="white" />
          <stop offset="1" stop-color="#CCCCCC" />
        </linearGradient>
        <linearGradient
          id="paint1_linear_1029_931"
          x1="183.703"
          y1="5.45097e-06"
          x2="0.0035463"
          y2="368.995"
          gradientUnits="userSpaceOnUse"
        >
          <stop stop-color="white" />
          <stop offset="1" stop-color="#D1D1D1" />
        </linearGradient>
      </defs>
    </svg>
    <!-- 이미지 -->
    {#if type != 'routeList'}
      {#if route.drawingImage}
        <img id="map-image" src={route.drawingImage} alt="" />
      {:else}
        <img id="map-image" src={type} alt="" />
      {/if}
    {:else}
      <img id="map-image" src={route.artImage} alt="" />
    {/if}
  </div>

  <h2 class="font-bold my-4 text-xl">{route.title}</h2>
  {#if type === 'routeList'}
    <p class="my-4">나와의 거리 {parseFloat((route.distanceFromUser / 1000).toFixed(2))}km</p>
  {:else}
    <p class="my-4">루트 길이 {parseFloat((route.distance / 1000).toFixed(2))}km</p>
  {/if}
  <div class="flex justify-around">
    <GradientButton color="pinkToOrange" size="sm" on:click={goBack} pill>
      <RedoOutline class="me-2" size="sm" />돌아가기
    </GradientButton>
    <GradientButton color="greenToBlue" size="sm" on:click={startDrawing} pill>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="1.22em"
        height="1.4em"
        viewBox="0 0 416 512"
        class="me-2"
        ><path
          fill="currentColor"
          d="M272 96c26.51 0 48-21.49 48-48S298.51 0 272 0s-48 21.49-48 48s21.49 48 48 48M113.69 317.47l-14.8 34.52H32c-17.67 0-32 14.33-32 32s14.33 32 32 32h77.45c19.25 0 36.58-11.44 44.11-29.09l8.79-20.52l-10.67-6.3c-17.32-10.23-30.06-25.37-37.99-42.61M384 223.99h-44.03l-26.06-53.25c-12.5-25.55-35.45-44.23-61.78-50.94l-71.08-21.14c-28.3-6.8-57.77-.55-80.84 17.14l-39.67 30.41c-14.03 10.75-16.69 30.83-5.92 44.86s30.84 16.66 44.86 5.92l39.69-30.41c7.67-5.89 17.44-8 25.27-6.14l14.7 4.37l-37.46 87.39c-12.62 29.48-1.31 64.01 26.3 80.31l84.98 50.17l-27.47 87.73c-5.28 16.86 4.11 34.81 20.97 40.09c3.19 1 6.41 1.48 9.58 1.48c13.61 0 26.23-8.77 30.52-22.45l31.64-101.06c5.91-20.77-2.89-43.08-21.64-54.39l-61.24-36.14l31.31-78.28l20.27 41.43c8 16.34 24.92 26.89 43.11 26.89H384c17.67 0 32-14.33 32-32s-14.33-31.99-32-31.99"
        /></svg
      >드로잉 시작하기</GradientButton
    >
  </div>
</div>

<style>
  #canvas-img {
    position: relative;
    width: 100%;
    height: auto;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  #map-image {
    position: absolute;
    bottom: 17px;
    left: 7px;
    width: 97.5%;
    height: 97.5%;
    object-fit: cover; /* 이미지를 프레임 안에 잘 맞추기 위해 사용 */
    clip-path: url(#clipShape); /* SVG 프레임의 빈 공간에 이미지를 배치 */
    z-index: 0;
  }

  #frame-svg {
    width: 100%;
    height: auto;
    position: relative;
    z-index: 1;
  }
</style>

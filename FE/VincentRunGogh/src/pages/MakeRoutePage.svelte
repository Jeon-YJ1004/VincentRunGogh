<script lang="ts">
  import { onMount } from 'svelte';
  import L, { latLng } from 'leaflet';
  import 'leaflet/dist/leaflet.css';
  import 'leaflet-draw';
  import 'leaflet-draw/dist/leaflet.draw.css';
  import Swal from 'sweetalert2';
  import { replace } from 'svelte-spa-router';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import { Button, Input, GradientButton, Label, Card } from 'flowbite-svelte';
  import {
    LockSolid,
    LockOpenSolid,
    RedoOutline,
    ArrowRightOutline,
    HomeSolid,
  } from 'flowbite-svelte-icons';
  // Chapter 2
  import html2canvas from 'html2canvas';
  import { Canvg, SVGElement } from 'canvg';
  import { makeRoute, sendArtLine } from '@/api/routeApi';

  //진행 상태 변수
  let isChapterOne = true;

  let map: L.Map;
  let currentPolyline: L.Polyline | null = null;
  let routePolyline: L.Polyline | null = null;
  let currentLatLngs: L.LatLng[] = [];
  let isDrawing: boolean = false;
  let isMouseDown: boolean = false;
  let isTouchDown: boolean = false;
  let isLocked: boolean = false;
  let isFixed: boolean = false;
  let showControls: boolean = true;
  let northWest: L.LatLng | null = null;
  let southEast: L.LatLng | null = null;
  let drawForm: Record<string, any> = {};
  let skip: number = 0;
  let shortenLatLngs: { lat: number; lng: number }[] = [];
  let latLngMessageList: string[] = [
    '지도를 움직여 \n 원하는 장소를 고르신 후 고정해주세요',
    '지도 내에서 한붓그리기로 \n 원하는 그림을 표현해보세요!',
    '위 아트를 기반으로 \n 루트를 생성할까요?',
  ];
  let latLngMessage: string = latLngMessageList[0];

  //초기 렌더링
  onMount(() => {
    //지도 생성
    map = L.map('map', { zoomControl: false }).setView([36.3593, 127.3416], 16);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap',
      crossOrigin: 'anonymous',
    }).addTo(map);
    // 동시 터치 방지
    map.on('touchstart', function (e) {
      if (e.touches && e.touches.length > 1) {
        e.preventDefault();
      }
    });
    //지도 재계산
    setTimeout(() => {
      map.invalidateSize();
    }, 100);
  });

  // Chapter 1 -----------------------------------------------/

  //지도 잠그기
  function lockMap() {
    isLocked = true;
    isDrawing = true;
    map.dragging.disable();
    map.scrollWheelZoom.disable();
    map.touchZoom.disable();
    map.doubleClickZoom.disable();
    map.boxZoom.disable();

    const bounds = map.getBounds();
    northWest = bounds.getNorthWest();
    southEast = bounds.getSouthEast();
  }

  //지도 잠금 풀기
  function unlockMap(condition: boolean) {
    isLocked = false;
    isDrawing = false;

    showControls = condition;
    map.dragging.enable();
    map.scrollWheelZoom.enable();
    map.touchZoom.enable();
    map.doubleClickZoom.enable();
    map.boxZoom.enable();
  }

  //그리기 시작
  function startDrawing() {
    isDrawing = true;
    isFixed = true;
    latLngMessage = latLngMessageList[1];
    lockMap();
  }

  //고정해제
  function returnMap() {
    isDrawing = false;
    isFixed = false;
    unlockMap(true);
  }

  //그리기 종료
  function stopDrawing() {
    if (isDrawing) {
      isDrawing = false;
      latLngMessage = latLngMessageList[2];
    }
  }

  //마우스 움직일 때

  function onMouseMove(e) {
    // e.preventDefault();
    if ((isDrawing && isMouseDown) || isTouchDown) {
      if (!currentPolyline) {
        currentLatLngs = [e.latlng];
        currentPolyline = new L.Polyline(currentLatLngs, {
          color: 'blue',
        }).addTo(map);
      } else {
        currentLatLngs.push(e.latlng);
        currentPolyline.setLatLngs(currentLatLngs);
      }
    }
  }

  // 그리기 종료 시 저장
  function stopAndSavePolyline() {
    stopDrawing();
    isFixed = false;
    unlockMap(false);
    for (let v in currentLatLngs) {
      if (skip % 5 === 0) {
        let dot = {
          lat: currentLatLngs[v].lat,
          lng: currentLatLngs[v].lng,
        };
        shortenLatLngs.push(dot);
      }
      skip++;
    }
  }
  // 클릭했을 때
  function onMouseDown() {
    if (isDrawing) {
      isMouseDown = true;
    }
  }

  // 마우스 클릭 뗐을 때
  function onMouseUp() {
    if (isDrawing && isMouseDown) {
      isMouseDown = false;
      stopAndSavePolyline();
    }
  }

  // 터치 시작할 때
  function onTouchStart(e) {
    e.preventDefault();
    if (isDrawing) {
      isTouchDown = true;
    }
  }

  // 터치 움직일 때
  function onTouchMove(e) {
    // e.preventDefault();
    const touch = e.touches[0]; // 터치 위치 가져오기
    const latlng = map.mouseEventToLatLng(touch); // 터치 좌표를 지도 좌표로 변환
    onMouseMove({ latlng }); // 마우스 움직임과 동일하게 처리
  }

  // 터치 종료할 때
  function onTouchEnd() {
    if (isDrawing && isTouchDown) {
      isTouchDown = false;
      stopAndSavePolyline(); // 중복 로직 함수로 분리
    }
  }

  //다시그리기
  function redraw(isRoute: boolean) {
    if (currentPolyline) {
      map.removeLayer(currentPolyline);
      currentPolyline = null;
    }
    if (routePolyline) {
      map.removeLayer(routePolyline);
      routePolyline = null;
    }
    currentLatLngs = [];
    latLngMessage = latLngMessageList[0];
    showControls = true;
    isChapterOne = true;
    skip = 0;
    shortenLatLngs = [];
    if (isRoute) {
      let footer = document.querySelector('#makeroute-footer');
      footer.style.height = '30vh';
    }
  }

  //다음 - 결과물 객체
  async function next() {
    console.log(shortenLatLngs, currentLatLngs);
    drawForm = {
      positionList: shortenLatLngs,
      leftLat: northWest.lat,
      leftLng: northWest.lng,
      rightLat: southEast.lat,
      rightLng: southEast.lng,
    };
    Swal.fire({
      title: '루트를 생성중입니다...',
      showConfirmButton: false,
      didOpen: async () => {
        // 루트생성 API 요청
        try {
          let routeResponse = await sendArtLine(drawForm);
          routePolyline = new L.Polyline(routeResponse.data.positionList, {
            color: 'red',
          }).addTo(map);
          Swal.close();
        } catch (error) {
          {
            throw error;
          }
        }
      },
    }).then((result) => {
      // 지도에 받은거 그리기
      isChapterOne = false;
    });
  }

  onMount(() => {
    map.on('mousemove', onMouseMove);
    map.on('mousedown', onMouseDown);
    map.on('mouseup', onMouseUp);
    map.getContainer().addEventListener('touchstart', onTouchStart, { passive: false });
    map.getContainer().addEventListener('touchmove', onTouchMove, { passive: false });
    map.getContainer().addEventListener('touchend', onTouchEnd, { passive: false });
  });

  // Chapter 2 -----------------------------------------------/

  let isConfirm: boolean = false;
  let isSubmit: boolean = false;
  let inputName = '';
  let isLoading: boolean = false;
  let routeInfo: {
    routeId: number;
    artImage: string;
    distance: number;
    predictTime: number;
  } | null = null;
  // 루트 확인후 진행
  function routeConfirm() {
    isConfirm = true;
    let footer = document.querySelector('#makeroute-footer');
    footer.style.height = '30vh';
  }

  //캡쳐 함수

  //백 전송용 이미지정보 변수
  let finalImage: string = '';

  function mapCapture(target: string) {
    map.invalidateSize();

    return new Promise(async (resolve, reject) => {
      try {
        // 사용 변수 지정
        let svgData = null;
        let mapData = null;
        let svgSizeX = null;
        let svgSizeY = null;

        // SVG 캡쳐
        const svg: any = document.querySelector(`${target} svg`); // SVG를 선택
        svgSizeX = svg.width.baseVal.value;
        svgSizeY = svg.height.baseVal.value;

        const svgString = new XMLSerializer().serializeToString(svg);

        // Canvg를 이용해 SVG를 캔버스에 그립니다
        const svgCanvas = document.createElement('canvas');
        const svgCtx: any = svgCanvas.getContext('2d');
        const canvg = await Canvg.fromString(svgCtx, svgString);
        await canvg.render();

        // SVG를 Base64로 변환
        svgData = svgCanvas.toDataURL('image/png');
        svg.style.display = 'none';

        // 지도 캡쳐
        const mapCanvas = await html2canvas(document.querySelector(target), {
          useCORS: true,
          scale: 1,
        });
        mapData = mapCanvas.toDataURL('image/png');
        svg.style.display = '';

        // 캔버스 및 그리기 작업
        const finalCanvas = document.createElement('canvas');
        finalCanvas.width = 350;
        finalCanvas.height = 350;
        const ctx = finalCanvas.getContext('2d');

        const loadImage = (src) => {
          return new Promise((resolve, reject) => {
            const img = new Image();
            img.src = src;
            img.onload = () => resolve(img);
            img.onerror = reject;
          });
        };

        // 이미지 로드 및 캔버스 그리기
        const mapImg = await loadImage(mapData);
        const svgImg = await loadImage(svgData);

        const cropX = (mapCanvas.width - 350) / 2;
        const cropY = (mapCanvas.height - 350) / 2;
        const svgCropX = (svgSizeX - 350) / 2;
        const svgCropY = (svgSizeY - 350) / 2;

        // 지도 그리기
        ctx.drawImage(mapImg, cropX, cropY, 350, 350, 0, 0, 350, 350);
        // SVG 그리기
        ctx.drawImage(svgImg, svgCropX, svgCropY, 350, 350, 0, 0, 350, 350);

        // 결과 이미지를 Base64로 변환하여 반환
        finalImage = finalCanvas.toDataURL('image/png');

        resolve(finalImage);
      } catch (error) {
        reject(error);
      }
    });
  }
  // 이름 확인 -> 사진찍고 저장하고 마무리

  async function nameConfirm() {
    // 캡쳐하기
    mapCapture('#map')
      .then(async (finalImage) => {
        isSubmit = true;
        isLoading = true;
        await submitRoute();
      })
      .catch((error) => {
        {
          throw error;
        }
      });
  }
  // 이름 글자수 검사
  let errorMessage: string = '';

  function validateInput() {
    if (inputName.length === 0) {
      errorMessage = '루트 이름을 입력해 주세요.';
      Swal.fire({
        text: errorMessage,
        icon: 'error',
      });
    } else if (inputName.length > 10) {
      errorMessage = '최대 10글자까지만 가능합니다.';
      Swal.fire({
        text: errorMessage,
        icon: 'error',
      });
    } else {
      errorMessage = '';
      Swal.fire({
        title: '루트를 저장중입니다...',
        showConfirmButton: false,
        didOpen: async () => {
          try {
            await nameConfirm(); // 비동기 작업을 대기
            Swal.close(); // 작업이 완료된 후에 닫기
          } catch (error) {
            console.error(error);
            Swal.fire({
              icon: 'error',
              title: '오류가 발생했습니다',
              text: '루트를 저장하는 도중 문제가 발생했습니다.',
            });
          }
        },
      }).then(() => {
        isLoading = false;
      });
    }
  }
  // 저장하기

  async function submitRoute() {
    let makeRouteForm = {
      title: inputName,
      artImage: finalImage.split(',')[1],
    };

    try {
      let responseRoute = await makeRoute(makeRouteForm);
      routeInfo = responseRoute.data;
    } catch (error) {
      {
        throw error;
      }
    }
  }
  // 마무리하기
</script>

<div id="makeroute-header" class="flex justify-center items-center">
  <BackButton />
  <h2 class="font-bold">루트 생성</h2>
</div>
<div id="make-route">
  {#if !isSubmit}
    <div id="map"></div>
  {:else}
    <div id="route-result">
      <h2 id="result-title">루트 저장 완료!</h2>
      <Card>
        <div id="canvas-img">
          <!-- SVG 프레임 -->
          <svg
            id="frame-svg"
            xmlns="http://www.w3.org/2000/svg"
            width="330"
            height="330"
            viewBox="0 0 370 370"
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
          <img id="map-image" src={routeInfo?.artImage} alt="" />
        </div>
        {#if routeInfo}
          <div class="mb-3">
            <h2 id="result-title-card">{inputName}</h2>
          </div>
          <div class="flex justify-around mb-3">
            <div>
              루트 총 길이 <br /> <br />
              <span class="font-bold">{parseFloat((routeInfo.distance / 1000).toFixed(2))}km</span>
            </div>
            <div>
              예상 소요 시간 <br /> <br />
              <span class="font-bold">{Math.floor(routeInfo.predictTime / 60)}분</span>
            </div>
          </div>
        {/if}
      </Card>
    </div>
  {/if}
  <!-- 첫 제출 전까지 -->
  <div id="makeroute-footer" class="flex flex-col items-center justify-end pb-3">
    {#if isChapterOne}
      {#if isFixed}
        <div
          class="fixed left-1/2 transform -translate-x-1/2 text-red-500 font-bold bg-white px-2 py-1"
          style="top: 12vh;"
        >
          지도가 고정되었습니다.
        </div>
      {/if}
      <div>
        <p class="mb-3">{latLngMessage}</p>
      </div>

      <div id="controls">
        {#if showControls && !isFixed}
          <GradientButton color="pinkToOrange" size="sm" on:click={startDrawing} pill>
            <LockSolid class="me-2" size="sm" /> 고정하기
          </GradientButton>
        {/if}
        {#if showControls && isFixed}
          <GradientButton color="greenToBlue" size="sm" on:click={returnMap} pill>
            <LockOpenSolid class="me-2" size="sm" /> 고정해제
          </GradientButton>
        {/if}

        {#if !showControls}
          <GradientButton color="pinkToOrange" size="sm" on:click={() => redraw(false)} pill>
            <RedoOutline class="me-2" size="sm" />다시그리기
          </GradientButton>
          <GradientButton color="purpleToBlue" size="sm" on:click={next} pill>
            다음 <ArrowRightOutline class="ms-2" size="sm" />
          </GradientButton>
        {/if}
      </div>
    {/if}
    <!-- 제출 후 -->
    {#if !isChapterOne}
      <div class="text-center">
        {#if !isConfirm && !isSubmit}
          <p class="mb-3">위 루트로 진행하시겠습니까?</p>
          <div id="controls">
            <GradientButton color="pinkToOrange" size="sm" on:click={() => redraw(true)} pill>
              <RedoOutline class="me-2" size="sm" />다시그리기
            </GradientButton>
            <GradientButton color="purpleToBlue" size="sm" on:click={routeConfirm} pill>
              다음 <ArrowRightOutline class="ms-2" size="sm" />
            </GradientButton>
          </div>
        {/if}
        {#if isConfirm && !isSubmit}
          <div class="mb-6">
            <Label for="input-group-1" class="block mb-2">루트의 이름을 지어주세요!</Label>
            <Input
              bind:value={inputName}
              id="route-name"
              type="text"
              color="base"
              placeholder="최대 10글자까지 입력가능합니다"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="1.22em"
                height="1.5em"
                viewBox="0 0 416 512"
                slot="left"
                class="text-gray-400"
                ><path
                  fill="currentColor"
                  d="M272 96c26.51 0 48-21.49 48-48S298.51 0 272 0s-48 21.49-48 48s21.49 48 48 48M113.69 317.47l-14.8 34.52H32c-17.67 0-32 14.33-32 32s14.33 32 32 32h77.45c19.25 0 36.58-11.44 44.11-29.09l8.79-20.52l-10.67-6.3c-17.32-10.23-30.06-25.37-37.99-42.61M384 223.99h-44.03l-26.06-53.25c-12.5-25.55-35.45-44.23-61.78-50.94l-71.08-21.14c-28.3-6.8-57.77-.55-80.84 17.14l-39.67 30.41c-14.03 10.75-16.69 30.83-5.92 44.86s30.84 16.66 44.86 5.92l39.69-30.41c7.67-5.89 17.44-8 25.27-6.14l14.7 4.37l-37.46 87.39c-12.62 29.48-1.31 64.01 26.3 80.31l84.98 50.17l-27.47 87.73c-5.28 16.86 4.11 34.81 20.97 40.09c3.19 1 6.41 1.48 9.58 1.48c13.61 0 26.23-8.77 30.52-22.45l31.64-101.06c5.91-20.77-2.89-43.08-21.64-54.39l-61.24-36.14l31.31-78.28l20.27 41.43c8 16.34 24.92 26.89 43.11 26.89H384c17.67 0 32-14.33 32-32s-14.33-31.99-32-31.99"
                /></svg
              >
            </Input>
          </div>
          <GradientButton color="purpleToBlue" size="sm" on:click={validateInput} pill>
            다음 <ArrowRightOutline class="ms-2" size="sm" />
          </GradientButton>
        {/if}
        {#if isSubmit}
          <GradientButton color="redToYellow" size="sm" on:click={() => replace('/')} pill>
            <RedoOutline class="me-2" size="sm" /> 홈으로
          </GradientButton>
        {/if}
      </div>
    {/if}
  </div>
</div>

<style>
  #makeroute-header {
    z-index: 1000;
    position: fixed;
    height: 10vh;
    width: 100%;
    background: linear-gradient(
      to bottom,
      rgba(255, 255, 255, 1) 80%,
      rgba(255, 255, 255, 0.6) 90%,
      rgba(255, 255, 255, 0) 100%
    );
  }

  #make-route {
    display: flex;
    flex-direction: column;
    place-items: center;
  }

  #map {
    height: 100vh;
    width: 100vw;
    max-width: 100%;
    pointer-events: auto;
    touch-action: auto;
  }

  #controls {
    margin: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
  }

  #makeroute-footer {
    z-index: 1000;
    position: fixed;
    bottom: 0px;
    height: 20vh;
    width: 100%;
    transition: 300ms;
    background: linear-gradient(
      to top,
      rgba(255, 255, 255, 1) 80%,
      rgba(255, 255, 255, 0.6) 90%,
      rgba(255, 255, 255, 0) 100%
    );
  }

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
    bottom: 16px;
    left: 6px;
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

  #route-result {
    z-index: 1001;
    width: 80%;
    height: 60%;
    margin-top: 25%;
    text-align: center;
  }

  #result-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 5%;
    margin-top: 5%;
  }

  #result-title-card {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 10%;
    margin-top: 10%;
  }
</style>

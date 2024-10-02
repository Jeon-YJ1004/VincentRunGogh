<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import L, { Map as LeafletMap, Marker, Polyline } from 'leaflet';
  import type { LatLng, LatLngExpression, Control } from 'leaflet';
  import { push, pop, replace } from 'svelte-spa-router';

  import 'leaflet/dist/leaflet.css';
  import 'leaflet-draw';
  import 'leaflet-draw/dist/leaflet.draw.css';
  import html2canvas from 'html2canvas';
  import { Canvg } from 'canvg';

  import Swal from 'sweetalert2';
  import { location, querystring } from 'svelte-spa-router';
  import { get, writable } from 'svelte/store';
  import { Button, Input, GradientButton, Label, Card } from 'flowbite-svelte';
  import { LockSolid, LockOpenSolid } from 'flowbite-svelte-icons';

  import { elapsedTime, posList, route, totalDistance, drawingStore } from '@/stores/drawingStore';
  import { startDrawing, completeDrawing, saveDrawing } from '@/api/drawingApi';
  import SaveRouteDrawing from '@components/cards/SaveRouteDrawing.svelte';

  let isLoading = false;
  // 폼 형태 변수 임시저장 or 완료
  let isComplete = $querystring?.split('=')[1] === 'complete';
  let map: LeafletMap;
  let mapRef: HTMLElement;
  let posPolyline: Polyline | null = null;
  let routePolyline: Polyline | null = null;

  let isLocked: boolean = false;
  let latLngMessageList: string[] = ['지도를 움직여 표지를 지정해주세요', '드로잉을 저장했습니다!'];
  let latLngMessage: string = latLngMessageList[0];

  let drawingDetailImage = writable('');
  let drawingImage = writable('');
  let showingImage = writable('');

  // 속도에 따른 색상을 반환하는 함수
  function getSpeedColor(speed: number): string {
    return speed < 30
      ? '#bd0026'
      : speed < 15
        ? '#f03b20'
        : speed < 10
          ? '#fd8d3c'
          : speed < 5
            ? '#fecc5c'
            : '#ffffb2';
  }

  // posList에 담긴 좌표를 기준으로 선을 그리고 속도에 따라 그라데이션 처리
  function drawPosListLines(map: L.Map) {
    const posListData = get(posList);
    const segments: L.LatLng[][] = [];
    for (let i = 1; i < posListData.length; i++) {
      const start = posListData[i - 1].latlng;
      const end = posListData[i].latlng;
      const segment = [start, end];
      segments.push(segment);
    }

    // 각 구간별로 선을 그리며, 속도에 따라 색상 적용
    segments.forEach((segment, index) => {
      const speed = posListData[index + 1].speed || 0;
      const polyline = L.polyline(segment, {
        color: getSpeedColor(speed),
        weight: 5,
        opacity: 0.8,
      }).addTo(map);
    });
  }
  // route 배열에 있는 좌표를 기준으로 점선 스타일로 선을 그리기
  function drawRouteLines(map: L.Map) {
    const routeData = get(route);
    const routeLatlngs: L.LatLng[] = routeData.map((item) => item.latlng);
    routePolyline = L.polyline(routeLatlngs, {
      color: '#000',
      weight: 3,
      dashArray: '5, 10',
      opacity: 0.5,
    }).addTo(map);
    routePolyline.setStyle({ zIndex: -1 });
  }

  // 전체 맵에 선을 그리는 함수
  function drawLinesOnMap(map: L.Map) {
    drawPosListLines(map);
    $route && drawRouteLines(map);
  }

  // 지도 초기화 및 선 그리기
  function initializeMap() {
    map = L.map('map').setView([36.3528192, 127.3102336], 16);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      crossOrigin: 'anonymous',
    }).addTo(map);

    drawLinesOnMap(map);

    // posList에 있는 모든 좌표에 맞게 지도의 위치 및 줌 조정
    const posListData = get(posList).map((item: any) => item.latlng);
    const bounds = L.latLngBounds(posListData);
    map.fitBounds(bounds, { padding: [50, 50] });
  }

  let imgData1, imgData2;

  async function changeMapWithSingleColor() {
    // posList 선을 단일 색상으로 그리기 위해 기존의 선 제거 후 재생성
    if (posPolyline) map.removeLayer(posPolyline);

    const posListData = get(posList);
    const segments: L.LatLng[][] = [];
    for (let i = 1; i < posListData.length; i++) {
      const start = posListData[i - 1].latlng;
      const end = posListData[i].latlng;
      const segment = [start, end];
      segments.push(segment);
    }

    segments.forEach((segment) => {
      L.polyline(segment, {
        color: '#000', // 단일 색상 적용
        weight: 5,
        opacity: 0.8,
      }).addTo(map);
    });
  }

  async function submitDrawing() {
    isLoading = true;
    const { endInfo } = get(drawingStore);

    const data = {
      drawingImage: $drawingImage,
      drawingDetailImage: $drawingDetailImage,
      step: 0,
      ...endInfo,
    };
    if (isComplete) {
      data.title = inputName;
    }

    console.log(data);
    if (isComplete) {
      completeDrawing(
        get(drawingStore).drawingId,
        data,
        (response) => {
          isLocked = true;
          isLoading = false;
        },
        (error) => {
          isLoading = false;
        }
      );
    } else {
      saveDrawing(
        get(drawingStore).drawingId,
        data,
        (response) => {
          isLoading = false;
          isLocked = true;
        },
        (error) => {
          isLoading = false;
        }
      );
    }
  }
  //지도 잠그기
  let errorMessage: string = '';

  async function lockMap() {
    if (isComplete) {
      if (inputName.length === 0) {
        errorMessage = '루트 이름을 입력해 주세요.';
        Swal.fire({
          text: errorMessage,
          icon: 'error',
        });
        return;
      } else if (inputName.length > 10) {
        errorMessage = '최대 10글자까지만 가능합니다.';
        Swal.fire({
          text: errorMessage,
          icon: 'error',
        });
        return;
      } else errorMessage = '';
    }
    await mapCapture(true);
    await changeMapWithSingleColor();

    await mapCapture(false);
    if (!isLoading) {
      await submitDrawing();
    }
  }

  let inputName: string = '';

  async function mapCapture(isTypeColorLine: boolean) {
    map.invalidateSize();
    const svg = document.querySelector('svg');
    const svgString = new XMLSerializer().serializeToString(svg);
    const svgCanvas = document.createElement('canvas');
    const svgCtx = svgCanvas.getContext('2d');
    const canvg = await Canvg.fromString(svgCtx, svgString);

    await canvg.render(); // Canvg 렌더링이 완료될 때까지 기다림

    svg.style.display = 'none';

    const mapCanvas = await html2canvas(document.querySelector('#map'), {
      useCORS: true,
    });

    svg.style.display = '';

    const finalCanvas = document.createElement('canvas');
    finalCanvas.width = 350;
    finalCanvas.height = 350;
    const ctx = finalCanvas.getContext('2d');
    const mapImg = new Image();
    const svgImg = new Image();

    // 데이터 URL 설정
    mapImg.src = mapCanvas.toDataURL('image/png');
    svgImg.src = svgCanvas.toDataURL('image/png');

    // 이미지 로딩을 Promise로 처리
    await new Promise((resolve) => {
      mapImg.onload = () => {
        svgImg.onload = () => {
          const cropX = (mapCanvas.width - 350) / 2;
          const cropY = (mapCanvas.height - 350) / 2;
          const svgCropX = (svgCanvas.width - 350) / 2;
          const svgCropY = (svgCanvas.height - 350) / 2;

          ctx.drawImage(mapImg, cropX, cropY, 350, 350, 0, 0, 350, 350);
          ctx.drawImage(svgImg, svgCropX, svgCropY, 350, 350, 0, 0, 350, 350);

          resolve();
        };
      };
    });

    const finalImage = finalCanvas.toDataURL('image/png');
    if (isTypeColorLine) {
      console.log('속도 색');

      $drawingDetailImage = finalImage.split(',')[1];
    } else {
      console.log('단일 색');
      $drawingImage = finalImage.split(',')[1];
      $showingImage = finalImage;
    }
  }

  //초기 렌더링
  onMount(() => {
    initializeMap();
    // console.log(get(drawingStore));
  });
</script>

<div id="makeroute-header" class="flex justify-center items-center">
  {#if isComplete}
    <h2 class="font-bold">드로잉 완성</h2>
  {:else}
    <h2 class="font-bold">드로잉 임시저장</h2>
  {/if}
</div>
<div class="make-route">
  {#if !isLocked}
    <div bind:this={mapRef} id="map"></div>
    <div id="makeroute-footer" class="flex flex-col items-center justify-end">
      <div
        class="fixed left-1/2 transform -translate-x-1/2 text-red-350 font-bold px-2 py-1 top-[12vh] overflow-hidden text-ellipsis whitespace-nowrap bg-[#FFFFFF67]"
      >
        {latLngMessage}
      </div>

      {#if isComplete}
        <div class="mb-6">
          <Label for="input-group-1" class="block mb-2">드로잉 이름</Label>
          <Input
            bind:value={inputName}
            id="route-name"
            type="text"
            color="base"
            placeholder="최대 10글자까지 입력가능합니다"
            required
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
      {/if}

      <div id="controls">
        <GradientButton color="pinkToOrange" size="sm" on:click={lockMap} pill>
          <LockSolid class="me-2" size="sm" /> 저장하기
        </GradientButton>
      </div>
    </div>
  {:else}
    <SaveRouteDrawing
      title={inputName}
      distance={$totalDistance}
      time={$elapsedTime}
      image={$showingImage}
      isRoute={false}
    />
  {/if}
</div>

<!-- {#if isLoading}
  <Spinner />
{/if} -->

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
</style>

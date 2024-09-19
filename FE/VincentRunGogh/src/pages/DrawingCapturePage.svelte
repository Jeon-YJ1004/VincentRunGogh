<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import L, { Map as LeafletMap, Marker, Polyline } from 'leaflet';
  import type { LatLng, LatLngExpression, Control } from 'leaflet';
  import { push, pop, replace } from 'svelte-spa-router';

  import 'leaflet/dist/leaflet.css';
  import 'leaflet-draw';
  import 'leaflet-draw/dist/leaflet.draw.css';
  import Swal from 'sweetalert2';
  import { location, querystring } from 'svelte-spa-router';
  import { get } from 'svelte/store';

  import { elapsedTime, posList, route, totalDistance } from '@/stores/drawingStore';

  import html2canvas from 'html2canvas';
  import { Canvg } from 'canvg';

  // 폼 형태 변수 임시저장 or 완료
  let isComplete = $querystring?.split('=')[1] === 'complete';
  let map: LeafletMap;
  let mapRef: HTMLElement;
  let posPolyline: Polyline | null = null;
  let routePolyline: Polyline | null = null;

  let isLocked: boolean = false;
  let latLngMessageList: string[] = ['지도를 움직여 표지를 지정해주세요', '드로잉을 저장했습니다!'];
  let latLngMessage: string = latLngMessageList[0];

  let drawingImage: string = '';
  let drawingDetailImage: string = '';

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
  // 맵 캡쳐 함수 1: 지도, posList 선(속도에 따른 색상), 루트 선 포함
  async function captureMapWithLines() {
    // const mapCanvas = await html2canvas(mapRef, {
    //   useCORS: true,
    // });
    // const imageData = mapCanvas.toDataURL('image/png');
    return mapCapture(1);
  }

  // 맵 캡쳐 함수 2: 지도, posList 선(단일 색상)
  async function captureMapWithSingleColor() {
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

    // const mapCanvas = await html2canvas(mapRef, {
    //   useCORS: true,
    // });
    // const imageData = mapCanvas.toDataURL('image/png');
  }
  //지도 잠그기
  async function lockMap() {
    // 맵 잠금
    // map.dragging.disable();
    // map.scrollWheelZoom.disable();
    // map.touchZoom.disable();

    // 두 가지 캡쳐 함수 호출
    // mapCapture(1).then((image) => {
    //   console.log('Map with lines captured:', image);
    //   document.querySelector('#final1').src = image;
    // });
    // captureMapWithSingleColor();
    // mapCapture(2).then((image) => {
    //   console.log('Map with single color lines captured:', image);
    //   document.querySelector('#final2').src = image;
    // });
    await mapCapture('#final1');
    captureMapWithSingleColor();

    await mapCapture('#final2');
    isLocked = true;
  }

  let inputName: string = '';

  let finalImage = null;
  async function mapCapture(result: string) {
    map.invalidateSize();
    // 사용 변수 지정
    let svgData = null;
    let mapData = null;
    let svgSizeX = null;
    let svgSizeY = null;

    // SVG 캡쳐
    const svg = document.querySelector('svg'); // SVG를 선택합니다.
    // SVG 사이즈
    svgSizeX = svg.width.baseVal.value;
    svgSizeY = svg.height.baseVal.value;

    const svgString = new XMLSerializer().serializeToString(svg);

    // canvas 및 context 생성
    const svgCanvas = document.createElement('canvas');
    const svgCtx = svgCanvas.getContext('2d');

    // Canvg를 이용해 SVG를 캔버스에 그립니다
    const canvg = await Canvg.fromString(svgCtx, svgString);
    await canvg.render(); // 렌더링 완료될 때까지 기다림

    // 캔버스의 데이터를 base64로 변환
    svgData = svgCanvas.toDataURL('image/png');

    // svg 숨기기
    svg.style.display = 'none';

    // 지도 캡쳐
    const mapCanvas = await html2canvas(document.querySelector('#map'), {
      useCORS: true,
    });
    mapData = mapCanvas.toDataURL('image/png');

    // svg 다시보이기
    if (svg) {
      svg.style.display = '';
    }

    const finalCanvas = document.createElement('canvas');
    finalCanvas.width = 500;
    finalCanvas.height = 500;

    const ctx = finalCanvas.getContext('2d');

    // 지도, svg 모두 불러오기

    const mapImg = new Image();
    const svgImg = new Image();
    mapImg.src = mapData;
    svgImg.src = svgData;

    mapImg.onload = () => {
      svgImg.onload = () => {
        //중심 기준 자르기
        const cropX = (mapCanvas.width - 500) / 2;
        const cropY = (mapCanvas.height - 500) / 2;
        const svgCropX = (svgSizeX - 500) / 2;
        const svgCropY = (svgSizeY - 500) / 2;

        //지도 그리기
        ctx.drawImage(mapImg, cropX, cropY, 500, 500, 0, 0, 500, 500);
        ctx.drawImage(svgImg, svgCropX, svgCropY, 500, 500, 0, 0, 500, 500);

        //결과
        finalImage = finalCanvas.toDataURL('image/png');
        if (result === '#final1') {
          drawingImage = finalImage;
        } else drawingDetailImage = finalImage;
        document.querySelector(result).src = finalImage;
      };
    };
  }

  async function submitDrawing() {
    //TODO - 백에게 전달

    replace('/');
  }

  //초기 렌더링
  onMount(() => {
    console.log(get(posList));
    initializeMap();
  });
</script>

<div class="make-route">
  <!-- 첫 제출 전까지 -->
  <img id="final1" src="" alt="" />
  <img id="final2" src="" alt="" />
  {#if !isLocked}
    <div bind:this={mapRef} id="map"></div>
    <div>
      <p>{latLngMessage}</p>
    </div>

    {#if isComplete}
      <div>
        <label for="route-name">드로잉의 이름을 지어주세요!</label><br />
        <input bind:value={inputName} type="text" name="route-name" id="route-name" />
      </div>
    {/if}
    <div id="controls">
      <button on:click={lockMap}>고정하기</button>
    </div>
  {:else}
    <!-- 제출 후 -->
    드로잉 이름: {inputName}
    총길이: {$totalDistance}
    시간: {$elapsedTime}
    <button on:click={submitDrawing}>홈으로</button>
  {/if}
</div>

<style>
  #map {
    height: 60vh;
    width: 100vw;
    max-width: 100%;
    pointer-events: auto;
    touch-action: auto;
  }
  #controls {
    margin: 10px;
  }
  button {
    margin: 5px;
  }
  #final {
    /* width: 100%; */
    /* height: 100%; */
    object-fit: cover;
  }
</style>

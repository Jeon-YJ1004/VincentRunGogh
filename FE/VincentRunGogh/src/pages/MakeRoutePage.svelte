<script lang="ts">
  import { onMount } from 'svelte';
  import L, { latLng } from 'leaflet';
  import 'leaflet/dist/leaflet.css';
  import 'leaflet-draw';
  import 'leaflet-draw/dist/leaflet.draw.css';
  import Swal from 'sweetalert2';
  import MakeLoadingAlert from '@components/makeroute/MakeLoadingAlert.svelte';
  import { push } from 'svelte-spa-router';
  // Chapter 2
  import html2canvas from 'html2canvas';
  import { Canvg } from 'canvg';

  //진행 상태 변수
  let isChapterOne = true;

  let map: L.Map;
  let currentPolyline: L.Polyline | null = null;
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
    '지도를 움직여 원하는 장소를 고르신 후 고정해주세요',
    '지도 내에서 한붓그리기로 원하는 그림을 표현해보세요!',
    '위 아트를 기반으로 루트를 생성할까요?',
  ];
  let latLngMessage: string = latLngMessageList[0];

  //초기 렌더링
  onMount(() => {
    //지도 생성
    map = L.map('map').setView([36.3593, 127.3416], 16);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap',
      crossOrigin: 'anonymous',
    }).addTo(map);

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
  function unlockMap(condition) {
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
    currentPolyline.setLatLngs(currentLatLngs);
    currentPolyline.redraw();
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
  function onTouchStart() {
    if (isDrawing) {
      isTouchDown = true;
    }
  }

  // 터치 움직일 때
  function onTouchMove(e) {
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
  function redraw() {
    if (currentPolyline) {
      map.removeLayer(currentPolyline);
      currentPolyline = null;
    }
    currentLatLngs = [];
    latLngMessage = latLngMessageList[0];
    showControls = true;
    isChapterOne = true;
    skip = 0;
    shortenLatLngs = [];
  }

  //다음 - 결과물 객체
  function next() {
    drawForm = {
      positionList: shortenLatLngs,
      leftLat: northWest.lat,
      leftLng: northWest.lng,
      rightLat: southEast.lat,
      rightLng: southEast.lng,
    };
    console.log('결과 객체:', drawForm);
    Swal.fire({
      title: '아트를 생성중입니다...',
      html: '<div id="making-alert"></div>',
      showConfirmButton: false,
      didOpen: () => {
        // 'svelte-component'라는 ID를 가진 div에 Svelte 컴포넌트 렌더링
        new MakeLoadingAlert({
          target: document.getElementById('making-alert'),
        });
        setTimeout(() => {
          Swal.close();
        }, 1000);
      },
    }).then((result) => {
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

  function testing() {
    currentPolyline.setLatLngs(shortenLatLngs);
    currentPolyline.redraw();
  }

  // Chapter 2 -----------------------------------------------/

  let isConfirm = false;
  let isSubmit = false;
  let inputName = '';
  // 루트 확인후 진행
  function routeConfirm() {
    isConfirm = true;
  }

  //캡쳐 함수

  //백 전송용 이미지정보 변수
  let finalImage = null;

  async function mapCapture(target, result) {
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
    const mapCanvas = await html2canvas(document.querySelector(target), {
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
        document.querySelector(result).src = finalImage;
      };
    };
  }

  // 이름 확인 -> 사진찍고 저장하고 마무리
  async function nameConfirm() {
    isSubmit = true;
    // 캡쳐하기
    await mapCapture('#map', '#final');
    // 저장하기
    const makeRouteForm = {
      title: inputName,
      artImage: '',
    };
    // 마무리하기
  }
</script>

<div class="make-route">
  <div id="map"></div>
  <!-- 첫 제출 전까지 -->
  {#if isChapterOne}
    {#if isFixed}
      <p>지도가 고정되었습니다.</p>
    {/if}
    <div>
      <p>{latLngMessage}</p>
    </div>

    <div id="controls">
      {#if showControls && !isFixed}
        <button on:click={startDrawing}>고정하기</button>
      {/if}
      {#if showControls && isFixed}
        <button on:click={returnMap}>고정해제</button>
      {/if}

      {#if !showControls}
        <button on:click={redraw}>다시그리기</button>
        <button on:click={next}>다음</button>
      {/if}

      <button on:click={testing}>테스트</button>
    </div>
  {/if}
  <!-- 제출 후 -->
  {#if !isChapterOne}
    <div>
      {#if !isConfirm && !isSubmit}
        <p>위 루트로 진행하시겠습니까?</p>
        <button on:click={redraw}>다시만들기</button>
        <button on:click={routeConfirm}>다음</button>
      {/if}
      {#if isConfirm && !isSubmit}
        <label for="route-name">루트의 이름을 지어주세요!</label><br />
        <input bind:value={inputName} type="text" name="route-name" id="route-name" />
        <button on:click={nameConfirm}>다음</button>
      {/if}
    </div>
    {#if isSubmit}
      <!-- <div id="test"></div> -->
      <img id="final" src="" alt="" />
      <p>끝!</p>
      <button on:click={() => push('/')}>홈으로</button>
      <img src="" alt="" id="resultImage" />
    {/if}
  {/if}
</div>

<style>
  .make-route {
    display: flex;
    flex-direction: column;
    place-items: center;
  }

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
  #test {
    width: 500px;
    height: 500px;
    position: relative;
  }
  #test img {
    position: absolute;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
</style>

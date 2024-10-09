<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import { writable } from 'svelte/store';
  import L, { Map as LeafletMap, Marker, Polyline } from 'leaflet';
  import type { LatLng, LatLngExpression, Control } from 'leaflet';
  import { replace, querystring } from 'svelte-spa-router';
  import Swal from 'sweetalert2';
  import { get } from 'svelte/store';

  import { MapToolbar, MapGUI, Timer } from '@/components/drawing';
  import {
    isLockScreen,
    isPause,
    elapsedTime,
    posList,
    updateDrawingInfo,
    setDrawingPos,
    updateDistanceAndSpeed,
    isRouteDrawing,
  } from '@/stores/drawingStore';
  import {
    setupMotionEventListeners,
    setupStepEventListeners,
    stopStepEventListeners,
    deviceOrientation,
    clearMotionEventListeners,
  } from '@/stores/deviceMotionStore';

  import DrawingPauseModal from '@/components/modals/DrawingPauseModal.svelte';
  import { userStore } from '@/stores/userStore';

  import { startDrawing } from '@/api/drawingApi';
  import { connectWebSocket, disconnectWebSocket, sendRealTimePosition } from '@/api/websocket';
  import { formatTimeToHMS } from '@/utils/formatter';

  $: $isLockScreen = $isLockScreen;
  $: $isPause = $isPause;
  $: $elapsedTime = $elapsedTime;
  $: $deviceOrientation, updateMarkerRotation();

  let countdown: number | null;

  let currPos: LatLng | null = null;
  let map: LeafletMap | null;
  let time: Date | null = null; // 현재시간
  let timerIntervalId: number | null = null; // 1초마다 타이머 업데이트하는 setInterval의 ID
  let trackingIntervalId: number | null = null; // 10초마다 위치를 추적하는 setInterval의 ID
  let startPos: LatLng | null = null;
  let startTime: string | null = null;
  let firstLocationFound = writable(false); // 최초 위치 찾기 상태
  let isFocusMarker: boolean = true;
  let zoomLevel: number = 16;
  let routeId = writable(null);
  let drawingId = writable(null);
  let options = writable({});
  $: if ($querystring) {
    const params = new URLSearchParams($querystring);
    const newRouteId = params.get('routeId');
    const newDrawingId = params.get('drawingId');
    if (newRouteId) {
      routeId.set(newRouteId);
      console.log('루트 드로잉임!!!');
      isRouteDrawing.set(true);
    }
    if (newDrawingId) {
      drawingId.set(newDrawingId);
      console.log('루트 드로잉임!!!');
      isRouteDrawing.set(true);
    }

    // 옵션 객체 업데이트
    options.set({
      ...(newDrawingId ? { drawingId: newDrawingId } : {}),
      ...(newRouteId ? { routeId: newRouteId } : {}),
    });
  }

  function createMap(): LeafletMap {
    const m = L.map('map', { preferCanvas: true });
    m.locate({ setView: true, maxZoom: zoomLevel });

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      // attribution: `&copy;<a href="https://www.openstreetmap.org/copyright" target="_blank">OpenStreetMap</a>,
      // &copy;<a href="https://carto.com/attributions" target="_blank">CARTO</a>`,
      subdomains: 'abcd',
    }).addTo(m);
    setupMotionEventListeners();
    return m;
  }

  function handleLocationFound(e: L.LocationEvent) {
    const { lat, lng } = e.latlng;
    currPos = new L.LatLng(lat, lng);

    addPosition(lat, lng);

    if (currPos && isFocusMarker) map.setView(currPos, zoomLevel, { animate: true });

    firstLocationFound.update((value) => {
      if (!value) {
        // 처음 위치 찾기
        console.log('처음 location found');
        firstLocationFound.set(true);
        startPos = new L.LatLng(lat, lng);
        const data = { lat: lat, lng: lng, time: startTime };
        // console.log(data, get(options));
        startDrawing(
          get(options),
          data,
          async (response) => {
            const responseData = response.data.data;
            const updateData = {
              ...responseData,
              routeId: get(routeId),
            };
            if (!responseData.drawingId) {
              updateData.drawingId = get(drawingId);
            }
            updateDrawingInfo(updateData); // 스토어를 업데이트

            // console.log('api 연결 후 드로잉 데이터:', get(drawingStore));

            if (routeLineLayers) {
              routeLineLayers.remove();
            }
            if (response.data.data.routePositionList) {
              routeLineLayers = createRouteLines(response.data.data.routePositionList);
              routeLineLayers.addTo(map);
            }
            try {
              await connectWebSocket();
              // console.log('WebSocket connected successfully');
            } catch (error) {
              console.error('Failed to connect WebSocket:', error);
            }
          },
          (error) => {
            console.error('Failed to start drawing:', error);
          }
        );
      } else {
        // 주기적 위치 업데이트
        // console.log('주기적 found');

        if (!$isPause) {
          // console.log('소켓 데이터 보내기!');
          const currentUser = get(userStore);
          const nickname = currentUser ? currentUser.nickname : '';
          sendRealTimePosition({ lat, lng }, nickname);
          updateDistanceAndSpeed();
        }
        setDrawingPos({ lat, lng, time: formatTimeToHMS() });
        if (map !== null) {
          // 마커가 이미 존재하면 업데이트하고, 없으면 새로 추가
          if (!marker) {
            marker = createMarker(currPos); // 새 마커 생성
          } else {
            marker.setLatLng(currPos); // 기존 마커 위치 업데이트
          }
          if (lineLayers) {
            lineLayers?.addLatLng(currPos);
          }
        }
      }
      return true;
    });
  }

  let toolbar = new L.Control({ position: 'topright' });
  let toolbarComponent: MapToolbar | null = null;
  toolbar.onAdd = (map: LeafletMap) => {
    const div = L.DomUtil.create('div');
    toolbarComponent = new MapToolbar({
      target: div,
      props: {},
    });

    toolbarComponent.$on('click-reset', () => {
      if (currPos) map.setView(currPos, zoomLevel, { animate: true });
      isFocusMarker = true;
    });

    return div;
  };

  toolbar.onRemove = () => {
    if (toolbarComponent) {
      toolbarComponent.$destroy();
      toolbarComponent = null;
    }
  };

  L.Map.include({
    _initControlPos: function () {
      var corners = (this._controlCorners = {}),
        l = 'leaflet-',
        container = (this._controlContainer = L.DomUtil.create(
          'div',
          l + 'control-container',
          this._container
        ));

      function createCorner(vSide, hSide) {
        var className = l + vSide + ' ' + l + hSide;

        corners[vSide + hSide] = L.DomUtil.create('div', className, container);
      }

      createCorner('top', 'left');
      createCorner('top', 'right');
      createCorner('bottom', 'left');
      createCorner('bottom', 'right');

      createCorner('top', 'center');
      createCorner('middle', 'center');
      createCorner('middle', 'left');
      createCorner('middle', 'right');
      createCorner('bottom', 'center');
    },
  });
  let gui = new L.Control({ position: 'bottomcenter' });
  let guiComponent: MapGUI | null = null;
  gui.onAdd = (map: LeafletMap) => {
    const div = L.DomUtil.create('div');
    guiComponent = new MapGUI({
      target: div,
      props: { elapsedTime },
    });

    guiComponent.$on('click-pause', () => {
      isPause.update((value) => !value);
      toggleTracking();
      let currentModalType: string = 'pause'; // 초기 modalType 설정

      const renderModal = (modalType: string) => {
        // SweetAlert 모달 생성
        Swal.fire({
          html: '<div id="drawing-modal-container"></div>', // Svelte 컴포넌트가 삽입될 DOM
          showConfirmButton: false, // 기본 SweetAlert 버튼 비활성화
          allowOutsideClick: false,
          willOpen: () => {
            // SweetAlert 모달이 열릴 때 Svelte 컴포넌트를 해당 DOM에 렌더링
            const modalContainer = document.getElementById('drawing-modal-container');
            if (modalContainer) {
              const modalInstance = new DrawingPauseModal({
                target: modalContainer,
                props: {
                  modalType, // 동적으로 modalType 전달
                },
              });

              // confirm 이벤트 처리
              modalInstance.$on('confirm', (event) => {
                const { modalType: nextModalType } = event.detail;
                // modalType에 따라 새로운 모달 렌더링
                renderModal(nextModalType);
              });

              modalInstance.$on('cancel', (event) => {
                renderModal('pause');
              });

              // complete 이벤트 처리
              modalInstance.$on('complete', () => {
                Swal.close();
                replace('/drawingcapture?mode=complete');
              });

              // save 이벤트 처리
              modalInstance.$on('save', () => {
                console.log('Temporary save');
                Swal.close();
                replace('/drawingcapture?mode=save');
              });

              // continue 이벤트 처리
              modalInstance.$on('continue', () => {
                //TODO - 3초 보여주기
                countdown = 3;
                Swal.close();
                setTimeout(() => {
                  isPause.update((value) => !value);
                  console.log('Continuing drawing');
                }, 3000);
              });
            }
          },
        });
      };
      renderModal(currentModalType);
    });

    guiComponent.$on('click-lockScreen', () => {
      isLockScreen.update((value) => !value); // store 값을 업데이트
    });

    return div;
  };
  gui.onRemove = () => {
    if (guiComponent) {
      guiComponent.$destroy();
      guiComponent = null;
    }
  };

  let marker: Marker | null = null; // 마커 객체를 저장할 변수

  function createMarker(loc: LatLngExpression): Marker | null {
    const iconHtml = `<div class="map-marker" style="transform: rotate(${$deviceOrientation.alpha}}deg);">
<div class="map-marker"><svg version="1.0" xmlns="http://www.w3.org/2000/svg"
 width="2rem" height="2rem" viewBox="0 0 256.000000 256.000000"
 preserveAspectRatio="xMidYMid meet">

<g transform="translate(0.000000,256.000000) scale(0.100000,-0.100000)"
fill="#000000" stroke="none">
<path d="M1245 1839 c-484 -209 -886 -387 -892 -396 -19 -25 -16 -77 5 -97 12
-10 158 -48 386 -101 202 -47 376 -89 386 -95 10 -5 21 -17 25 -27 3 -10 44
-182 91 -382 52 -226 90 -371 100 -383 20 -21 72 -24 97 -6 22 17 777 1775
777 1809 0 34 -29 59 -66 58 -16 -1 -425 -172 -909 -380z"/>
</g>
</svg>
</div>
    </div>`;
    const icon = new L.DivIcon({
      html: iconHtml,
      className: 'map-marker',
    });
    marker = new L.Marker(loc, { icon });

    if (map) {
      marker.addTo(map);
      return marker;
    }
    return null;
  }
  function updateMarkerRotation() {
    if (marker && marker.getIcon) {
      const icon = marker.getIcon();
      icon.options.html = `<div class="map-marker" style="transform: rotate(${$deviceOrientation.alpha}deg);">
<div class="map-marker"><svg version="1.0" xmlns="http://www.w3.org/2000/svg"
 width="2rem" height="2rem" viewBox="0 0 256.000000 256.000000"
 preserveAspectRatio="xMidYMid meet">

<g transform="translate(0.000000,256.000000) scale(0.100000,-0.100000)"
fill="#000000" stroke="none">
<path d="M1245 1839 c-484 -209 -886 -387 -892 -396 -19 -25 -16 -77 5 -97 12
-10 158 -48 386 -101 202 -47 376 -89 386 -95 10 -5 21 -17 25 -27 3 -10 44
-182 91 -382 52 -226 90 -371 100 -383 20 -21 72 -24 97 -6 22 17 777 1775
777 1809 0 34 -29 59 -66 58 -16 -1 -425 -172 -909 -380z"/>
</g>
</svg>
</div>
    </div>`;
      marker.setIcon(icon);
    }
  }

  function createLines(): Polyline {
    return L.polyline(
      $posList.map((p) => p.latlng),
      { color: '#3FAE48', opacity: 0.5, smoothFactor: 1 }
    );
  }

  let lineLayers: Polyline;
  let routeLineLayers: Polyline;

  function createRouteLines(positions) {
    const latlngs = positions.map((pos) => new L.LatLng(pos.lat, pos.lng));
    console.log(latlngs);
    return L.polyline(latlngs, {
      color: 'gray',
      weight: 5,
      opacity: 0.7,
      dashArray: '10, 20',
    });
  }
  function mapAction() {
    map = createMap();
    toolbar.addTo(map);
    gui.addTo(map);
    lineLayers = createLines();
    lineLayers.addTo(map);

    map.on('dblclick', (e) => {
      console.log('dblclick map');
      isFocusMarker = true;
    });
    map.on('drag', (e) => {
      console.log('drag map');
      isFocusMarker = false;
    });
    map.on('zoom', (e) => {
      zoomLevel = e.target._zoom;
    });
    return {
      destroy: () => {
        if (map) {
          map.remove();
          gui.remove();
          map = null;
        }
        clearMotionEventListeners();
      },
    };
  }

  function resizeMap() {
    if (map) {
      map.invalidateSize();
    }
  }
  function addPosition(lat: number, lng: number) {
    const newPos = {
      latlng: new L.LatLng(lat, lng), // LatLng 객체로 저장
      time: Date.now(), // 현재 시간을 timestamp로 저장
    };

    // posList에 새로운 좌표 추가
    posList.update((list) => [...list, newPos]);
  }
  async function toggleTracking() {
    const trackingActive = get(isPause);
    map?.locate({ setView: true, maxZoom: zoomLevel });
    map?.on('locationfound', handleLocationFound);

    map?.on('locationerror', (e: L.ErrorEvent) => {
      console.error(e.message);
    });
    if (!trackingActive) {
      console.log('트래킹 시작');
      setupStepEventListeners();

      if (!startTime) {
        startTime = formatTimeToHMS();
      }

      timerIntervalId = window.setInterval(() => {
        elapsedTime.update((prev) => prev + 1);
        console.log(get(elapsedTime) + '초');
      }, 1000);

      trackingIntervalId = window.setInterval(() => {
        map?.locate();
      }, 2000);
    } else {
      console.log('일시정지');
      stopStepEventListeners();

      if (timerIntervalId) {
        console.log('시간 타이머 멈추기 :' + timerIntervalId);
        clearInterval(timerIntervalId);
        timerIntervalId = null;
      }

      if (trackingIntervalId) {
        console.log('10초 위치 트래킹 멈추기 :' + trackingIntervalId);
        clearInterval(trackingIntervalId);
        trackingIntervalId = null;
      }
    }
  }

  function clickLockScreen() {
    $isLockScreen = !$isLockScreen;
  }
  function handleTimerComplete() {
    countdown = null;
    !map && mapAction();
    console.log('타이머 끝 ');
    toggleTracking();
  }

  onMount(() => {
    userStore.initialize(); // 스토어에서 사용자 정보 초기화
    // resetDrawingStore();
    countdown = 3;
  });
  onDestroy(() => {
    if (map) {
      map.remove();
      clearInterval(timerIntervalId);
      clearInterval(trackingIntervalId);
      clearMotionEventListeners();
    }
    disconnectWebSocket();
  });
</script>

<svelte:window on:resize={resizeMap} />

{#if countdown}
  <Timer on:clearTimer={handleTimerComplete} {countdown} />
{/if}

<div class="map-container" style="height:100vh; width:100%;">
  <div id="map" class="map" style="height:100%;width:100%;" />
</div>
<div class="lock-overlay" class:is-active={$isLockScreen}>
  <div class="overlay-content">
    <button on:click={clickLockScreen} class="unlock-btn"
      ><svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 24 24" {...$$props}>
        <path
          fill="none"
          stroke="#749b59"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="1.85"
          d="M17.893 21.988c-.052-1.913.077-2.143.214-2.569s1.093-1.96 1.431-3.057c1.095-3.548.075-4.303-1.285-5.309c-1.509-1.115-4.354-1.68-5.765-1.558V3.744a1.744 1.744 0 0 0-3.489 0v10.26l-2.06-2.18c-.64-.694-1.668-.764-2.37-.133a1.72 1.72 0 0 0-.222 2.318l1.292 1.713M8.869 22l-.019-1.05c.043-1.232-.853-2.035-2.021-3.641l-.246-.336m0 0a72 72 0 0 0-.944-1.251m.944 1.25l1.17 1.55m-1.17-1.55l-.944-1.25m0 0c-.572-.742-1.003-1.281-1.375-1.816"
          color="#749b59"
        />
      </svg>
    </button>
  </div>
</div>

<style>
  .map-container {
    overflow: visible; /* 콘텐츠가 넘치더라도 보이게 함 */
  }
  .lock-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5);
    display: none;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    pointer-events: none;
  }

  .lock-overlay.is-active {
    display: flex;
    pointer-events: auto;
  }

  .unlock-btn {
    bottom: 19vh;
    left: 58vw;
    color: white;
    border: none;
    font-size: 1rem;
    cursor: pointer;
    border-radius: 55px;
    background: rgba(255, 255, 255);
    padding: 0.8rem;
    text-align: center;
    position: absolute;
    display: flex;
    align-items: center;
    align-content: center;
    justify-content: center;
    flex-wrap: wrap;
    border-radius: 55px;
  }
  .map-marker {
    transition: transform 0.3s ease-in-out; /* 부드러운 회전 효과 */
  }
</style>

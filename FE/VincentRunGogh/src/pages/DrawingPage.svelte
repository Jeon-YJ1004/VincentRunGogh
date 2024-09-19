<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import L, { Map as LeafletMap, Marker, Polyline } from 'leaflet';
  import type { LatLng, LatLngExpression, Control } from 'leaflet';
  import { push, pop, replace } from 'svelte-spa-router';

  import Swal from 'sweetalert2';
  import { get } from 'svelte/store';

  import { MapToolbar, MapGUI } from '@/components/drawing';
  import { isLockScreen, isPause, elapsedTime, posList } from '@/stores/drawingStore';
  import DrawingPauseModal from '@/components/modal/DrawingPauseModal.svelte';
  import Timer from '@/components/drawing/Timer.svelte';
  import { updateDistanceAndSpeed } from '@/utils/calculateFuc';

  $: $isLockScreen = $isLockScreen;
  $: $isPause = $isPause;
  $: $elapsedTime = $elapsedTime;

  let countdown: number | null;

  let currPos: LatLng | null = null;
  let map: LeafletMap | null;
  let time: Date | null = null; // 현재시간
  let timerIntervalId: number | null = null; // 1초마다 타이머 업데이트하는 setInterval의 ID
  let trackingIntervalId: number | null = null; // 10초마다 위치를 추적하는 setInterval의 ID
  let startPos: LatLng | null = null;
  let startTime: Date | null = null;

  //STUB - 임시 좌표 없애기
  // const posList: LatLngExpression[] = [
  //   [36.3528192, 127.3102336],
  //   [36.35, 127.31],
  //   [36.36, 127.3102336],
  //   [36.37, 127.3102336],
  // ];

  function createMap(): LeafletMap {
    const m = L.map('map', { preferCanvas: true });
    m.locate({ setView: true, maxZoom: 16 });

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
      // attribution: `&copy;<a href="https://www.openstreetmap.org/copyright" target="_blank">OpenStreetMap</a>,
      // &copy;<a href="https://carto.com/attributions" target="_blank">CARTO</a>`,
      subdomains: 'abcd',
    }).addTo(m);

    return m;
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
      if (currPos) map.setView(currPos, 16, { animate: true });
    });

    return div;
  };

  toolbar.onRemove = () => {
    if (toolbarComponent) {
      toolbarComponent.$destroy();
      toolbarComponent = null;
    }
  };

  let gui = new L.Control({ position: 'bottomleft' });
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
                //TODO - api 연결
                Swal.close(); // SweetAlert 모달을 닫음
                replace('/drawingcapture?mode=complete');
              });

              // save 이벤트 처리
              modalInstance.$on('save', () => {
                //TODO - api 연결
                console.log('Temporary save');
                Swal.close(); // SweetAlert 모달을 닫음
                replace('/drawingcapture?mode=save');
              });

              // continue 이벤트 처리
              modalInstance.$on('continue', () => {
                //TODO - 3초 보여주기
                // countdown = 3;
                Swal.close(); // SweetAlert 모달을 닫음
                setTimeout(() => {
                  isPause.update((value) => !value);
                  toggleTracking();
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

  let marker = new Map<string, Marker>();

  function markerIcon(): L.DivIcon {
    const html = `<div class="map-marker"><svg version="1.0" xmlns="http://www.w3.org/2000/svg"
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
</div>`;
    return L.divIcon({
      html,
      className: 'map-marker',
    });
  }

  function createMarker(loc: LatLngExpression): Marker {
    const icon = markerIcon();
    if (map) return L.marker(loc, { icon }).addTo(map); // 마커를 지도에 추가
    return L.marker(loc, { icon });
  }

  function createLines(): Polyline {
    return L.polyline($posList, { color: '#E4E', opacity: 0.5, smoothFactor: 1 });
  }

  let lineLayers: Polyline;

  function mapAction() {
    map = createMap();
    toolbar.addTo(map);
    gui.addTo(map);
    lineLayers = createLines();
    lineLayers.addTo(map);

    return {
      destroy: () => {
        if (map) {
          map.remove();
          gui.remove();
          map = null;
        }
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
  function toggleTracking() {
    const trackingActive = get(isPause);
    if (!trackingActive) {
      map?.locate({ setView: true, maxZoom: 18 });

      map?.on('locationfound', (e: L.LocationEvent) => {
        const { lat, lng } = e.latlng;
        currPos = new L.LatLng(lat, lng);

        if (!startPos) {
          startPos = new L.LatLng(lat, lng);
        }
        addPosition(lat, lng);

        if (map !== null) {
          // 마커가 이미 존재하면 업데이트하고, 없으면 새로 추가
          if (marker.has('current')) {
            const existingMarker = marker.get('current');
            existingMarker?.setLatLng(currPos); // 기존 마커 위치 업데이트
          } else {
            const newMarker = createMarker(currPos); // 새 마커 생성
            marker.set('current', newMarker); // 새 마커를 저장
          }
          if (lineLayers) {
            lineLayers?.addLatLng(currPos);
          }
          updateDistanceAndSpeed($posList);
        }
      });

      map?.on('locationerror', (e: L.ErrorEvent) => {
        console.error(e.message);
      });

      if (!startTime) {
        startTime = new Date();
      }

      timerIntervalId = window.setInterval(() => {
        elapsedTime.update((prev) => prev + 1);
        console.log(get(elapsedTime) + '초');
      }, 1000);

      trackingIntervalId = window.setInterval(() => {
        map?.locate();
      }, 10000);
    } else {
      console.log('일시정지');

      if (timerIntervalId) {
        clearInterval(timerIntervalId);
        timerIntervalId = null;
      }

      if (trackingIntervalId) {
        clearInterval(trackingIntervalId);
        trackingIntervalId = null;
      }
    }
  }

  function clickLockScreen() {
    $isLockScreen = !$isLockScreen;
  }

  onMount(() => {
    countdown = 3;
    //TODO - 루트의 좌표 값들을 url의 routeId 파람으로 조회
  });
  onDestroy(() => {
    if (map) {
      map.remove();
      // clearInterval(timerIntervalId);
      // clearInterval(trackingIntervalId);
    }
  });
</script>

<svelte:window on:resize={resizeMap} />

{#if countdown}
  <Timer
    on:clearTimer={() => {
      countdown = null;
      !map && mapAction();
      toggleTracking();
    }}
    {countdown}
  />
{/if}

<div class="map-container" style="height:100vh; width:100%;">
  <div id="map" class="map" style="height:100%;width:100%;" />
</div>
<div class="lock-overlay" class:is-active={$isLockScreen}>
  <div class="overlay-content">
    <button on:click={clickLockScreen} class="unlock-btn"> </button>
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

  .overlay-content {
    background: rgba(255, 255, 255, 0.9);
    padding: 1rem 2rem;
    border-radius: 0.5rem;
    text-align: center;
  }

  .unlock-btn {
    background-color: purple;
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    font-size: 1rem;
    cursor: pointer;
    border-radius: 0.25rem;
    margin-top: 1rem;
  }
</style>

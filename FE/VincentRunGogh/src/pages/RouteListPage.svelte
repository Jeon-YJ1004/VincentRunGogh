<script lang="ts">
  import { getRouteList } from '@/api/routeApi';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import Header from '@/components/common/Header.svelte';
  import RouteDetail from '@/components/modals/RouteDetail.svelte';
  import { formatSecToHMS } from '@/utils/formatter';
  import { Tabs, TabItem, Card, Button } from 'flowbite-svelte';
  import {
    PaletteOutline,
    HeartSolid,
    MapPinSolid,
    ChevronDoubleUpOutline,
  } from 'flowbite-svelte-icons';
  import { onMount } from 'svelte';
  import Swal from 'sweetalert2';

  let routeList: {
    routeId: string;
    title: string;
    artImage: string;
    distance: number;
    predictTime: number;
    distanceFromUser: number;
  }[] = [];

  // 루트 클릭 시 모달
  function showRouteDetail(route: object) {
    Swal.fire({
      html: '<div id="route-detail"></div>',
      showConfirmButton: false,
      didOpen: () => {
        // 'route-detail'라는 ID를 가진 div에 Svelte 컴포넌트 렌더링
        new RouteDetail({
          target: document.getElementById('route-detail'),
          props: {
            route,
            onClose: () => {
              Swal.close(); // 모달 닫기
            },
            routeId: route.routeId,
            type: 'routeList',
          },
        });
      },
    });
  }

  // 검색범위 설정
  let range: number = 5;

  function searchCondition() {
    Swal.fire({
      title: "<div class='text-lg'>" + '검색 범위를 조절해주세요!' + '</div>',
      html: `
    <input type="range" min="1" max="10" step="1" class="w-full h-2 bg-gray-200 rounded-lg cursor-pointer" id="range-input" value="${range}">
    <div id="range-value">반경 ${range}km 까지 검색합니다.</div>
  `,
      showCancelButton: true,
      confirmButtonText: '확인',
      confirmButtonColor: '#FFB800',
      cancelButtonText: '취소',
      preConfirm: () => {
        return range; // 범위 값을 반환
      },
      didOpen: () => {
        const input = document.getElementById('range-input') as HTMLInputElement;
        const rangeValue = document.getElementById('range-value');

        // 범위 값 업데이트 이벤트 리스너
        input.addEventListener('input', () => {
          range = Number(input.value); // 범위 값 업데이트
          if (rangeValue) {
            rangeValue.innerText = `반경 ${range}km 까지 검색합니다.`; // 문구 업데이트
          }
        });
      },
    }).then((result) => {
      if (result.isConfirmed) {
        console.log(`선택된 범위: ${range}`); // 확인 시 범위 값 출력
        // 여기서 range 변수를 사용하여 추가 로직을 구현
      }
    });
  }

  // 내 위치 가져오기
  let currentLat: number = 0;
  let currentLng: number = 0;

  function handlePosition(position: GeolocationPosition) {
    currentLat = position.coords.latitude;
    currentLng = position.coords.longitude;
    getMyRoute();
  }

  function failPosition() {
    console.log('geolocation api error');
  }

  const positionOption = {
    enableHighAccuracy: true,
    timeout: 5000, // 5 seconds
    maximumAge: 60000, // 1 minute
  };

  onMount(async () => {
    await window.navigator.geolocation.getCurrentPosition(
      handlePosition,
      failPosition,
      positionOption
    );
  });

  // 탭별 api 요청 로직
  let routeListParams: {
    type: string;
    lat: number;
    lng: number;
  } = {
    type: '',
    lat: 0,
    lng: 0,
  };

  async function getMyRoute() {
    routeListParams = {
      type: 'mine',
      lat: currentLat,
      lng: currentLng,
    };
    let routeListResponse = await getRouteList(routeListParams);
    routeList = routeListResponse.data.routeList;
    console.log('Position :', currentLat, ',', currentLng);
  }

  async function getOtherRoute() {
    routeListParams = {
      type: 'others',
      lat: currentLat,
      lng: currentLng,
    };
    let routeListResponse = await getRouteList(routeListParams);
    routeList = routeListResponse.data.routeList;
    console.log('Position :', currentLat, ',', currentLng);
  }

  async function getLikedRoute() {
    routeListParams = {
      type: 'like',
      lat: currentLat,
      lng: currentLng,
    };
    let routeListResponse = await getRouteList(routeListParams);
    routeList = routeListResponse.data.routeList;
    console.log('Position :', currentLat, ',', currentLng);
  }

  //맨 위로 스크롤
  function scrollToTop(target: string) {
    let targetDiv = document.querySelector(target);
    targetDiv.scrollTo({
      top: 0,
      behavior: 'smooth', // 부드럽게 스크롤
    });
  }
</script>

<Header title="루트 조회" />

<div id="routelist-body">
  <div id="search-control">
    <Button size="sm" on:click={searchCondition}>검색 반경 설정</Button>
    <p class="my-3 font-bold">반경 {range}km내의 루트만 표시됩니다.</p>
  </div>
  <Tabs id="tabs" defaultClass="flex justify-between p-0" tabStyle="underline">
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        on:click={getMyRoute}
        open
      >
        <div slot="title" class="flex items-center gap-1">
          <PaletteOutline size="sm" />
          <p>만든 루트</p>
        </div>
        <div id="routelist-content" class="space-y-4" on:touchmove>
          {#if routeList.length === 0}
            <h1>주변 {range}km 내에 루트가 없습니다!</h1>
          {:else}
            {#each routeList as route}
              {#if route.distanceFromUser <= range}
                <div class="h-[14vh] mb-3 relative z-10">
                  <Card
                    on:click={() => showRouteDetail(route)}
                    horizontal
                    class="w-full p-0 flex"
                    size="sm"
                  >
                    <div class="flex p-1">
                      <img src={route.artImage} class="h-[100%] w-[30%]" alt="" />
                      <div class="flex flex-col justify-around ms-3 mt-2">
                        <p
                          class="mb-1 font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                        >
                          나와의 거리 {route.distanceFromUser}km
                        </p>
                        <h5
                          class="mb-1 text-xl font-bold tracking-tight text-gray-900 dark:text-white"
                        >
                          {route.title}
                        </h5>
                        <div class="flex justify-between">
                          <p
                            class="me-3 font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                          >
                            <span class="text-xs">총 길이</span>
                            {route.distance}km
                          </p>
                          <p
                            class="font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                          >
                            <span class="text-xs">예상 소요 시간</span>
                            {formatSecToHMS(route.predictTime)}
                          </p>
                        </div>
                      </div>
                    </div>
                  </Card>
                </div>
              {/if}
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        on:click={getLikedRoute}
      >
        <div slot="title" class="flex items-center gap-1">
          <HeartSolid size="sm" />
          찜한 루트
        </div>
        <div id="routelist-content" class="space-y-4" on:touchmove>
          {#if routeList.length === 0}
            <h1>찜한 루트가 아직 없습니다!</h1>
          {:else}
            {#each routeList as route}
              {#if route.distanceFromUser <= range}
                <div class="h-[14vh] mb-3 relative z-10">
                  <Card
                    on:click={() => showRouteDetail(route)}
                    horizontal
                    class="w-full p-0 flex"
                    size="sm"
                  >
                    <div class="flex p-1">
                      <img src={route.artImage} class="h-[100%] w-[30%]" alt="" />
                      <div class="flex flex-col justify-around ms-3 mt-2">
                        <p
                          class="mb-1 font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                        >
                          나와의 거리 {route.distanceFromUser}km
                        </p>
                        <h5
                          class="mb-1 text-xl font-bold tracking-tight text-gray-900 dark:text-white"
                        >
                          {route.title}
                        </h5>
                        <div class="flex justify-between">
                          <p
                            class="me-3 font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                          >
                            <span class="text-xs">총 길이</span>
                            {route.distance}km
                          </p>
                          <p
                            class="font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                          >
                            <span class="text-xs">예상 소요 시간</span>
                            {formatSecToHMS(route.predictTime)}
                          </p>
                        </div>
                      </div>
                    </div>
                  </Card>
                </div>
              {/if}
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        on:click={getOtherRoute}
      >
        <div slot="title" class="flex items-center gap-1">
          <MapPinSolid size="sm" />
          주변 루트
        </div>
        <div id="routelist-content" class="space-y-4" on:touchmove>
          {#if routeList.length === 0}
            <h1>주변 {range}km 내에 루트가 없습니다!</h1>
          {:else}
            {#each routeList as route}
              {#if route.distanceFromUser <= range}
                <div class="h-[14vh] mb-3 relative z-10">
                  <Card
                    on:click={() => showRouteDetail(route)}
                    horizontal
                    class="w-full p-0 flex"
                    size="sm"
                  >
                    <div class="flex p-1">
                      <img src={route.artImage} class="h-[100%] w-[30%]" alt="" />
                      <div class="flex flex-col justify-around ms-3 mt-2">
                        <p
                          class="mb-1 font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                        >
                          나와의 거리 {route.distanceFromUser}km
                        </p>
                        <h5
                          class="mb-1 text-xl font-bold tracking-tight text-gray-900 dark:text-white"
                        >
                          {route.title}
                        </h5>
                        <div class="flex justify-between">
                          <p
                            class="me-3 font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                          >
                            <span class="text-xs">총 길이</span>
                            {route.distance}km
                          </p>
                          <p
                            class="font-normal text-gray-700 dark:text-gray-400 leading-tight text-sm"
                          >
                            <span class="text-xs">예상 소요 시간</span>
                            {formatSecToHMS(route.predictTime)}
                          </p>
                        </div>
                      </div>
                    </div>
                  </Card>
                </div>
              {/if}
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
  </Tabs>
</div>
<div class="absolute bottom-3 right-3 z-20">
  <Button on:click={() => scrollToTop('#routelist-body')} size="sm">
    <ChevronDoubleUpOutline />
  </Button>
</div>
<div id="background">
  <svg width="200" height="100" xmlns="http://www.w3.org/2000/svg" style="overflow: visible;">
    <ellipse cx="100" cy="50" rx="220" ry="330" fill="url(#grad1)" filter="url(#blur-filter)" />
    <defs>
      <filter id="blur-filter" x="-50%" y="-50%" width="1000%" height="1000%">
        <feGaussianBlur stdDeviation="40" />
      </filter>
      <linearGradient id="grad1" x1="0%" y1="0%" x2="80%" y2="70%">
        <stop offset="0%" style="stop-color:rgb(255,184,0);stop-opacity:1" />
        <stop offset="100%" style="stop-color:rgb(255,218,115);stop-opacity:1" />
      </linearGradient>
    </defs>
  </svg>
</div>
<div id="background2">
  <svg width="200" height="100" xmlns="http://www.w3.org/2000/svg" style="overflow: visible;">
    <ellipse cx="100" cy="50" rx="220" ry="330" fill="url(#grad2)" filter="url(#blur-filter)" />
    <defs>
      <filter id="blur-filter" x="-50%" y="-50%" width="1000%" height="1000%">
        <feGaussianBlur stdDeviation="40" />
      </filter>
      <linearGradient id="grad2" x1="0%" y1="0%" x2="80%" y2="70%">
        <stop offset="0%" style="stop-color:rgb(94,131,88);stop-opacity:1" />
        <stop offset="100%" style="stop-color:rgb(154,186,149);stop-opacity:1" />
      </linearGradient>
    </defs>
  </svg>
</div>

<style>
  #search-control {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 10vh;
    z-index: 2;
  }

  #routelist-content {
    height: auto;
    flex-grow: 1;
    overflow-y: scroll;
    z-index: 2;
  }

  #routelist-body {
    width: 100%;
    height: 90%;
    padding-bottom: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    overflow-y: scroll;
    z-index: 2;
  }

  #routelist-body::-webkit-scrollbar {
    display: none;
  }

  #tab-item {
    width: 35%;
    z-index: 2 !important;
  }

  #background {
    position: fixed;
    bottom: 2%;
    left: 25%;
    transform: translateX(-50%);
    z-index: 1;
    opacity: 0.3;
    overflow: visible;
    transition: 800ms;
  }

  #background2 {
    position: fixed;
    top: 9%;
    right: -50%;
    transform: translateX(-50%);
    z-index: 1;
    opacity: 0.3;
    overflow: visible;
    transition: 800ms;
  }
</style>

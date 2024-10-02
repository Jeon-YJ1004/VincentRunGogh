<script lang="ts">
  import { getRouteList } from '@/api/routeApi';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import RouteDetail from '@/components/modals/RouteDetail.svelte';
  import { formatSecToMMSS } from '@/utils/formatter';
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
      title: '검색 범위를 조절해주세요!',
      html: `
    <input type="range" min="1" max="10" step="1" class="w-full h-2 bg-gray-200 rounded-lg cursor-pointer" id="range-input" value="${range}">
    <div id="range-value">반경 ${range}km 까지 검색합니다.</div>
  `,
      showCancelButton: true,
      confirmButtonText: '확인',
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

<div id="routelist-header" class="flex justify-center items-center">
  <BackButton />
  <h2>루트 조회</h2>
</div>
<div id="search-control">
  <Button size="sm" on:click={searchCondition}>검색 반경 설정</Button>
  <p class="my-3 font-bold">반경 {range}km내의 루트만 표시됩니다.</p>
</div>
<div id="routelist-body">
  <Tabs id="tabs" defaultClass="flex justify-between p-0" tabStyle="underline">
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2"
        divClass="flex flex-col justify-center p-0 w-80"
        on:click={getMyRoute}
        open
      >
        <div slot="title" class="flex items-center gap-1">
          <PaletteOutline size="sm" />
          <p>만든 루트</p>
        </div>
        <div id="routelist-content" class="space-y-4" on:touchmove>
          {#each routeList as route}
            {#if route.distanceFromUser <= range * 1000}
              <div class="h-[14vh] mb-3">
                <Card
                  on:click={() => showRouteDetail(route)}
                  horizontal
                  class="w-full p-0 flex"
                  size="sm"
                >
                  <div class="flex p-1">
                    <img src={route.artImage} class="h-[100%] w-[30%]" alt="" />
                    <div class="ms-3 mt-2">
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
                          {formatSecToMMSS(route.predictTime)}
                        </p>
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
            {/if}
          {/each}
        </div>
      </TabItem>
    </div>
    <div id="tab-item">
      <TabItem defaultClass="tab-item font-bold text-xs gap-2" on:click={getLikedRoute}>
        <div slot="title" class="flex items-center gap-1">
          <HeartSolid size="sm" />
          찜한 루트
        </div>
        <div id="routelist-content" class="space-y-4" on:touchmove>
          {#each routeList as route}
            {#if route.distanceFromUser <= range * 1000}
              <div class="h-[14vh] mb-3">
                <Card
                  on:click={() => showRouteDetail(route)}
                  horizontal
                  class="w-full p-0 flex"
                  size="sm"
                >
                  <div class="flex p-1">
                    <img src={route.artImage} class="h-[100%] w-[30%]" alt="" />
                    <div class="ms-3 mt-2">
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
                          {formatSecToMMSS(route.predictTime)}
                        </p>
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
            {/if}
          {/each}
        </div>
      </TabItem>
    </div>
    <div id="tab-item">
      <TabItem defaultClass="tab-item font-bold text-xs gap-2" on:click={getOtherRoute}>
        <div slot="title" class="flex items-center gap-1">
          <MapPinSolid size="sm" />
          주변 루트
        </div>
        <div id="routelist-content" class="space-y-4" on:touchmove>
          {#each routeList as route}
            {#if route.distanceFromUser <= range * 1000}
              <div class="h-[14vh] mb-3">
                <Card
                  on:click={() => showRouteDetail(route)}
                  horizontal
                  class="w-full p-0 flex"
                  size="sm"
                >
                  <div class="flex p-1">
                    <img src={route.artImage} class="h-[100%] w-[30%]" alt="" />
                    <div class="ms-3 mt-2">
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
                          {formatSecToMMSS(route.predictTime)}
                        </p>
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
            {/if}
          {/each}
        </div>
      </TabItem>
    </div>
  </Tabs>
</div>
<div class="absolute bottom-3 right-3">
  <Button on:click={() => scrollToTop('#routelist-body')} size="sm">
    <ChevronDoubleUpOutline />
  </Button>
</div>

<style>
  #routelist-header {
    height: 10vh;
    width: 100%;
  }

  #search-control {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 10vh;
  }

  #routelist-content {
    height: auto;
    flex-grow: 1;
    overflow-y: scroll;
  }

  #routelist-body {
    width: 100%;
    height: 80%;
    padding-bottom: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    overflow-y: scroll;
  }

  #routelist-body::-webkit-scrollbar {
    display: none;
  }

  #tab-item {
    width: 35%;
  }
</style>

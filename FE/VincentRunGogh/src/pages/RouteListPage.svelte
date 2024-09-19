<script lang="ts">
  import RouteDetail from '@/components/modals/RouteDetail.svelte';
  import { Tabs, TabItem, Card, Button } from 'flowbite-svelte';
  import { PaletteOutline, HeartSolid, MapPinSolid } from 'flowbite-svelte-icons';
  import Swal from 'sweetalert2';

  let dummyRouteList: { id: number; name: string; near: number; length: number; time: number }[] = [
    { id: 1, name: '내루트1', near: 1.2, length: 2.3, time: 1234 },
    { id: 2, name: '내루트2', near: 1.3, length: 3.4, time: 1345 },
    { id: 3, name: '내루트3', near: 1.4, length: 4.5, time: 1456 },
    { id: 4, name: '내루트4', near: 1.5, length: 5.6, time: 1567 },
  ];
  let dummyRouteList2: { id: number; name: string; near: number; length: number; time: number }[] =
    [
      { id: 5, name: '찜루트1', near: 1.2, length: 2.3, time: 1234 },
      { id: 6, name: '찜루트2', near: 1.3, length: 3.4, time: 1345 },
      { id: 7, name: '찜루트3', near: 1.4, length: 4.5, time: 1456 },
      { id: 8, name: '찜루트4', near: 1.5, length: 5.6, time: 1567 },
    ];
  let dummyRouteList3: { id: number; name: string; near: number; length: number; time: number }[] =
    [
      { id: 9, name: '루트1', near: 1.2, length: 2.3, time: 1234 },
      { id: 10, name: '루트2', near: 1.3, length: 3.4, time: 1345 },
      { id: 11, name: '루트3', near: 1.4, length: 4.5, time: 1456 },
      { id: 12, name: '루트4', near: 1.5, length: 5.6, time: 1567 },
    ];

  function showRouteDetail(route: object) {
    Swal.fire({
      html: '<div id="route-detail"></div>',
      showConfirmButton: false,
      didOpen: () => {
        // 'route-detail'라는 ID를 가진 div에 Svelte 컴포넌트 렌더링
        new RouteDetail({
          target: document.getElementById('route-detail'),
          props: {
            onClose: () => {
              Swal.close(); // 모달 닫기
            },
          },
        });
      },
    });
  }
</script>

<Tabs tabStyle="underline">
  <TabItem open>
    <div slot="title" class="flex items-center gap-2">
      <PaletteOutline size="md" />
      내가 만든 루트
    </div>
    <div class="space-y-4">
      {#each dummyRouteList as route}
        <Card img="/4.png" on:click={() => showRouteDetail(route)} horizontal size="sm">
          <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
            나와의 거리 {route.near}km
          </p>
          <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            {route.name}
          </h5>
          <div class="flex justify-around">
            <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
              <span class="text-sm">총 길이</span>
              {route.length}km
            </p>
            <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
              <span class="text-sm">예상 소요 시간</span>
              {route.time}
            </p>
          </div>
        </Card>
      {/each}
    </div>
  </TabItem>
  <TabItem>
    <div slot="title" class="flex items-center gap-2">
      <HeartSolid size="md" />
      찜한 루트
    </div>
    <div class="space-y-4">
      {#each dummyRouteList2 as route}
        <Card img="/4.png" on:click={clickRoute} horizontal size="sm">
          <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
            나와의 거리 {route.near}km
          </p>
          <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            {route.name}
          </h5>
          <div class="flex justify-around">
            <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
              <span class="text-sm">총 길이</span>
              {route.length}km
            </p>
            <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
              <span class="text-sm">예상 소요 시간</span>
              {route.time}
            </p>
          </div>
        </Card>
      {/each}
    </div>
  </TabItem>
  <TabItem>
    <div slot="title" class="flex items-center gap-2">
      <MapPinSolid size="md" />
      주변 루트
    </div>
    <div class="space-y-4">
      {#each dummyRouteList3 as route}
        <Card img="/4.png" on:click={clickRoute} horizontal size="sm">
          <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
            나와의 거리 {route.near}km
          </p>
          <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            {route.name}
          </h5>
          <div class="flex justify-around">
            <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
              <span class="text-sm">총 길이</span>
              {route.length}km
            </p>
            <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
              <span class="text-sm">예상 소요 시간</span>
              {route.time}
            </p>
          </div>
        </Card>
      {/each}
    </div>
  </TabItem>
</Tabs>

<style>
</style>

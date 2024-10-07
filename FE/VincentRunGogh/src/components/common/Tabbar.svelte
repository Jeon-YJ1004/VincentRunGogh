<script>
  import { link, push, replace } from 'svelte-spa-router';
  import { BottomNav, BottomNavItem, Tooltip } from 'flowbite-svelte';
  import { HomeSolid, GlobeSolid, PaletteOutline, UserCircleSolid } from 'flowbite-svelte-icons';
  import Swal from 'sweetalert2';
  import SelectDrawing from '@/components/modals/SelectDrawing.svelte';

  function startDrawing() {
    Swal.fire({
      title: "<div class='text-lg'>" + '어떤 드로잉을 시작하실건가요?' + '</div>',
      html: '<div id="start-drawing"></div>',
      showConfirmButton: false,
      didOpen: () => {
        // 'start-drawing'라는 ID를 가진 div에 Svelte 컴포넌트 렌더링
        new SelectDrawing({
          target: document.getElementById('start-drawing'),
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

<BottomNav
  position="absolute"
  navType="application"
  outerClass="w-80 border-gray-200 bg-opacity-80 border-gray-600"
  classInner="grid-cols-5"
>
  <BottomNavItem btnName="Home" appBtnPosition="left" on:click={() => replace('/')}>
    <HomeSolid
      class="w-6 h-6 mb-1 text-gray-500 dark:text-gray-400 group-hover:text-primary-600 dark:group-hover:text-primary-500"
    />
    <Tooltip arrow={false}>Home</Tooltip>
  </BottomNavItem>
  <BottomNavItem btnName="Community" appBtnPosition="middle" on:click={() => replace('/community')}>
    <GlobeSolid
      class="w-6 h-6 mb-1 text-gray-500 dark:text-gray-400 group-hover:text-primary-600 dark:group-hover:text-primary-500"
    />
    <Tooltip arrow={false}>Community</Tooltip>
  </BottomNavItem>
  <div class="flex items-center justify-center">
    <BottomNavItem
      btnName="Start Drawing"
      appBtnPosition="middle"
      btnClass="inline-flex items-center justify-center w-15 h-15 font-medium bg-primary-600 rounded-full hover:bg-primary-700 group focus:ring-4 focus:ring-primary-300 dark:focus:ring-primary-800"
      on:click={startDrawing}
    >
      <svg xmlns="http://www.w3.org/2000/svg" width="1.5em" height="4em" viewBox="0 0 416 512"
        ><path
          fill="currentColor"
          d="M272 96c26.51 0 48-21.49 48-48S298.51 0 272 0s-48 21.49-48 48s21.49 48 48 48M113.69 317.47l-14.8 34.52H32c-17.67 0-32 14.33-32 32s14.33 32 32 32h77.45c19.25 0 36.58-11.44 44.11-29.09l8.79-20.52l-10.67-6.3c-17.32-10.23-30.06-25.37-37.99-42.61M384 223.99h-44.03l-26.06-53.25c-12.5-25.55-35.45-44.23-61.78-50.94l-71.08-21.14c-28.3-6.8-57.77-.55-80.84 17.14l-39.67 30.41c-14.03 10.75-16.69 30.83-5.92 44.86s30.84 16.66 44.86 5.92l39.69-30.41c7.67-5.89 17.44-8 25.27-6.14l14.7 4.37l-37.46 87.39c-12.62 29.48-1.31 64.01 26.3 80.31l84.98 50.17l-27.47 87.73c-5.28 16.86 4.11 34.81 20.97 40.09c3.19 1 6.41 1.48 9.58 1.48c13.61 0 26.23-8.77 30.52-22.45l31.64-101.06c5.91-20.77-2.89-43.08-21.64-54.39l-61.24-36.14l31.31-78.28l20.27 41.43c8 16.34 24.92 26.89 43.11 26.89H384c17.67 0 32-14.33 32-32s-14.33-31.99-32-31.99"
        /></svg
      >
      <Tooltip arrow={false}>Start Drawing</Tooltip>
    </BottomNavItem>
  </div>
  <BottomNavItem btnName="Route" appBtnPosition="middle" on:click={() => replace('/makeroute')}>
    <PaletteOutline
      class="w-6 h-6 mb-1 text-gray-500 dark:text-gray-400 group-hover:text-primary-600 dark:group-hover:text-primary-500"
    />
    <Tooltip arrow={false}>Route</Tooltip>
  </BottomNavItem>
  <BottomNavItem btnName="Myhealth" appBtnPosition="right" on:click={() => replace('/myhealth')}>
    <UserCircleSolid
      class="w-6 h-6 mb-1 text-gray-500 dark:text-gray-400 group-hover:text-primary-600 dark:group-hover:text-primary-500"
    />
    <Tooltip arrow={false}>Myhealth</Tooltip>
  </BottomNavItem>
</BottomNav>

<!-- <a use:link href="/makeroute">MakeRoute</a>
  <a use:link href="/drawingmap">Drawing Map</a> -->

<style>
</style>

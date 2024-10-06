<script lang="ts">
  import { onMount } from 'svelte';
  import { PaletteOutline } from 'flowbite-svelte-icons';
  import Swal from 'sweetalert2';

  import DrawingInfoBox from '@/components/calendar/DrawingInfoBox.svelte';
  import { getDrawingDetail } from '@/api/myhealthApi';
  import { formatToKoreanTime } from '@/utils/formatter';

  interface DrawingInfo {
    title: string;
    routeImage: string;
    drawingDetailList: DrawingDetail[];
  }

  interface DrawingDetail {
    drawingDetailId: number;
    drawingDetailImage: string;
    drawingDetailDistance: number;
    drawingDetailTime: number;
    drawingDetailAvgPace: number;
    drawingDetailCreateTime: string;
  }

  export let drawingId;
  export let date;
  let drawingDetail: DrawingInfo;

  function showImageModal() {
    Swal.fire({
      imageUrl: drawingDetail?.routeImage,
      imageAlt: 'Custom image',
      background: '#fff',
      width: 'auto',
      padding: '20px',
    });
  }
  onMount(() => {
    getDrawingDetail(
      drawingId,
      date,
      (response) => {
        drawingDetail = response.data.data;
        console.log(drawingDetail);
      },
      (error) => {}
    );
  });
</script>

<div class="p-4 bg-white shadow-lg rounded-lg overflow-hidden">
  <div class="flex flex-row justify-between items-center">
    <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
      {date} &nbsp;
      {drawingDetail?.title}
    </h5>
    <button
      class="flex items-center justify-center p-2 rounded bg-gray-200 hover:bg-gray-300"
      on:click={showImageModal}
    >
      <PaletteOutline
        class="w-6 h-6 text-gray-500 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-500"
      />
    </button>
  </div>

  <ul class="space-y-4 mt-4">
    {#if drawingDetail?.drawingDetailList}
      {#each drawingDetail.drawingDetailList as detail}
        <li class="bg-gray-100 p-4 rounded-lg shadow">
          <p class="font-medium text-gray-600 text-[1.40rem] leading-7">
            {formatToKoreanTime(detail.drawingDetailCreateTime)}
          </p>
          <DrawingInfoBox
            time={detail.drawingDetailTime}
            averagePace={detail.drawingDetailAvgPace}
            distance={detail.drawingDetailDistance}
            imgSrc={detail.drawingDetailImage}
          />
        </li>
      {/each}
    {/if}
  </ul>
</div>

<script lang="ts">
  import { onMount } from 'svelte';
  import { PaletteOutline } from 'flowbite-svelte-icons';
  import Swal from 'sweetalert2';
  import { Card } from 'flowbite-svelte';

  import DrawingSummaryInfo from '@components/calendar/DrawingSummaryInfo.svelte';
  import { getDrawingInfo } from '@/api/drawingApi';
  import { formatToKoreanTime } from '@/utils/formatter';

  interface DrawingInfo {
    routeImage: string;
    drawingDetails: DrawingDetail[];
    totalDistance: number;
    totalTime: number;
    totalStep: number;
    avgPace: number;
  }

  interface DrawingDetail {
    drawingDetailImage: string;
    created: string;
  }

  export let drawingId;
  export let title;
  let drawingDetail: DrawingInfo;

  function showImageModal() {
    if (drawingDetail && drawingDetail.routeImage) {
      // drawingDetail 존재 확인
      Swal.fire({
        imageUrl: drawingDetail.routeImage,
        imageAlt: 'Custom image',
        background: '#fff',
        width: 'auto',
        padding: '20px',
      });
    }
  }
  onMount(async () => {
    let response = await getDrawingInfo(drawingId, true);
    drawingDetail = response.data;
  });
</script>

{#if drawingDetail}
  <div class="p-4 bg-white rounded-lg overflow-hidden">
    <div class="flex flex-row justify-between items-center mb-1">
      <div>
        <h6 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          {title}
        </h6>
        <span class="mb-2 text-sm tracking-tight text-gray-800 dark:text-white">
          {#if drawingDetail && drawingDetail.totalStep >= 0}
            {drawingDetail.totalStep} 걸음{/if}
        </span>
      </div>
      {#if drawingDetail && drawingDetail.routeImage}
        <button
          class="flex items-center justify-center p-2 rounded bg-gray-200 hover:bg-gray-300"
          on:click={showImageModal}
        >
          <PaletteOutline
            class="w-6 h-6 text-gray-500 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-500"
          />
        </button>
      {/if}
    </div>
    <DrawingSummaryInfo
      time={drawingDetail.totalTime}
      averagePace={drawingDetail.avgPace}
      distance={drawingDetail.totalDistance}
    />
    <ul class="space-y-4 mt-4">
      {#if drawingDetail && drawingDetail.drawingDetails}
        {#each drawingDetail.drawingDetails as detail}
          <!-- <Card img={detail.drawingDetailImage} horizontal> -->
          <Card horizontal>
            <img
              src={detail.drawingDetailImage}
              alt="Detailed view of the drawing"
              class="h-48 object-cover"
            />
            <div class="pt-4">
              {formatToKoreanTime(detail.created, true)}
            </div>
          </Card>
        {/each}
      {/if}
    </ul>
  </div>
{:else}
  <div class="p-4 bg-white shadow-lg rounded-lg overflow-hidden">
    <p class="text-center text-gray-500 dark:text-gray-400">해당 드로잉 정보를 찾을 수 없습니다.</p>
  </div>
{/if}

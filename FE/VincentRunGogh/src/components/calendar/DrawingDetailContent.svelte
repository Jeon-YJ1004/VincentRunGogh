<script lang="ts">
  import { onMount } from 'svelte';

  import DrawingInfoBox from '@/components/calendar/DrawingInfoBox.svelte';
  import { getDrawingDetail } from '@/api/myhealthApi';
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
    drawingDetailSpeed: number;
    drawingDetailCreateTime: string;
  }

  export let drawingId;
  export let date;
  let drawingDetail: DrawingInfo;

  onMount(() => {
    console.log(drawingId, date);
    //FIXME - 드로잉 상세 조회 api 연결
    getDrawingDetail(
      drawingId,
      date,
      (response) => {},
      (error) => {}
    );
    drawingDetail = {
      title: '마루는 강쥐',
      routeImage: 'https:~',
      drawingDetailList: [
        {
          drawingDetailId: 1,
          drawingDetailImage: 'https:~',
          drawingDetailDistance: 1,
          drawingDetailTime: 1,
          drawingDetailSpeed: 1,
          drawingDetailCreateTime: '2024-06-19 19-20-57',
        },
      ],
    };
  });
  // onMount(async () => {
  //   const response = await fetch('/api/drawings/detail');
  //   const data = await response.json();
  //   drawingDetail.set(data);
  // });
</script>

<div>
  <h1>{drawingDetail?.title}</h1>
  <img src={drawingDetail?.routeImage} alt="routeImage" />
  <ul>
    {#if drawingDetail?.drawingDetailList}
      {#each drawingDetail.drawingDetailList as detail}
        <li>
          <p>{detail.drawingDetailCreateTime}</p>
          <DrawingInfoBox
            time={detail.drawingDetailTime}
            averagePace={detail.drawingDetailSpeed}
            distance={detail.drawingDetailDistance}
            imgSrc={detail.drawingDetailImage}
          />
        </li>
      {/each}
    {:else}
      <p>Loading...</p>
    {/if}
  </ul>
</div>

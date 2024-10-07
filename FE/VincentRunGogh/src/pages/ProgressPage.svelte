<script lang="ts">
  import { onMount } from 'svelte';
  import { Chart } from 'svelte-chartjs';
  import {
    Chart as ChartJS,
    Tooltip,
    Legend,
    BarElement,
    PointElement,
    LineElement,
    CategoryScale,
    LinearScale,
    LineController,
    BarController,
  } from 'chart.js';
  import { writable, derived } from 'svelte/store';
  import { Select } from 'flowbite-svelte';
  import type { SelectOptionType } from 'flowbite-svelte';

  import Header from '@components/common/Header.svelte';
  import { formatSecToH, formatDistanceFix2 } from '@/utils/formatter';
  ChartJS.register(
    LinearScale,
    CategoryScale,
    BarElement,
    PointElement,
    LineElement,
    Legend,
    Tooltip,
    LineController,
    BarController
  );

  let selectedYear: number = new Date().getFullYear();
  let yearItem: SelectOptionType<any>[] = [
    { value: '2024', name: '2024' },
    { value: '2023', name: '2023' },
  ];
  const onChangeYear = () => {
    console.log('change year');
    //TODO - api 연결
    activeCategory.update(() => 'walk');
  };

  //SECTION - 차트
  let activeCategory = writable('walk');
  let categoryData = writable({
    walkList: [11, 22, 33, 44, 55, 66, 77, 88, 99, 100, 110, 120],
    distanceList: [11, 22, 33, 44, 55, 66, 77, 88, 99, 100, 110, 120],
    timeList: [11, 22, 33, 44, 55, 66, 77, 88, 99, 100, 110, 120],
    completedRouteDrawingList: [2, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
    completedFreeDrawingList: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
    totalWalk: 123456,
    totalDistance: 123456,
    totalTime: 123456,
    totalCompletedDrawing: 123,
  });
  const labels = Array.from({ length: 12 }, (_, i) => `${i + 1}월`);
  let chartKey = writable(0);

  const chartData = derived(activeCategory, ($activeCategory) => {
    // 차트 데이터 생성
    let datasets = [];
    switch ($activeCategory) {
      case 'walk':
        datasets = [
          {
            label: '걸음 수',
            data: $categoryData.walkList,
            fill: true,
            borderColor: 'yellow',
            backgroundColor: '#ffb800',
          },
        ];
        break;
      case 'distance':
        datasets = [
          {
            label: '이동 거리',
            data: $categoryData.distanceList,
            borderColor: 'green',
            backgroundColor: '#5e8358',
          },
        ];
        break;
      case 'time':
        datasets = [
          {
            label: '활동 시간',
            data: $categoryData.timeList,
            borderColor: 'red',
            backgroundColor: '#ff9693',
          },
        ];
        break;
      case 'completed':
        datasets = [
          {
            label: '완료된 루트 그리기',
            data: $categoryData.completedRouteDrawingList,
            borderColor: 'yellow',
            backgroundColor: 'rgba(255, 255, 0, 0.5)',
          },
          {
            label: '완료된 자유 그리기',
            data: $categoryData.completedFreeDrawingList,
            borderColor: 'purple',
            backgroundColor: 'rgba(128, 0, 128, 0.5)',
          },
        ];

        break;
    }
    return { labels, datasets };
  });

  let options = {
    responsive: true,

    scales: {
      y: {
        beginAtZero: true,
        // display: false,
        stacked: true,
      },
      x: {
        // display: false,
        stacked: true,
      },
    },
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        enabled: true,
        usePointStyle: true,
        callbacks: {
          // To change title in tooltip
          label: (context) => {
            var label = context.dataset.label;
            var value = context.formattedValue;
            if (label === '활동 시간') return label + ' : ' + value + ' 초';
            else if (label === '이동 거리') return label + ' : ' + value + ' M';
            else return label + ' : ' + value + ' 번';
          },
        },
      },
    },
  };
  onMount(() => {
    //TODO - api 연결 드로잉 활동 정보 조회 /api/v1/myhealth/drawings?year={year}
  });
</script>

<div class="flex flex-col items-center bg-bg-main h-screen">
  <Header title="통계" />
  <Select items={yearItem} bind:value={selectedYear} on:change={onChangeYear} />
  {#if $activeCategory === 'completed'}
    <Chart type="bar" {options} data={$chartData} />
  {:else}
    <Chart type="line" {options} data={$chartData} />
  {/if}
  <div class="grid grid-flow-row-dense grid-cols-3 grid-rows-3">
    <button class="categoryBtn col-span-2" on:click={() => activeCategory.set('walk')}>
      <div class="categoryTitle">
        <span>걸음수</span>
      </div>
      <span class="chartSum">{$categoryData.totalWalk}</span>
    </button>
    <button class="categoryBtn col-span-2" on:click={() => activeCategory.set('distance')}>
      <div class="categoryTitle">
        <span>거리</span>
      </div>
      <span class="chartSum">{formatDistanceFix2($categoryData.totalDistance)}</span>
    </button>
    <button class="categoryBtn" on:click={() => activeCategory.set('time')}>
      <div class="categoryTitle">
        <span>시간(h)</span>
      </div>
      <span class="chartSum">{formatSecToH($categoryData.totalTime)}</span>
    </button>
    <button class="categoryBtn" on:click={() => activeCategory.set('completed')}>
      <div class="categoryTitle">
        <span>완성(개)</span>
      </div>
      <span class="chartSum">{$categoryData.totalCompletedDrawing}</span>
    </button>
  </div>
</div>

<style>
  .categoryBtn {
    width: 40vw;
    height: 20vh;
    border-radius: 20px;
    background: #fff;
  }

  .categoryTitle {
    width: 100%;
    height: 11.184px;
    flex-shrink: 0;
  }
  .categoryTitle span {
    color: var(--gray-color-200, #bebaba);
    font-size: 18px;
    font-style: normal;
    font-weight: 300;
    line-height: normal;
  }
  .chartSum {
    color: var(--gray-label-color, #868282);
    font-family: Roboto;
    font-size: 24px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
  }
</style>

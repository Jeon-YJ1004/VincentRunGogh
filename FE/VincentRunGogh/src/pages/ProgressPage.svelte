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
    Filler,
  } from 'chart.js';
  import { writable, derived } from 'svelte/store';
  import { Select } from 'flowbite-svelte';
  import type { SelectOptionType } from 'flowbite-svelte';

  import Header from '@components/common/Header.svelte';
  import { formatSecToH, formatDistanceFix2 } from '@/utils/formatter';
  import { getDrawingProgress } from '@/api/myhealthApi';
  ChartJS.register(
    LinearScale,
    CategoryScale,
    BarElement,
    PointElement,
    LineElement,
    Legend,
    Tooltip,
    LineController,
    BarController,
    Filler
  );

  const yellowGradient = [
    'rgba(250, 253, 177, 0.5)',
    'rgba(249, 252, 153, 0.6)',
    'rgba(248, 251, 128, 0.7)',
    'rgba(247, 250, 102, 0.8)',
    'rgb(255, 210, 95,0.9)',
  ];
  const redGradient = [
    'rgba(255, 150, 147, 0.5)',
    'rgba(255, 126, 126, 0.6)',
    'rgba(255, 102, 102, 0.7)',
    'rgba(255, 78, 78, 0.8)',
    'rgba(255, 64, 64, 0.9)',
  ];
  const blueGradient = [
    'rgba(129, 212, 250, 0.5)',
    'rgba(77, 182, 233, 0.6)',
    'rgba(3, 169, 244, 0.7)',
    'rgba(2, 136, 209, 0.8)',
    'rgba(1, 87, 155, 0.9)',
  ];
  const greenGradient = [
    'rgba(162, 254, 165, 0.5)',
    'rgba(134, 240, 136, 0.6)',
    'rgba(107, 226, 108, 0.7)',
    'rgba(80, 212, 80, 0.8)',
    'rgba(51, 160, 59, 0.9)',
  ];
  const purpleGradient = [
    'rgba(185, 130, 208, 0.5)',
    'rgba(170, 115, 193, 0.6)',
    'rgba(155, 100, 178, 0.7)',
    'rgba(140, 85, 163, 0.8)',
    'rgba(125, 70, 148, 0.9)',
  ];

  let selectedYear: number = new Date().getFullYear();
  let yearItem: SelectOptionType<any>[] = [
    { value: '2024', name: '2024' },
    { value: '2023', name: '2023' },
  ];

  //SECTION - 차트
  let activeCategory = writable('walk');
  let categoryData = writable({
    walkList: [],
    distanceList: [],
    timeList: [],
    completedRouteDrawingList: [],
    completedFreeDrawingList: [],
    totalWalk: 0,
    totalDistance: 0,
    totalTime: 0,
    totalCompletedDrawing: 0,
  });
  const labels = Array.from({ length: 12 }, (_, i) => `${i + 1}월`);
  const onChangeYear = () => {
    console.log('change year');
    setChartData();
    activeCategory.update(() => 'walk');
  };
  const setChartData = () => {
    getDrawingProgress(
      selectedYear,
      (response) => {
        categoryData.set(response.data.data);
      },
      (error) => {}
    );
  };
  const makeChartBgColor = (context, bgColor) => {
    if (!context.chart.chartArea) return;
    const {
      ctx,
      chartArea: { top, bottom, height },
      scales: { y },
    } = context.chart;
    const gradientBg = ctx.createLinearGradient(0, top, 0, bottom);

    // y축 범위를 7개 구간으로 나누어 세밀하게 그라데이션을 생성
    const steps = 5;
    const range = Array.from(
      { length: steps },
      (_, i) => y.min + (i * (y.max - y.min)) / (steps - 1)
    );
    const rangePercentage = range.map((value) => {
      let val = (y.getPixelForValue(value) - top) / height;
      if (val > 1) val = 1;
      if (val < 0) val = 0;
      return val;
    });

    rangePercentage.forEach((percent, index) => {
      gradientBg.addColorStop(percent, bgColor[index]);
    });

    return gradientBg;
  };

  const makeBorderRadius = (ctx, isTop) => {
    const datasetIndex = ctx.datasetIndex;
    const dataset = ctx.chart.data.datasets[datasetIndex];
    const dataPoint = dataset.data[ctx.dataIndex];

    if (dataPoint === undefined || dataPoint === null) {
      return {
        topLeft: 20,
        topRight: 20,
        bottomLeft: 20,
        bottomRight: 20,
      };
    } else if (isTop) {
      return {
        topLeft: 20,
        topRight: 20,
      };
    } else {
      return {
        bottomLeft: 20,
        bottomRight: 20,
      };
    }
  };
  const chartData = derived([activeCategory, categoryData], ([$activeCategory, $categoryData]) => {
    // 차트 데이터 생성
    let datasets = [];
    switch ($activeCategory) {
      case 'walk':
        datasets = [
          {
            label: '걸음 수',
            data: $categoryData.walkList,
            fill: true,
            borderColor: yellowGradient,
            backgroundColor: (context) => {
              return makeChartBgColor(context, yellowGradient);
            },
            tension: 0.2,
          },
        ];
        break;
      case 'distance':
        datasets = [
          {
            label: '이동 거리',
            data: $categoryData.distanceList,
            borderColor: greenGradient,
            backgroundColor: (context) => {
              return makeChartBgColor(context, greenGradient);
            },
            tension: 0.2,
            fill: true,
          },
        ];
        break;
      case 'time':
        datasets = [
          {
            label: '활동 시간',
            data: $categoryData.timeList,
            borderColor: purpleGradient,
            backgroundColor: (context) => {
              return makeChartBgColor(context, purpleGradient);
            },
            tension: 0.2,
            fill: true,
          },
        ];
        break;
      case 'completed':
        datasets = [
          {
            label: '완료된 루트 그리기',
            data: $categoryData.completedRouteDrawingList,
            backgroundColor: (context) => {
              return makeChartBgColor(context, blueGradient);
            },
            borderWidth: 1,
            borderSkipped: false,
            barPercentage: 0.8,
            borderRadius: (ctx) => {
              return makeBorderRadius(ctx, false);
            },
          },
          {
            label: '완료된 자유 그리기',
            data: $categoryData.completedFreeDrawingList,
            backgroundColor: (context) => {
              return makeChartBgColor(context, redGradient);
            },
            borderWidth: 1,
            borderSkipped: false,
            barPercentage: 0.8,
            borderRadius: (ctx) => {
              return makeBorderRadius(ctx, true);
            },
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
      colors: {
        forceOverride: true,
      },
    },
  };
  onMount(() => {
    setChartData();
  });
</script>

<div class="flex flex-col items-center bg-bg-main h-screen">
  <Header title="통계" />
  <div class="flex w-full items-center justify-center">
    <h5 class="mb-2 ttext-3xl font-semibold">{selectedYear}</h5>

    <Select
      items={yearItem}
      bind:value={selectedYear}
      on:change={onChangeYear}
      placeholder="연도"
      class="w-28 absolute right-0"
    />
  </div>
  {#if $activeCategory === 'completed'}
    <Chart type="bar" {options} data={$chartData} class="m-4 flex justify-center" />
  {:else}
    <Chart type="line" {options} data={$chartData} class="m-4 flex justify-center" />
  {/if}
  <div
    class="grid grid-flow-row-dense grid-cols-3 grid-rows-3 items-center justify-center justify-items-center gap-[1rem] mr-[10%]"
  >
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

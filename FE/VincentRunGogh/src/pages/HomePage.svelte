<script lang="ts">
  import { getDrawingList } from '@/api/drawingApi2';
  import { getWeeklyInfo } from '@/api/userApi';
  import Tabbar from '@/components/tabbar/Tabbar.svelte';
  import { Chart, Card, ImagePlaceholder } from 'flowbite-svelte';
  import { onMount } from 'svelte';
  import { userStore } from '@/stores/userStore';
  import Swal from 'sweetalert2';
  import { writable } from 'svelte/store';

  let isLoad: boolean = false;

  // 로컬스토리지에서 유저정보 가져오기
  let user: {
    profile: string;
    nickname: string;
  } = {
    profile: '',
    nickname: '',
  };

  // 유형별 정보 더미
  let dummyGroupInfo: {
    averageDayRuntime: number;
    averageDayDistance: number;
    averageSpeed: number;
    myDayRuntime: number;
    myDayDistance: number;
    mySpeed: number;
    gender: number;
    age: number;
  } = {
    // 평균 운동량 600초, 400미터, 4km/h
    // 더미 운동량 1200초, 800미터, 8km/h
    // 더미 정보 20세 남자
    averageDayRuntime: 600,
    averageDayDistance: 400,
    averageSpeed: 4,
    myDayRuntime: 1200,
    myDayDistance: 800,
    mySpeed: 8,
    gender: 0,
    age: 20,
  };

  // 홈 문구 대사
  // 내 그룹 설정
  let groupIn: string = '';
  if (!dummyGroupInfo.gender) {
    groupIn = `${Math.floor(dummyGroupInfo.age / 10) * 10}대 남자`;
  } else {
    groupIn = `${Math.floor(dummyGroupInfo.age / 10) * 10}대 여자`;
  }
  let welcomeWordList: string[] = [
    '오운완했나요?',
    `${groupIn}는 오늘 평균 ${Math.floor(dummyGroupInfo.averageDayRuntime / 60)}분 운동했습니다!`,
    `${groupIn}는 오늘 평균 ${Math.floor(dummyGroupInfo.averageDayDistance / 1000)}km 뛰었습니다!`,
    `오늘은 ${Math.floor(dummyGroupInfo.myDayRuntime / 60)}분 뛰었군요!`,
    `오늘은 ${parseFloat((dummyGroupInfo.myDayDistance / 1000).toFixed(2))}km를 뛰었군요!`,
  ];
  // 랜덤 문구 출력
  let randomNum: number = Math.floor(Math.random() * welcomeWordList.length);

  // 진행중 드로잉 더미

  // 진행중 드로잉 리스트 받기
  let ongoingDrawingList: {
    drawingId: number;
    title: string;
    artImage: string;
    drawingImage: string;
    updated: string;
  }[] = [];

  async function getDrawingInfo() {
    try {
      let responseOngoing = await getDrawingList('ongoing');
      let ongoingLength: number = responseOngoing.data.findDrawingList.length;
      if (ongoingLength > 3) {
        for (let i = 0; i < 3; i++) {
          ongoingDrawingList = [...ongoingDrawingList, responseOngoing.data.findDrawingList[i]];
        }
      } else {
        for (let i = 0; i < ongoingLength; i++) {
          ongoingDrawingList = [...ongoingDrawingList, responseOngoing.data.findDrawingList[i]];
        }
      }
    } catch (error) {
      {
        throw error;
      }
    }
  }

  // 진행중인 드로잉 클릭
  async function clickOngoingDrawing(drawingId: number) {
    // try {
    //     let responseOngoing = await getDrawingInfo(drawingId);
    //     // 요청 성공 후 모달 열기
    //     Swal.fire({
    //         title: '진행중인 드로잉',
    //         html:'<div id='
    //         text: data.message, // AJAX 요청으로 받은 데이터를 모달에 표시
    //         showCancelButton: true,
    //         confirmButtonText: '확인',
    //         preConfirm: () => {
    //             // 확인 버튼 클릭 시 실행할 코드
    //             return new Promise((resolve) => {
    //                 // 추가 처리 코드 (예: 다른 AJAX 요청 등)
    //                 resolve();
    //             });
    //         }
    //     });
    // } catch (error) {
    //     // 오류 처리
    //     Swal.fire('오류', 'AJAX 요청에 실패했습니다.', 'error');
    // }
  }

  // 주간 운동량
  // 이전 7일 계산
  let today: Date = new Date();
  let dateList: string[] = [];

  for (let i = 6; i >= 0; i--) {
    const pastDate = new Date();
    pastDate.setDate(today.getDate() - i);

    const month = pastDate.getMonth() + 1; // 월은 0부터 시작하므로 +1
    const day = pastDate.getDate();

    dateList.push(`${month}/${day}`);
  }

  // 주간 운동량 더미
  let dummyWeeklyinfo: {
    distance: number[];
    time: number[];
  } = {
    distance: [1234, 2345, 2030, 3040, 4050, 2020, 3030],
    time: [600, 700, 800, 654, 1065, 1624, 845],
  };
  // 요청받기
  let weeklyInfo: {
    distance: number[];
    time: number[];
  } = {
    distance: [],
    time: [],
  };

  async function getWeekly() {
    try {
      let responseWeekly = await getWeeklyInfo();
      weeklyInfo = responseWeekly.data;
      options = {
        ...options,
        series: [
          {
            name: '운동 거리',
            color: '#FFB800',
            data: [
              { x: dateList[0], y: Math.floor(weeklyInfo.distance[0] * 0.25) },
              { x: dateList[1], y: Math.floor(weeklyInfo.distance[1] * 0.25) },
              { x: dateList[2], y: Math.floor(weeklyInfo.distance[2] * 0.25) },
              { x: dateList[3], y: Math.floor(weeklyInfo.distance[3] * 0.25) },
              { x: dateList[4], y: Math.floor(weeklyInfo.distance[4] * 0.25) },
              { x: dateList[5], y: Math.floor(weeklyInfo.distance[5] * 0.25) },
              { x: dateList[6], y: Math.floor(weeklyInfo.distance[6] * 0.25) },
            ],
          },
          {
            name: '운동 시간',
            color: '#5E8358',
            data: [
              { x: dateList[0], y: weeklyInfo.time[0] },
              { x: dateList[1], y: weeklyInfo.time[1] },
              { x: dateList[2], y: weeklyInfo.time[2] },
              { x: dateList[3], y: weeklyInfo.time[3] },
              { x: dateList[4], y: weeklyInfo.time[4] },
              { x: dateList[5], y: weeklyInfo.time[5] },
              { x: dateList[6], y: weeklyInfo.time[6] },
            ],
          },
        ],
      };
    } catch (error) {
      {
        throw error;
      }
    }
  }

  // 주간 차트 옵션
  let options = {
    colors: ['rgb(255,184,0)', 'rgb(94,131,88)'],
    series: [
      {
        name: '운동 거리',
        color: '#FFB800',
        data: [
          { x: dateList[0], y: 0 },
          { x: dateList[1], y: 0 },
          { x: dateList[2], y: 0 },
          { x: dateList[3], y: 0 },
          { x: dateList[4], y: 0 },
          { x: dateList[5], y: 0 },
          { x: dateList[6], y: 0 },
        ],
      },
      {
        name: '운동 시간',
        color: '#5E8358',
        data: [
          { x: dateList[0], y: 0 },
          { x: dateList[1], y: 0 },
          { x: dateList[2], y: 0 },
          { x: dateList[3], y: 0 },
          { x: dateList[4], y: 0 },
          { x: dateList[5], y: 0 },
          { x: dateList[6], y: 0 },
        ],
      },
    ],
    chart: {
      type: 'bar',
      width: '100%',
      height: '120%',
      fontFamily: 'Pretendard-Regular',
      toolbar: {
        show: false,
      },
    },
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: '75%',
        borderRadiusApplication: 'end',
        borderRadius: 4,
      },
    },
    tooltip: {
      shared: true,
      intersect: false,
      style: {
        fontFamily: 'Pretendard-Regular',
      },
      y: {
        formatter: function (val: number, { seriesIndex }) {
          if (seriesIndex === 0) {
            return Math.floor(val) + 'km'; // '운동 거리'에 km 단위 추가
          } else if (seriesIndex === 1) {
            return Math.floor(val / 60) + '분'; // '운동 시간'에 분 단위 추가
          }
          return val;
        },
      },
    },
    states: {
      hover: {
        filter: {
          type: 'darken',
          value: 1,
        },
      },
    },
    stroke: {
      show: true,
      width: 0,
      colors: ['transparent'],
    },
    grid: {
      show: false,
      strokeDashArray: 4,
      padding: {
        left: 2,
        right: 2,
        top: -14,
      },
    },
    dataLabels: {
      enabled: false,
    },
    legend: {
      show: false,
    },
    xaxis: {
      floating: false,
      labels: {
        show: true,
        style: {
          fontFamily: 'Pretendard-Regular',
          cssClass: 'text-xs font-normal fill-gray-500 dark:fill-gray-400',
        },
      },
      axisBorder: {
        show: false,
      },
      axisTicks: {
        show: false,
      },
    },
    yaxis: {
      show: false,
    },
    fill: {
      opacity: 1,
    },
  };

  onMount(async () => {
    await getWeekly();
    await getDrawingInfo();
    let UserInfo = localStorage.getItem('user');
    console.log(UserInfo);
    if (UserInfo) {
      user = {
        profile: JSON.parse(UserInfo).profile,
        nickname: JSON.parse(UserInfo).nickname,
      };
    }
    isLoad = true;
  });
</script>

<!-- 차트 째보기 -->

<div id="homepage-body">
  <div id="homepage-header">
    <h1 style="font-family: 'Brush Script MT'; font-size:30px;">Vincent Run Gogh</h1>
    <p><span class="text-xl font-bold">{user.nickname}</span>님!</p>
    <h3 class="font-bold">{welcomeWordList[randomNum]}</h3>
  </div>
  <div id="homepage-drawing">
    <p class="mt-2 font-bold">진행 중인 드로잉</p>
    <div id="drawing-list">
      {#if ongoingDrawingList}
        {#each ongoingDrawingList as drawing}
          <Card
            class="m-2 mb-0 text-xs p-0 w-c-30"
            size="xs"
            on:click={() => clickOngoingDrawing(drawing.drawingId)}
          >
            <img class="rounded-t-lg" src={drawing.artImage} alt="" />
            <div class="p-1">
              <h5 class="mb-2 font-bold tracking-tight text-gray-900 dark:text-white">
                {drawing.title}
              </h5>
              <p class="font-normal text-gray-700 dark:text-gray-400 leading-tight">
                {drawing.updated.split('T')[0]}
              </p>
            </div>
          </Card>
        {/each}
      {:else}
        <Card
          class="m-2 mt-5 mb-5 text-xs p-0 h-40 gap-1 flex flex-col justify-around items-center"
          size="xs"
          horizontal
        >
          <ImagePlaceholder imgOnly imgHeight="20" divClass="w-20 m-0 animate-pulse" />
          <ImagePlaceholder imgOnly imgHeight="20" divClass="w-20 m-0 animate-pulse" />
          <ImagePlaceholder imgOnly imgHeight="20" divClass="w-20 m-0 animate-pulse" />
        </Card>
      {/if}
    </div>
  </div>
  <div id="homepage-chart">
    <Card class="w-80 pb-1 pt-3 bg-opacity-80">
      <p class="font-bold text-black">주간 운동 정보</p>
      {#if isLoad}
        <div class="flex ms-3 justify-center items-center">
          <Chart {options} />
        </div>
      {/if}
    </Card>
  </div>
  <div id="homepage-tabbar">
    <Tabbar />
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
</div>

<style>
  #homepage-body {
    justify-self: center;
    text-align: center;
    width: 100%;
    max-width: 1024px;
    margin: 0;
    background: rgba(0, 0, 0, 0);
    display: flex;
    flex-direction: column;
    position: relative;
  }

  #homepage-header {
    z-index: 2;
    justify-self: left;
    height: 25vh;
    padding: 2vh 0vh 10vh 0vh;
  }
  #homepage-header > h1 {
    margin-bottom: 10vh;
  }
  #homepage-drawing {
    height: 28vh;
    padding-top: 0%;
    width: 90%;
    margin-left: 5%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-content: center;
    margin-bottom: 5vh;
    border-radius: 3%;
    background-color: rgba(255, 255, 255, 0.8);
    --tw-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
    --tw-shadow-colored: 0 4px 6px -1px var(--tw-shadow-color),
      0 2px 4px -2px var(--tw-shadow-color);
    box-shadow: var(--tw-ring-offset-shadow, 0 0 #0000), var(--tw-ring-shadow, 0 0 #0000),
      var(--tw-shadow);
    z-index: 2;
  }

  #drawing-list {
    display: flex;
    height: 90%;
    justify-content: space-around;
    align-items: center;
    overflow: hidden;
    z-index: 2;
  }

  #homepage-chart {
    height: 28vh;
    gap: 1vh;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2;
  }

  #homepage-tabbar {
    height: 15vh;
    z-index: 10;
    background-color: rgba(255, 255, 255, 0);
  }

  #background {
    position: fixed;
    top: 6%;
    left: 26%;
    transform: translateX(-50%);
    z-index: 1;
    opacity: 0.8;
    overflow: visible;
  }

  #background2 {
    position: fixed;
    bottom: -9%;
    right: -50%;
    transform: translateX(-50%);
    z-index: 1;
    opacity: 0.8;
    overflow: visible;
  }

  .apexcharts-inner {
    margin: auto;
  }
</style>

<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import { link } from 'svelte-spa-router';
  import { writable, get } from 'svelte/store';
  import FullCalendar from 'svelte-fullcalendar';
  import type { CalendarOptions } from 'svelte-fullcalendar';
  import daygridPlugin from '@fullcalendar/daygrid';
  import interactionPlugin from '@fullcalendar/interaction';
  import { getMonthData } from '@/api/myhealthApi';
  import DrawingSummaryInfo from '@/components/calendar/DrawingSummaryInfo.svelte';
  type CalendarOptions = typeof CalendarOptions;

  interface MonthInfo {
    monthTotalTime: number;
    monthTotalDistance: number;
    dayList: DayInfo[];
  }

  interface DayInfo {
    date: string;
    isRun: boolean;
    isDrawing: boolean;
    dayTotalTime: number;
    dayTotalDistance: number;
    drawingList: Drawing[];
  }

  interface Drawing {
    drawingId: number;
    drawingName: string;
    drawingTime: number;
    drawingDistance: number;
  }
  let today = new Date();

  let monthInfo = writable<MonthInfo | null>(null);
  let selectedDayInfo = writable<DayInfo | null>(null);
  let selectedYear = writable<number | null>(null);
  let selectedMonth = writable<number | null>(null);
  let options: CalendarOptions;

  function getEventColor(day) {
    if (day.isDrawing) {
      return 'var(--yellow-main-color)';
    } else if (day.isRun) {
      return 'var(--green-main-color)';
    }
    return 'transparent';
  }

  function handleDateClick(info) {
    const dayData = get(monthInfo).dayList.find((day) => day.date === info.dateStr);
    if (dayData) {
      selectedDayInfo.set(dayData);
    } else {
      selectedDayInfo.set(null); // 일치하는 날짜가 없을 경우 빈 객체를 설정
    }
    // info.dayEl.style.backgroundColor = '#8BA2E7AD';
    console.log($selectedDayInfo.date);
  }

  function handleDatesSet(arg) {
    selectedYear.set(arg.start.getFullYear());
    selectedMonth.set(arg.start.getMonth() + 1); // 월은 0부터 시작하므로 +1
    selectedDayInfo.set(null);
    //TODO - 월별정보 가져오는 api 연결
    getMonthInfo();
  }
  const getMonthInfo = async () => {
    const year = today.getFullYear();
    const month = today.getMonth() + 1; // JavaScript에서 월은 0부터 시작하므로 +1

    selectedYear.set(year);
    selectedMonth.set(month);
    //FIXME - api 연결, 오늘 이벤트 추가
    $selectedMonth = getMonthData(
      $selectedYear,
      $selectedMonth,
      (response) => {
        let data = response.data.data;
        monthInfo.set({ ...response.data.data });
      },
      (error) => {}
    );
  };
  onMount(() => {
    getMonthInfo();
  });
  $: if ($monthInfo) {
    options = {
      initialView: 'dayGridMonth',
      plugins: [daygridPlugin, interactionPlugin],
      dateClick: handleDateClick,
      events: [
        {
          date: today.toISOString().slice(0, 10), // 오늘 날짜
          title: '오늘', // '오늘'이라는 제목
          color: 'transparent',
        },
        ...$monthInfo.dayList.map((day) => ({
          date: day.date,
          color: getEventColor(day),
        })),
      ],
      eventOrder: (a, b) => {
        // '오늘' 이벤트가 항상 먼저 오도록 정렬
        if (a.title === '오늘') return -1;
        if (b.title === '오늘') return 1;
        return 0;
      },
      datesSet: handleDatesSet,
      headerToolbar: {
        left: 'prev',
        center: 'title,today',
        right: 'next ',
      },
      buttonText: {
        today: '오늘',
      },

      dayCellContent: function (arg) {
        const dayNumber = arg.dayNumberText.replace('일', '');
        return dayNumber;
      },
      displayEventTime: false,
      locale: 'ko',
      selectable: true,
      showNonCurrentDates: false,
      fixedWeekCount: false,
      // contentHeight: 400,
    };
  }
</script>

<div class="flex flex-col items-center h-screen">
  <div id="calendar" class="h-[80vh]">
    <FullCalendar {options} />
  </div>

  {#if $selectedDayInfo}
    <div>
      <h2>Day Details:</h2>
      <p>Total Time: {$selectedDayInfo.dayTotalTime} seconds</p>
      <p>Total Distance: {$selectedDayInfo.dayTotalDistance} meters</p>
      <h3>Drawings:</h3>
      <ul>
        {#each $selectedDayInfo.drawingList as drawing}
          <a use:link href="/drawingdetail?id={drawing.drawingId}&date={$selectedDayInfo.date}">
            <li>
              {drawing.drawingName} - Time: {drawing.drawingTime}, Distance: {drawing.drawingDistance}
            </li>
          </a>
        {/each}
      </ul>
    </div>
  {:else if $monthInfo}
    <h2>{$selectedYear}년 {$selectedMonth}월 통계</h2>
    <DrawingSummaryInfo
      time={$monthInfo.monthTotalTime}
      averagePace={null}
      distance={$monthInfo.monthTotalDistance}
    />
  {/if}
</div>

<style>
</style>

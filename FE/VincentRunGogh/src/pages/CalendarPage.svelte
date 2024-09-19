<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import { link } from 'svelte-spa-router';
  import { writable, get } from 'svelte/store';
  import type { CalendarOptions } from 'svelte-fullcalendar';
  import FullCalendar from 'svelte-fullcalendar';
  import daygridPlugin from '@fullcalendar/daygrid';
  import interactionPlugin from '@fullcalendar/interaction';

  import DrawingSummaryInfo from '@/components/calendar/DrawingSummaryInfo.svelte';

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
  let date = writable(new Date());
  let monthInfo = writable<MonthInfo | null>(null);
  let selectedDayInfo = writable(null);
  let selectedYear = writable<number | null>(null);
  let selectedMonth = writable<number | null>(null);
  let options: CalendarOptions;

  function getEventColor(day) {
    if (day.isDrawing) {
      return 'yellow';
    } else if (day.isRun) {
      return 'green';
    }
    return 'gray';
  }

  function handleDateClick(info) {
    const selectedDay = new Date(info.dateStr).getDate(); // '2024-09-18'에서 '18'을 추출
    const dayData = get(monthInfo).dayList.find((day) => day.day === selectedDay);
    if (dayData) {
      selectedDayInfo.set(dayData);
    } else {
      selectedDayInfo.set(null); // 일치하는 날짜가 없을 경우 빈 객체를 설정
    }
    // info.dayEl.style.backgroundColor = '#8BA2E7AD';
  }

  function handleDatesSet(arg) {
    console.log(arg);
    selectedYear.set(arg.start.getFullYear());
    selectedMonth.set(arg.start.getMonth() + 1); // 월은 0부터 시작하므로 +1
    selectedDayInfo.set(null);
    //TODO - 월별정보 가져오는 api 연결
    // monthInfo.set()
  }
  onMount(() => {
    monthInfo.set({
      monthTotalTime: 78061,
      monthTotalDistance: 32553,
      dayList: [
        {
          date: '2024-09-13',
          isRun: true,
          isDrawing: false,
          dayTotalTime: 4120,
          dayTotalDistance: 2612,
          drawingList: [
            {
              drawingId: 123,
              drawingName: '마루는 강쥐',
              drawingTime: 620,
              drawingDistance: 503,
            },
            {
              drawingId: 112,
              drawingName: '고래 그림',
              drawingTime: 546,
              drawingDistance: 806,
            },
            {
              drawingId: 154,
              drawingName: '거북이 그림',
              drawingTime: 442,
              drawingDistance: 336,
            },
          ],
        },
        {
          date: '2024-09-14',
          isRun: true,
          isDrawing: true,
          dayTotalTime: 4120,
          dayTotalDistance: 2612,
          drawingList: [
            {
              drawingId: 123,
              drawingName: '마루는 강쥐',
              drawingTime: 620,
              drawingDistance: 503,
            },
          ],
        },
      ],
    });
  });
  $: if ($monthInfo) {
    options = {
      initialView: 'dayGridMonth',
      plugins: [daygridPlugin, interactionPlugin],
      dateClick: handleDateClick,
      events: $monthInfo.dayList.map((day) => ({
        date: day.date,
        color: getEventColor(day),
      })),
      datesSet: handleDatesSet,

      displayEventTime: false,
      locale: 'ko',
      selectable: true,
      showNonCurrentDates: false,
      fixedWeekCount: false,
    };
  }
</script>

<FullCalendar {options} />

{#if $selectedDayInfo}
  <div>
    <h2>Day Details:</h2>
    <p>Total Time: {$selectedDayInfo.dayTotalTime} seconds</p>
    <p>Total Distance: {$selectedDayInfo.dayTotalDistance} meters</p>
    <h3>Drawings:</h3>
    <ul>
      {#each $selectedDayInfo.drawingList as drawing}
        <a use:link href="/drawingdetail?id={drawing.drawingId}&date={drawing.date}">
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
    averagePace="5'25''"
    distance={$monthInfo.monthTotalDistance}
  />
{/if}

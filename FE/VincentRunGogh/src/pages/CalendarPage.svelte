<script lang="ts">
  import { onDestroy, onMount } from 'svelte';
  import { writable, get } from 'svelte/store';
  import FullCalendar from 'svelte-fullcalendar';
  import type { CalendarOptions } from 'svelte-fullcalendar';
  import daygridPlugin from '@fullcalendar/daygrid';
  import interactionPlugin from '@fullcalendar/interaction';
  import { Timeline, TimelineItem } from 'flowbite-svelte';
  import { CalendarWeekSolid } from 'flowbite-svelte-icons';

  import { getMonthData } from '@/api/myhealthApi';
  import Header from '@/components/common/Header.svelte';
  import { DrawingDetailContent, DrawingSummaryInfo } from '@components/calendar';

  import BottomSheet from '@components/common/BottomSheet.svelte';
  import { formatSecToH, formatDistanceFix2 } from '@/utils/formatter';
  import { getPace } from '@/utils/calculateFuc';

  type CalendarOptions = typeof CalendarOptions;

  let today = new Date();

  let monthInfo = writable<MonthInfo | null>(null);
  let selectedDayInfo = writable<DayInfo | null>(null);
  let selectedYear = writable<number | null>(null);
  let selectedMonth = writable<number | null>(null);
  let selectedDate = writable<string | null>(null);
  let options: CalendarOptions;

  let activeDrawing = writable(null); // 보여줄 드로잉의 세부 정보를 추적하기 위한 스토어

  function getEventColor(day) {
    if (day.isDrawing) {
      return 'var(--yellow-main-color)';
    } else if (day.isRunning) {
      return 'var(--green-main-color)';
    }
    return 'transparent';
  }

  function handleDateClick(info) {
    const dayData = get(monthInfo)?.dayList.find((day) => day.date === info.dateStr);
    selectedDate.set(info.dateStr);

    if (dayData) {
      selectedDayInfo.set(dayData);
    } else {
      selectedDayInfo.set(null); // 일치하는 날짜가 없을 경우 빈 객체를 설정
    }
  }
  let showModal = false;

  function showDrawingDetails(drawing) {
    activeDrawing.set(drawing);
    showModal = true;
    console.log('click');
  }

  function closeDrawingDetails() {
    activeDrawing.set(null);
    showModal = false;
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
    getMonthData(
      $selectedYear,
      $selectedMonth,
      (response) => {
        let data = response.data.data;
        monthInfo.set({ ...response.data.data });
        console.log(get(monthInfo));
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
        center: 'title', //,today,
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
      titleFormat: {
        // 연도와 월을 숫자로만 표시
        year: 'numeric', // YYYY 형식
        month: '2-digit'.split('.')[0], // MM 형식
      },
      selectable: true,
      showNonCurrentDates: false,
      fixedWeekCount: false,
    };
  }
</script>

<div class="flex flex-col items-center bg-bg-main h-screen">
  <Header title="캘린더" />
  <div id="calendar" class="h-3/5 w-[90vw] bg-white rounded-2xl">
    <FullCalendar {options} />
  </div>

  <div class=" w-[90vw] bg-white shadow-lg rounded-lg p-4 max-h-[25%] overflow-y-auto mt-4">
    {#if $selectedDayInfo}
      <div class="text-sm flex gap-4 flex-col">
        <h2 class="text-gray-500 mb-1 font-bold">
          {$selectedMonth}월 {$selectedDate?.split('-')[2]}일 운동
        </h2>
        <DrawingSummaryInfo
          time={formatSecToH($selectedDayInfo.dayTotalTime)}
          averagePace={getPace($selectedDayInfo.dayTotalDistance, $selectedDayInfo.dayTotalTime)}
          distance={formatDistanceFix2($selectedDayInfo.dayTotalDistance)}
          km
        />

        <ul class="list-disc pl-5">
          {#each $selectedDayInfo.drawingList as drawing}
            <div on:click={() => showDrawingDetails(drawing.drawingId)}>
              <Timeline order="vertical">
                <TimelineItem title={drawing.drawingName}>
                  <svelte:fragment slot="icon">
                    <span
                      class="flex absolute -start-3 justify-center items-center w-6 h-6 bg-primary-200 rounded-full ring-8 ring-white dark:ring-gray-900 dark:bg-primary-900"
                    >
                      <CalendarWeekSolid class="w-4 h-4 text-primary-600 dark:text-primary-400" />
                    </span>
                  </svelte:fragment>
                  <p class="mb-4 text-base font-normal text-gray-500 dark:text-gray-400">
                    {formatSecToH(drawing.drawingTime)}
                    {formatDistanceFix2(drawing.drawingDistance)} km
                  </p>
                </TimelineItem>
              </Timeline>
            </div>
          {/each}
        </ul>
      </div>
    {:else if $monthInfo}
      <div class="text-sm">
        <h2 class="text-gray-500 mb-1 font-bold">{$selectedYear}년 {$selectedMonth}월 통계</h2>
        <DrawingSummaryInfo
          time={formatSecToH($monthInfo.monthTotalTime)}
          averagePace={getPace($monthInfo.monthTotalDistance, $monthInfo.monthTotalTime)}
          distance={formatDistanceFix2($monthInfo.monthTotalDistance)}
        />
      </div>
    {/if}
  </div>
</div>
{#if showModal}
  <!-- drawing을 button에 해당하는 drawing으로 설정 -->
  <BottomSheet
    Component={DrawingDetailContent}
    props={{ drawingId: $activeDrawing, date: $selectedDate }}
    onClose={closeDrawingDetails}
  />
{/if}

<style>
  /* calendar global.css 내용 */
  .fc .fc-media-screen .fc-direction-ltr .fc-theme-standard {
    width: 80%;

    display: flex;
    justify-content: center;
    margin-top: 10vh;
    padding: 1rem;
  }
  .fc {
    height: 100%;
  }
  /* 네비게이션 폰트 설정 */
  .fc .fc-toolbar-title {
    font-weight: 800;
    font-size: 1rem;
  }

  /* 네비게이션 비활성화 됐을 때 스타일 */
  .fc-prev-button:disabled,
  .fc-next-button:disabled {
    background-color: white;
    color: var(--black-color-200,);
  }

  /* 요일 밑줄 제거 및 요일 글꼴 */
  .fc .fc-col-header-cell-cushion {
    font-weight: 800;
    font-size: 0.9rem;
    color: rgba(52, 72, 94, 0.54);
  }
  .fc .fc-daygrid-day-number {
    color: #7c86a2;
    text-align: center;
    font-size: 0.8rem;
    font-style: normal;
    font-weight: 500;
    line-height: normal;
  }
  /* 일요일에만 빨간 폰트 */
  .fc-day-sun {
    color: #ff4040;
    text-decoration: none;
  }
  /* 토요일 컬러 */
  .fc-day-sat a {
    color: #747bff;
  }

  #calendar .fc-day-today {
    background: none;
  }
  .fc-daygrid-day .fc-event-title {
    color: var(--blue-select-color, rgba(139, 162, 231));
    font-size: 0.7rem;
  }
  .fc .fc-daygrid-day:hover .fc-event-title,
  .fc .fc-daygrid-day:focus .fc-event-title,
  .fc .fc-daygrid-day.fc-day-selected .fc-event-title {
    color: var(--white-color-100) !important;
  }
  .fc-day-today .fc-event .fc-event-title {
    cursor: pointer;
    margin-bottom: 5px;
    border-radius: 55px;
    width: 30px !important;
    height: 10px;
    overflow: visible;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: row;
  }
  /* 선택한 날짜 스타일 적용 */
  .fc-daygrid-day:hover,
  .fc-daygrid-day:focus,
  .fc-daygrid-day.fc-day-selected {
    border-radius: 10px;
    background: var(--blue-select-color, rgba(139, 162, 231, 0.68)) !important;
  }
  #calendar .fc-button {
    background-color: transparent;
    color: #000000;
    border: none;
  }

  #calendar .fc-button-primary span {
    font-weight: 500;
    font-size: 28px;
  }

  /* 오늘 버튼 스타일 */
  .today-button {
    position: absolute;
    right: 7%;
    top: 6%;
    width: 18%;
    height: 1.5rem;
    text-align: center;
    line-height: 1.6rem;
    border-radius: 15px;
    font-size: 0.8rem;
    font-weight: 800;
  }

  #calendar .fc-daygrid-day-events {
    display: flex;
    justify-content: center;
    flex-direction: column;
    height: auto;
    width: 100%;
  }
  .fc-daygrid-event-harness {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  #calendar .fc-event {
    cursor: pointer;
    margin-bottom: 5px;
    border-radius: 55px;
    width: 10px;
    height: 10px;
  }

  .fc .fc-scrollgrid,
  .fc .fc-scrollgrid table {
    border: none;
  }
  .fc-theme-standard td,
  .fc-theme-standard th {
    border: none;
    padding-top: 3.5px;
    font-weight: 500;
    font-size: 16px;
    line-height: 19px;
  }

  /* 날짜 글자  */
  .fc .fc-daygrid-day-top {
    display: flex;
    justify-content: center;
  }
  .fc-daygrid-day-frame {
    padding: 2px; /* 패딩을 줄여 더 컴팩트하게 만듦 */
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    flex-direction: column;
  }
  .fc-basic-view .fc-body .fc-row {
    height: 1em;
  }
  .fc tr {
    line-height: 8px !important;
  }
  .fc td {
    height: unset !important;
  }

  .fc-direction-ltr .fc-toolbar > * > :not(:first-child) {
    margin: 0;
  }
</style>

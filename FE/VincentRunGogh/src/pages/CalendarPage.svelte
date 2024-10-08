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
    getMonthInfo();
  }
  const getMonthInfo = async () => {
    const year = get(selectedYear);
    const month = get(selectedMonth); // 선택된 월을 사용
    getMonthData(
      year,
      month,
      (response) => {
        let data = response.data.data;
        monthInfo.set({ ...response.data.data });
        console.log(get(monthInfo));
      },
      (error) => {}
    );
  };
  onMount(() => {
    const year = today.getFullYear();
    const month = today.getMonth() + 1; // JavaScript에서 월은 0부터 시작하므로 +1

    selectedYear.set(year);
    selectedMonth.set(month);
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
          time={`${formatSecToH($monthInfo.monthTotalTime)} hrs`}
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
</style>

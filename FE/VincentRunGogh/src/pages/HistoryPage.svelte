<script lang="ts">
  import { onMount } from 'svelte';
  import { Tabs, TabItem, Timeline, TimelineItem, Card, Button } from 'flowbite-svelte';
  import {
    PaletteOutline,
    FloppyDiskOutline,
    CalendarWeekSolid,
    ArrowRightOutline,
  } from 'flowbite-svelte-icons';
  import { link } from 'svelte-spa-router';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import FeedArticle from '@/components/cards/FeedArticle.svelte';
  import { getRouteList } from '@/api/routeApi';
  import { getDrawingList } from '@/api/drawingApi2';
  import Header from '@/components/common/Header.svelte';
  import { formatToKoreanTime } from '@/utils/formatter';

  let routeArticle = [];
  let drawingAriticle = [];

  onMount(async () => {
    let routeListResponse = await getRouteList({ type: 'mine', lng: 0, lat: 0 });
    routeArticle = routeListResponse.data.routeList;

    let drawingListResponse = await getDrawingList('done');
    drawingAriticle = drawingListResponse.data.findDrawingList;
  });
</script>

<div id="community-body">
  <Header title="히스토리" />
  <Tabs defaultClass="flex justify-between" tabStyle="underline">
    <TabItem defaultClass="tab-item font-bold text-xs gap-2" open>
      <div slot="title" class="flex items-center gap-1">
        <PaletteOutline size="md" />
        내가 만든 루트
      </div>
      <div id="community-content">
        {#each routeArticle as article}
          <Card class="mb-5">
            <FeedArticle
              title={article.title}
              artImage={article.artImage}
              distance={article.distance}
              time={article.predictTime}
              drawingImage={null}
            />
          </Card>
          <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
            {article.comment}
          </p>
        {/each}
      </div>
    </TabItem>
    <TabItem defaultClass="tab-item font-bold text-xs gap-2">
      <div slot="title" class="flex items-center gap-1">
        <FloppyDiskOutline size="md" />
        완료한 드로잉
      </div>
      {#each drawingAriticle as article}
        <!-- <a use:link href="/drawingdetail?id={article.drawingId}&date={article.date}">
          <li>
            {article.title} - Time: {article.updated}
          </li>
        </a> -->
        <Timeline order="vertical">
          <TimelineItem title={article.title} date={formatToKoreanTime(article.updated, true)}>
            <svelte:fragment slot="icon">
              <span
                class="flex absolute -start-3 justify-center items-center w-6 h-6 bg-primary-200 rounded-full ring-8 ring-white dark:ring-gray-900 dark:bg-primary-900"
              >
                <CalendarWeekSolid class="w-4 h-4 text-primary-600 dark:text-primary-400" />
              </span>
            </svelte:fragment>
            <p class="mb-4 text-base font-normal text-gray-500 dark:text-gray-400">
              Get access to over 20+ pages including a dashboard layout, charts, kanban board,
              calendar, and pre-order E-commerce & Marketing pages.
            </p>
            <Button color="alternative">Learn more<ArrowRightOutline class="ms-2 w-5 h-5" /></Button
            >
          </TimelineItem>
        </Timeline>
      {/each}
    </TabItem>
  </Tabs>
</div>

<style>
  #community-body {
    justify-self: center;
    text-align: center;
    width: 100%;
    max-width: 1024px;
    margin: 0;
    background: var(--white-bg-color, #f9f8ef);
    display: flex;
    flex-direction: column;
  }

  #community-header {
    height: 10vh;
    width: 100%;
  }

  #community-mystorage {
    width: 15%;
  }

  #community-content {
    height: 75vh;
    width: 100%;
    overflow-y: scroll;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  #community-content::-webkit-scrollbar {
    display: none;
  }

  #community-tabbar {
    height: 15vh;
  }
</style>

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
  import { getDrawingList } from '@/api/drawingApi';
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

<Header title="히스토리" />
<div id="mystorage-body">
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <!-- svelte-ignore a11y-interactive-supports-focus -->

  <Tabs id="tabs" defaultClass="flex justify-between p-0" tabStyle="underline">
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        divClass="flex flex-col justify-center p-0 w-80"
        open
      >
        <div slot="title" class="flex items-center gap-1">
          <PaletteOutline size="sm" />
          <p>내가 만든 루트</p>
        </div>
        <div id="mystorage-content" class="space-y-4" on:touchmove>
          {#if routeArticle.length === 0}
            <h1>내가 만든 루트가 없습니다!</h1>
            <p class="text-gray-700 dark:text-gray-400 text-xl">나만의 MAP ART를 만들어보세요</p>
          {:else}
            {#each routeArticle as article}
              <div class="mb-3 relative z-10 text-center overflow-y-auto">
                <Card size="sm" class="mb-5 p-1 grow">
                  <FeedArticle
                    title={article.title}
                    artImage={article.artImage}
                    distance={article.distance}
                    time={article.predictTime}
                    drawingImage={null}
                  />
                </Card>
              </div>
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        divClass="flex flex-col justify-center p-0 w-80"
      >
        <div slot="title" class="flex items-center gap-1">
          <FloppyDiskOutline size="sm" />
          완료한 드로잉
        </div>
        <div id="mystorage-content" class="space-y-4" on:touchmove>
          {#if drawingAriticle.length === 0}
            <h1>완료한 드로잉이 없습니다!</h1>
          {:else}
            {#each drawingAriticle.reverse() as article}
              <div class="mb-3 relative z-10 overflow-y-auto">
                <Timeline order="vertical" class="flex justify-between m-4">
                  <TimelineItem
                    title={article.title}
                    date={formatToKoreanTime(article.updated, true)}
                  >
                    <svelte:fragment slot="icon">
                      <span class="flex absolute justify-center items-center w-6 h-6 -start-3">
                        <CalendarWeekSolid class="w-4 h-4 text-primary-600 dark:text-primary-400" />
                      </span>
                    </svelte:fragment>

                    <Button color="alternative"
                      >상세 보기<ArrowRightOutline class="ms-2 w-5 h-5" /></Button
                    >
                  </TimelineItem>
                  <div class="w-[30vw]">
                    <img src={article.drawingImage} alt="" />
                  </div>
                </Timeline>
              </div>
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
  </Tabs>
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

<style>
  #mystorage-body {
    width: 100%;
    height: 90%;
    padding-bottom: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    overflow-y: scroll;
    z-index: 2;
  }

  #mystorage-body::-webkit-scrollbar {
    display: none;
  }

  #search-control {
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 10vh;
    z-index: 2;
  }

  #community-mystorage {
    width: 15%;
    z-index: 20;
  }

  #mystorage-content {
    height: auto;
    flex-grow: 1;
    overflow-y: scroll;
    z-index: 2;
  }

  #mystorage-content::-webkit-scrollbar {
    display: none;
  }

  #tab-item {
    z-index: 2 !important;
  }

  #background {
    position: fixed;
    bottom: 2%;
    left: 25%;
    transform: translateX(-50%);
    z-index: 1;
    opacity: 0.3;
    overflow: visible;
    transition: 800ms;
  }

  #background2 {
    position: fixed;
    top: 9%;
    right: -50%;
    transform: translateX(-50%);
    z-index: 1;
    opacity: 0.3;
    overflow: visible;
    transition: 800ms;
  }
</style>

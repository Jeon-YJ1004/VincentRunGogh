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

<div class="bg-bg-main h-screen">
  <Header title="히스토리" />
  <Tabs defaultClass="flex justify-around" tabStyle="underline">
    <TabItem defaultClass="tab-item font-bold text-xs gap-2" open>
      <div slot="title" class="flex items-center gap-1">
        <PaletteOutline size="md" />
        내가 만든 루트
      </div>
      <div id="community-content">
        {#if routeArticle.length > 0}
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
          {/each}
        {:else}
          <span class="gap-4 flex items-center flex-col justify-center mt-60">
            <p class="text-gray-700 dark:text-gray-400 text-4xl">내가 만든 루트가 없습니다</p>
            <p class="text-gray-700 dark:text-gray-400 text-xl">나만의 MAP ART를 만들어보세요</p>
          </span>
        {/if}
      </div>
    </TabItem>
    <TabItem defaultClass="tab-item font-bold text-xs gap-2">
      <div slot="title" class="flex items-center gap-1">
        <FloppyDiskOutline size="md" />
        완료한 드로잉
      </div>
      <div class="gap-8 m-4">
        {#each drawingAriticle as article}
          <Timeline order="vertical" class="flex justify-between m-4">
            <TimelineItem title={article.title} date={formatToKoreanTime(article.updated, true)}>
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
        {/each}
      </div>
    </TabItem>
  </Tabs>
</div>

<style>
  #community-body {
    justify-content: center;
    text-align: center;
    width: 100%;
    max-width: 1024px;
    margin: 0;
    background: var(--white-bg-color, #f9f8ef);
    display: flex;
    flex-direction: column;
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

  #community-content {
    height: 68vh;
    width: 90%;
    margin-left: 5%;
    overflow-y: scroll;
    display: flex;
    flex-direction: column;
    align-items: center;
    z-index: 2;
  }

  #community-content::-webkit-scrollbar {
    display: none;
  }

  #community-tabbar {
    height: 12vh;
    z-index: 2;
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

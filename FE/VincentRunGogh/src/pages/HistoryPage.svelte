<script lang="ts">
  import { onMount } from 'svelte';
  import { Tabs, TabItem, Group, GroupItem } from 'flowbite-svelte';
  import { PaletteOutline, FloppyDiskOutline } from 'flowbite-svelte-icons';
  import { link } from 'svelte-spa-router';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import FeedArticle from '@/components/cards/FeedArticle.svelte';
  import { getRouteList } from '@/api/routeApi';
  import { getDrawingList } from '@/api/drawingApi2';
  let routeArticle: {
    id: number;
    nickname: string;
    profile: string;
    drawingImage: string;
    artImage: string;
    comment: string;
    likeCount: number;
    isLiked: boolean;
    distance: number;
    time: number;
    created: any;
    distanceFromUser: number;
  }[] = [];
  let drawingAriticle: {}[] = [];
  let dummyLikedList = [
    {
      id: 21,
      nickname: 'test1',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '런닝 후 상쾌해요!',
      likeCount: 43,
      isLiked: true,
      distance: 5000,
      time: 1200,
      created: null,
      distanceFromUser: 1200,
    },
    {
      id: 22,
      nickname: 'test1',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '오늘도 완주 성공!',
      likeCount: 67,
      isLiked: true,
      distance: 7000,
      time: 3400,
      created: null,
      distanceFromUser: 3500,
    },
    {
      id: 23,
      nickname: 'test2',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '조금 힘들었어요',
      likeCount: 25,
      isLiked: true,
      distance: 3000,
      time: 2000,
      created: null,
      distanceFromUser: 2200,
    },
    {
      id: 24,
      nickname: 'test2',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '기록 갱신!',
      likeCount: 89,
      isLiked: true,
      distance: 8000,
      time: 4500,
      created: null,
      distanceFromUser: 1800,
    },
    {
      id: 25,
      nickname: 'test3',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '날씨가 좋았어요',
      likeCount: 53,
      isLiked: true,
      distance: 6000,
      time: 3200,
      created: null,
      distanceFromUser: 4100,
    },
    {
      id: 26,
      nickname: 'test3',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '조금 더 뛸걸!',
      likeCount: 32,
      isLiked: true,
      distance: 4000,
      time: 2800,
      created: null,
      distanceFromUser: 3000,
    },
    {
      id: 27,
      nickname: 'test4',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '즐거운 시간!',
      likeCount: 75,
      isLiked: true,
      distance: 9000,
      time: 5100,
      created: null,
      distanceFromUser: 3600,
    },
    {
      id: 28,
      nickname: 'test4',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '다음엔 더 잘할래요!',
      likeCount: 22,
      isLiked: true,
      distance: 4500,
      time: 3700,
      created: null,
      distanceFromUser: 2800,
    },
    {
      id: 29,
      nickname: 'test5',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '너무 힘들어요',
      likeCount: 48,
      isLiked: true,
      distance: 3500,
      time: 2900,
      created: null,
      distanceFromUser: 4900,
    },
    {
      id: 30,
      nickname: 'test5',
      profile: '/default.png',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FF7ED0',
      comment: '잘 뛴 것 같아요',
      likeCount: 16,
      isLiked: true,
      distance: 1000,
      time: 350,
      created: null,
      distanceFromUser: 1500,
    },
  ];

  onMount(() => {
    getRouteList({ type: 'mine' }).then((response) => {
      routeArticle = response.data;
      console.log(response.data);
    });
    getDrawingList('done').then((response) => {
      drawingAriticle = response.data.findDrawingList;
    });
  });
</script>

<div id="community-body">
  <div id="community-header" class="flex justify-center items-center">
    <BackButton />
    <h2>히스토리</h2>
  </div>

  <div id="community-content">
    <Tabs defaultClass="flex justify-between" tabStyle="underline">
      <!-- api 연결시 여기에 on:click 해야함 just like RouteListPage -->
      <TabItem defaultClass="tab-item font-bold text-xs gap-2" open>
        <div slot="title" class="flex items-center gap-1">
          <PaletteOutline size="md" />
          내가 만든 루트
        </div>
        {#each routeArticle as article}
          <!-- //FIXME - 피트아티클 프롭스 다시 넣기 -->
          <FeedArticle {article} />
          <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
            {article.comment}
          </p>
        {/each}
      </TabItem>
      <TabItem defaultClass="tab-item font-bold text-xs gap-2">
        <div slot="title" class="flex items-center gap-1">
          <FloppyDiskOutline size="md" />
          완료한 드로잉
        </div>
        <Group date="January 13th, 2022">
          <GroupItem timelines={groupTimelines} />
        </Group>
        {#each drawingAriticle as article}
          <a use:link href="/drawingdetail?id={article.drawingId}&date={article.date}">
            <li>
              {article.title} - Time: {article.updated}
              <!-- , Distance: {article.drawingDistance} -->
            </li>
          </a>
        {/each}
      </TabItem>
    </Tabs>
  </div>
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

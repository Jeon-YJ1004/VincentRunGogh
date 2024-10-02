<script lang="ts">
  import Tabbar from '@/components/common/Tabbar.svelte';
  import { Card, Button, Carousel, Tabs, TabItem, Avatar } from 'flowbite-svelte';
  import { replace } from 'svelte-spa-router';
  import { PaletteOutline, HeartSolid, TrashBinSolid, HeartOutline } from 'flowbite-svelte-icons';
  import Swal from 'sweetalert2';
  import BackButton from '@/components/buttons/BackButton.svelte';
  import FeedArticle from '@/components/cards/FeedArticle.svelte';
  import Header from '@/components/common/Header.svelte';

  let dummyArticleList: {
    boardId: number;
    nickname: string;
    profile: string;
    title: string;
    drawingImage: string;
    artImage: string;
    comment: string;
    likeCount: number;
    isLiked: boolean;
    distance: number;
    time: number;
    created: any;
    distanceFromUser: number;
  }[] = [
    {
      boardId: 1,
      nickname: 'test1',
      profile: '/default.png',
      title: '런닝으로 스트레스 해소하기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '정말 기분이 좋았어요!',
      likeCount: 23,
      isLiked: true,
      distance: 4500,
      time: 3600,
      created: new Date(2024, 0, 5),
      distanceFromUser: 3000,
    },
    {
      boardId: 2,
      nickname: 'test2',
      profile: '/default.png',
      title: '하루 5km 달리기 도전!',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '정말 힘들었지만 재밌어요!',
      likeCount: 57,
      isLiked: false,
      distance: 3200,
      time: 5400,
      created: new Date(2024, 2, 15),
      distanceFromUser: 4500,
    },
    {
      boardId: 3,
      nickname: 'test1',
      profile: '/default.png',
      title: '런닝으로 건강한 삶 만들기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '매일 조금씩 달려요!',
      likeCount: 34,
      isLiked: true,
      distance: 2500,
      time: 4800,
      created: new Date(2024, 3, 20),
      distanceFromUser: 5200,
    },
    {
      boardId: 4,
      nickname: 'test2',
      profile: '/default.png',
      title: '런닝으로 에너지 충전하기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '이 기분이 좋네요!',
      likeCount: 12,
      isLiked: false,
      distance: 6700,
      time: 7200,
      created: new Date(2024, 5, 10),
      distanceFromUser: 6100,
    },
    {
      boardId: 5,
      nickname: 'test1',
      profile: '/default.png',
      title: '달리기로 시작하는 아침',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '하루의 시작은 달리기!',
      likeCount: 40,
      isLiked: true,
      distance: 5400,
      time: 3000,
      created: new Date(2024, 6, 25),
      distanceFromUser: 7800,
    },
    {
      boardId: 6,
      nickname: 'test2',
      profile: '/default.png',
      title: '즐거운 런닝 클래스',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '함께 달리니 더 재밌어요!',
      likeCount: 85,
      isLiked: true,
      distance: 8900,
      time: 1500,
      created: new Date(2024, 7, 18),
      distanceFromUser: 6500,
    },
    {
      boardId: 7,
      nickname: 'test1',
      profile: '/default.png',
      title: '산책처럼 가벼운 달리기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '부담 없이 달려요!',
      likeCount: 77,
      isLiked: false,
      distance: 2900,
      time: 7200,
      created: new Date(2024, 1, 12),
      distanceFromUser: 9000,
    },
    {
      boardId: 8,
      nickname: 'test2',
      profile: '/default.png',
      title: '친구와 함께하는 런닝',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '함께하면 더 즐거워요!',
      likeCount: 16,
      isLiked: true,
      distance: 4800,
      time: 3600,
      created: new Date(2024, 4, 28),
      distanceFromUser: 7200,
    },
    {
      boardId: 9,
      nickname: 'test1',
      profile: '/default.png',
      title: '매일 10분 런닝 도전!',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '작은 목표부터 시작해요!',
      likeCount: 39,
      isLiked: false,
      distance: 3100,
      time: 9000,
      created: new Date(2024, 8, 5),
      distanceFromUser: 4000,
    },
    {
      boardId: 10,
      nickname: 'test2',
      profile: '/default.png',
      title: '새로운 길에서 런닝',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '새로운 장소에서의 달리기!',
      likeCount: 60,
      isLiked: true,
      distance: 7100,
      time: 2000,
      created: new Date(2024, 2, 25),
      distanceFromUser: 5300,
    },
  ];

  let dummyLikedList: {
    boardId: number;
    nickname: string;
    profile: string;
    title: string;
    drawingImage: string;
    artImage: string;
    comment: string;
    likeCount: number;
    isLiked: boolean;
    distance: number;
    time: number;
    created: any;
    distanceFromUser: number;
  }[] = [
    {
      boardId: 1,
      nickname: 'test1',
      profile: '/default.png',
      title: '런닝으로 스트레스 해소하기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '정말 기분이 좋았어요!',
      likeCount: 23,
      isLiked: true,
      distance: 4500,
      time: 3600,
      created: new Date(2024, 0, 5),
      distanceFromUser: 3000,
    },
    {
      boardId: 2,
      nickname: 'test2',
      profile: '/default.png',
      title: '하루 5km 달리기 도전!',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '정말 힘들었지만 재밌어요!',
      likeCount: 57,
      isLiked: false,
      distance: 3200,
      time: 5400,
      created: new Date(2024, 2, 15),
      distanceFromUser: 4500,
    },
    {
      boardId: 3,
      nickname: 'test1',
      profile: '/default.png',
      title: '런닝으로 건강한 삶 만들기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '매일 조금씩 달려요!',
      likeCount: 34,
      isLiked: true,
      distance: 2500,
      time: 4800,
      created: new Date(2024, 3, 20),
      distanceFromUser: 5200,
    },
    {
      boardId: 4,
      nickname: 'test2',
      profile: '/default.png',
      title: '런닝으로 에너지 충전하기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '이 기분이 좋네요!',
      likeCount: 12,
      isLiked: false,
      distance: 6700,
      time: 7200,
      created: new Date(2024, 5, 10),
      distanceFromUser: 6100,
    },
    {
      boardId: 5,
      nickname: 'test1',
      profile: '/default.png',
      title: '달리기로 시작하는 아침',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '하루의 시작은 달리기!',
      likeCount: 40,
      isLiked: true,
      distance: 5400,
      time: 3000,
      created: new Date(2024, 6, 25),
      distanceFromUser: 7800,
    },
    {
      boardId: 6,
      nickname: 'test2',
      profile: '/default.png',
      title: '즐거운 런닝 클래스',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '함께 달리니 더 재밌어요!',
      likeCount: 85,
      isLiked: true,
      distance: 8900,
      time: 1500,
      created: new Date(2024, 7, 18),
      distanceFromUser: 6500,
    },
    {
      boardId: 7,
      nickname: 'test1',
      profile: '/default.png',
      title: '산책처럼 가벼운 달리기',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '부담 없이 달려요!',
      likeCount: 77,
      isLiked: false,
      distance: 2900,
      time: 7200,
      created: new Date(2024, 1, 12),
      distanceFromUser: 9000,
    },
    {
      boardId: 8,
      nickname: 'test2',
      profile: '/default.png',
      title: '친구와 함께하는 런닝',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '함께하면 더 즐거워요!',
      likeCount: 16,
      isLiked: true,
      distance: 4800,
      time: 3600,
      created: new Date(2024, 4, 28),
      distanceFromUser: 7200,
    },
    {
      boardId: 9,
      nickname: 'test1',
      profile: '/default.png',
      title: '매일 10분 런닝 도전!',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '작은 목표부터 시작해요!',
      likeCount: 39,
      isLiked: false,
      distance: 3100,
      time: 9000,
      created: new Date(2024, 8, 5),
      distanceFromUser: 4000,
    },
    {
      boardId: 10,
      nickname: 'test2',
      profile: '/default.png',
      title: '새로운 길에서 런닝',
      drawingImage: 'http://via.placeholder.com/150x150',
      artImage: 'http://via.placeholder.com/150x150/FFFF00',
      comment: '새로운 장소에서의 달리기!',
      likeCount: 60,
      isLiked: true,
      distance: 7100,
      time: 2000,
      created: new Date(2024, 2, 25),
      distanceFromUser: 5300,
    },
  ];

  // 게시글 검색 조건
  let range: number = 5;

  function searchCondition() {
    Swal.fire({
      title: '검색 범위를 조절해주세요!',
      html: `
    <input type="range" min="1" max="10" step="1" class="w-full h-2 bg-gray-200 rounded-lg cursor-pointer" id="range-input" value="${range}">
    <div id="range-value">반경 ${range}km 까지 검색합니다.</div>
  `,
      showCancelButton: true,
      confirmButtonText: '확인',
      cancelButtonText: '취소',
      preConfirm: () => {
        return range; // 범위 값을 반환
      },
      didOpen: () => {
        const input = document.getElementById('range-input') as HTMLInputElement;
        const rangeValue = document.getElementById('range-value');

        // 범위 값 업데이트 이벤트 리스너
        input.addEventListener('input', () => {
          range = Number(input.value); // 범위 값 업데이트
          if (rangeValue) {
            rangeValue.innerText = `반경 ${range}km 까지 검색합니다.`; // 문구 업데이트
          }
        });
      },
    }).then((result) => {
      if (result.isConfirmed) {
        console.log(`선택된 범위: ${range}`); // 확인 시 범위 값 출력
        // 여기서 range 변수를 사용하여 추가 로직을 구현
      }
    });
  }

  // 게시글 삭제
  async function deleteArticle(article: object) {
    // 삭제여부 모달
    Swal.fire({
      title: '정말로 삭제하시겠습니까?',
      text: '삭제 후 복구가 불가능합니다.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
    }).then((result) => {
      // 여기서 API 삭제
      if (result.isConfirmed) {
        Swal.fire({
          title: '삭제 완료!',
          text: '등록한 게시글을 삭제하였습니다',
          icon: 'success',
        });
      }
    });
  }

  // 좋아요버튼 클릭
  async function switchLiked(article: any) {
    // 현재 좋아요 상태를 반전시키고 새로운 객체로 대체
    const updatedArticle = { ...article, isLiked: !article.isLiked };

    // dummyArticleList에서 해당 article 업데이트
    const articleIndex = dummyArticleList.findIndex((dummy) => dummy.id === article.boardId);

    if (articleIndex !== -1) {
      dummyArticleList[articleIndex] = updatedArticle; // 배열 내 객체 교체
    }

    // API 요청하여 좋아요 처리하기
    // 예: await api.likeArticle(updatedArticle);
  }
</script>

<div id="mystorage-body">
  <Header title="내 보관함" />
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <!-- svelte-ignore a11y-interactive-supports-focus -->
  <div id="community-mystorage" role="button" class="absolute top-3 right-3">
    <Avatar dot={{ color: 'green' }} />
  </div>
  <div id="search-control">
    <Button size="sm" on:click={searchCondition}>검색 반경 설정</Button>
    <p class="my-3 font-bold">반경 {range}km내의 루트만 표시됩니다.</p>
  </div>
  <div id="mystorage-content">
    <Tabs defaultClass="flex justify-between" tabStyle="underline">
      <!-- api 연결시 여기에 on:click 해야함 just like RouteListPage -->
      <TabItem defaultClass="tab-item font-bold text-xs gap-2" open>
        <div slot="title" class="flex items-center gap-1">
          <PaletteOutline size="md" />
          게시한 루트
        </div>
        {#each dummyArticleList as article}
          {#if article.distanceFromUser <= range * 1000}
            <Card size="sm" class="mb-5 p-1 grow">
              <div class="flex mb-3 items-center justify-between">
                <img src={article.profile} alt="" style="width: 50px;" />
                <p class="ml-5">{article.nickname}</p>
                <button on:click={() => deleteArticle(article)}>
                  <TrashBinSolid size="md" color="red" />
                </button>
              </div>
              <FeedArticle
                title={article.title}
                artImage={article.artImage}
                drawingImage={article.drawingImage}
                distance={article.distance}
                time={article.time}
              />
              <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
                {article.comment}
              </p>
              <div class="flex justify-between items-center">
                <div class="flex justify-between items-center">
                  {#if article.isLiked}
                    <button
                      on:click={() => switchLiked(article)}
                      color={article.isLiked ? 'red' : 'blue'}
                    >
                      <HeartSolid size="lg" color={'red'} />
                    </button>
                    <p>{article.likeCount}</p>
                  {:else}
                    <button
                      on:click={() => switchLiked(article)}
                      color={article.isLiked ? 'red' : 'blue'}
                    >
                      <HeartOutline size="lg" />
                    </button>
                    <p>{article.likeCount}</p>
                  {/if}
                </div>
              </div>
            </Card>
          {/if}
        {/each}
      </TabItem>
      <TabItem defaultClass="tab-item font-bold text-xs gap-2">
        <div slot="title" class="flex items-center gap-1">
          <HeartSolid size="md" />
          찜한 루트
        </div>
        {#each dummyLikedList as article}
          {#if article.distanceFromUser <= range * 1000}
            <Card size="sm" class="mb-5 p-1 grow">
              <div class="flex mb-3 items-center justify-between">
                <img src={article.profile} alt="" style="width: 50px;" />
                <p class="ml-5">{article.nickname}</p>
              </div>
              <FeedArticle
                title={article.title}
                artImage={article.artImage}
                drawingImage={article.drawingImage}
                distance={article.distance}
                time={article.time}
              />
              <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
                {article.comment}
              </p>
              <div class="flex justify-between items-center">
                <div class="flex justify-between items-center">
                  {#if article.isLiked}
                    <button
                      on:click={() => switchLiked(article)}
                      color={article.isLiked ? 'red' : 'blue'}
                    >
                      <HeartSolid size="lg" color={'red'} />
                    </button>
                    <p>{article.likeCount}</p>
                  {:else}
                    <button
                      on:click={() => switchLiked(article)}
                      color={article.isLiked ? 'red' : 'blue'}
                    >
                      <HeartOutline size="lg" />
                    </button>
                    <p>{article.likeCount}</p>
                  {/if}
                </div>
              </div>
            </Card>
          {/if}
        {/each}
      </TabItem>
    </Tabs>
  </div>
</div>

<style>
  #mystorage-body {
    justify-self: center;
    text-align: center;
    width: 100%;
    max-width: 1024px;
    margin: 0;
    background: var(--white-bg-color, #f9f8ef);
    display: flex;
    flex-direction: column;
  }

  #community-mystorage {
    width: 15%;
  }

  #mystorage-content {
    height: 90vh;
    width: 100%;
    overflow-y: scroll;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  #mystorage-content::-webkit-scrollbar {
    display: none;
  }
</style>

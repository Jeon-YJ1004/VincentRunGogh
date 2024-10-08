<script lang="ts">
  import { Card, Button, Carousel, Tabs, TabItem, Avatar } from 'flowbite-svelte';
  import {
    PaletteOutline,
    HeartSolid,
    TrashBinSolid,
    HeartOutline,
    MapPinSolid,
    ChevronDoubleUpOutline,
    UploadSolid,
  } from 'flowbite-svelte-icons';
  import Swal from 'sweetalert2';
  import FeedArticle from '@/components/cards/FeedArticle.svelte';
  import Header from '@/components/common/Header.svelte';
  import { userStore } from '@/stores/userStore';
  import { get } from 'svelte/store';
  import { deleteArticle, dislikeArticle, getArticleList, likeArticle } from '@/api/communityApi';
  import { getDrawingList } from '@/api/drawingApi';
  import { onMount } from 'svelte';
  import PostArticle from '@/components/modals/PostArticle.svelte';

  let articleList: {
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
    created: Date;
    distanceFromUser: number;
  }[] = [];

  let unpostArticles: {
    drawingId: number;
    title: string;
    artImage: string;
    drawingImage: string;
    updated: string;
  }[] = [];

  // 유저정보 가져오기
  userStore.initialize();
  let userInfo = get(userStore);
  console.log(userInfo);

  // 내 위치 가져오기
  let currentLat: number = 0;
  let currentLng: number = 0;

  function handlePosition(position: GeolocationPosition) {
    currentLat = position.coords.latitude;
    currentLng = position.coords.longitude;
    console.log(currentLat, currentLng);
  }

  function failPosition() {
    console.log('geolocation api error');
  }

  const positionOption = {
    enableHighAccuracy: true,
    timeout: 5000, // 5 seconds
    maximumAge: 60000, // 1 minute
  };

  // 게시글 검색 조건
  let range: number = 5;

  function searchCondition() {
    Swal.fire({
      title: "<div class='text-lg'>" + '검색 범위를 조절해주세요!' + '</div>',
      html: `
    <input type="range" min="1" max="10" step="1" class="w-full h-2 bg-gray-200 rounded-lg cursor-pointer" id="range-input" value="${range}">
    <div id="range-value">반경 ${range}km 까지 검색합니다.</div>
  `,
      showCancelButton: true,
      confirmButtonText: '확인',
      confirmButtonColor: '#FFB800',
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
      }
    });
  }

  // 게시물 조회하기

  // 내 게시물
  async function getMyArticleInfo() {
    let articleListParams: {
      lat: number;
      lng: number;
      type: string;
    } = {
      lat: currentLat,
      lng: currentLng,
      type: 'mine',
    };

    let responseAllArticle = await getArticleList(articleListParams);
    articleList = responseAllArticle.data.boardList;
  }

  // 찜한 게시물
  async function getLikedArticleInfo() {
    let articleListParams: {
      lat: number;
      lng: number;
      type: string;
    } = {
      lat: currentLat,
      lng: currentLng,
      type: 'myliked',
    };

    let responseAllArticle = await getArticleList(articleListParams);
    articleList = responseAllArticle.data.boardList;
  }

  // 아직 안올린 게시물
  async function getUnpostArticleInfo() {
    let responseAllArticle = await getDrawingList('community');
    unpostArticles = responseAllArticle.data.findDrawingList.slice().reverse();
  }

  // 게시글 삭제

  async function clickDeleteArticle(boardId: number) {
    // 삭제여부 모달
    Swal.fire({
      title: "<div class='text-xl'>" + '정말로 삭제하시겠습니까?' + '</div>',
      text: '삭제 후 복구가 불가능합니다.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#697386',
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      preConfirm: () => {
        deleteArticle(boardId)
          .then(() => {
            articleList = articleList.filter((article) => article.boardId != boardId);
            Swal.fire({
              title: '삭제 완료!',
              text: '등록한 게시글을 삭제하였습니다',
              icon: 'success',
              confirmButtonColor: '#FFB800',
            });
          })
          .catch((error) => {
            console.error(error);
            Swal.fire({
              icon: 'error',
              title: '오류가 발생했습니다',
              text: '루트를 저장하는 도중 문제가 발생했습니다.',
            });
          });
      },
    });
  }

  // 좋아요버튼 클릭
  async function clickLiked(isLiked: boolean, boardId: number) {
    if (isLiked) {
      // 이미 좋아요상태 -> 취소
      await dislikeArticle(boardId);
      let index = articleList.findIndex((article) => article.boardId === boardId);
      articleList[index].isLiked = !articleList[index].isLiked;
      articleList[index].likeCount--;
    } else {
      // 좋아요상태가 아님 -> 등록
      await likeArticle(boardId);
      let index = articleList.findIndex((article) => article.boardId === boardId);
      articleList[index].isLiked = !articleList[index].isLiked;
      articleList[index].likeCount++;
    }
  }

  // 게시글 생성 버튼 클릭
  function clickPost(article: {
    drawingId: number;
    title: string;
    artImage: string;
    drawingImage: string;
    updated: string;
  }) {
    Swal.fire({
      html: '<div id="post-article"></div>',
      showConfirmButton: false,
      allowOutsideClick: false,
      didOpen: () => {
        // 'route-detail'라는 ID를 가진 div에 Svelte 컴포넌트 렌더링
        new PostArticle({
          target: document.getElementById('post-article'),
          props: {
            article: article,
            onClose: () => {
              unpostArticles = unpostArticles.filter((one) => one.drawingId != article.drawingId);
              Swal.close(); // 모달 닫기
            },
          },
        });
      },
    });
  }

  onMount(() => {
    // 현재 위치 지정
    window.navigator.geolocation.getCurrentPosition(
      (position) => {
        // 성공 시 현재 위치를 처리
        handlePosition(position);
        getMyArticleInfo();
      },
      failPosition, // 위치 가져오기 실패 시 처리
      positionOption
    );
  });

  //맨 위로 스크롤
  function scrollToTop(target: string) {
    let targetDiv = document.querySelector(target);
    targetDiv.scrollTo({
      top: 0,
      behavior: 'smooth', // 부드럽게 스크롤
    });
  }
</script>

<Header title="내 보관함" to={'/community'} />
<div id="mystorage-body">
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <!-- svelte-ignore a11y-interactive-supports-focus -->
  <div id="community-mystorage" role="button" class="absolute top-5 right-3">
    <Avatar src={userInfo.profile} dot={{ color: 'green' }} />
  </div>
  <div id="search-control">
    <Button size="sm" on:click={searchCondition}>검색 반경 설정</Button>
    <p class="my-3 font-bold">반경 {range}km내의 루트만 표시됩니다.</p>
  </div>
  <Tabs id="tabs" defaultClass="flex justify-between p-0" tabStyle="underline">
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        divClass="flex flex-col justify-center p-0 w-80"
        on:click={getMyArticleInfo}
        open
      >
        <div slot="title" class="flex items-center gap-1">
          <PaletteOutline size="sm" />
          <p>내 게시물</p>
        </div>
        <div id="mystorage-content" class="space-y-4" on:touchmove>
          {#if articleList.length === 0}
            <h1>등록한 게시물이 없습니다!</h1>
          {:else}
            {#each articleList as article}
              <div class="mb-3 relative z-10 text-center">
                <Card size="sm" class="mb-5 p-1 grow">
                  <div class="flex ms-1 mt-1 mb-3 items-center text-center">
                    <img
                      src={article.profile}
                      alt=""
                      style="width: 50px; height: 50px; border-radius:50%;"
                    />
                    <p class="ml-5">{article.nickname}</p>
                    <button
                      class="absolute top-5 right-3"
                      on:click={() => clickDeleteArticle(article.boardId)}
                    >
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
                  <p class="mt-2 mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
                    {article.comment}
                  </p>
                  <div class="me-2 flex justify-end items-center">
                    <div class="flex justify-between items-center">
                      {#if article.isLiked}
                        <button on:click={() => clickLiked(article.isLiked, article.boardId)}>
                          <HeartSolid size="lg" color={'red'} />
                        </button>
                        <p>{article.likeCount}</p>
                      {:else}
                        <button on:click={() => clickLiked(article.isLiked, article.boardId)}>
                          <HeartOutline size="lg" />
                        </button>
                        <p>{article.likeCount}</p>
                      {/if}
                    </div>
                  </div>
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
        on:click={getLikedArticleInfo}
      >
        <div slot="title" class="flex items-center gap-1">
          <HeartSolid size="sm" />
          찜한 루트
        </div>
        <div id="mystorage-content" class="space-y-4" on:touchmove>
          {#if articleList.filter((article) => article.distanceFromUser <= range).length === 0}
            <h1>찜한 루트가 아직 없습니다!</h1>
          {:else}
            {#each articleList as article}
              {#if article.distanceFromUser <= range}
                <div class="mb-3 relative z-10 text-center">
                  <Card size="sm" class="mb-5 p-1 grow">
                    <div class="flex ms-1 mt-1 mb-3 items-center text-center">
                      <img
                        src={article.profile}
                        alt=""
                        style="width: 50px;  height: 50px; border-radius: 50%;"
                      />
                      <p class="ml-5">{article.nickname}</p>
                    </div>
                    <FeedArticle
                      title={article.title}
                      artImage={article.artImage}
                      drawingImage={article.drawingImage}
                      distance={article.distance}
                      time={article.time}
                    />
                    <p class="mt-2 mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">
                      {article.comment}
                    </p>
                    <div class="me-2 flex justify-end items-center">
                      <div class="flex justify-between items-center">
                        {#if article.isLiked}
                          <button on:click={() => clickLiked(article.isLiked, article.boardId)}>
                            <HeartSolid size="lg" color={'red'} />
                          </button>
                          <p>{article.likeCount}</p>
                        {:else}
                          <button on:click={() => clickLiked(article.isLiked, article.boardId)}>
                            <HeartOutline size="lg" />
                          </button>
                          <p>{article.likeCount}</p>
                        {/if}
                      </div>
                    </div>
                  </Card>
                </div>
              {/if}
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
    <div id="tab-item">
      <TabItem
        defaultClass="tab-item font-bold text-xs gap-2 bg-white bg-opacity-80 rounded-t-md"
        divClass="flex flex-col justify-center p-0 w-80"
        on:click={getUnpostArticleInfo}
      >
        <div slot="title" class="flex items-center gap-1">
          <MapPinSolid size="sm" />
          내 드로잉
        </div>
        <div id="mystorage-content" class="space-y-4" on:touchmove>
          {#if unpostArticles.length === 0}
            <h1>게시할 드로잉이 없습니다!</h1>
          {:else}
            {#each unpostArticles as article}
              <div class="mb-3 relative z-10">
                <Card size="sm" class="mb-5 p-1 grow">
                  <button
                    class="absolute top-5 right-3 z-20 bg-gray-600 text-white bg-opacity-70 rounded-md p-1"
                    on:click={() => clickPost(article)}
                  >
                    <UploadSolid size="md" />
                  </button>

                  <FeedArticle
                    title={article.title}
                    artImage={article.artImage}
                    drawingImage={article.drawingImage}
                  />
                </Card>
              </div>
            {/each}
          {/if}
        </div>
      </TabItem>
    </div>
  </Tabs>
</div>
<div class="absolute bottom-3 right-3 z-20">
  <Button on:click={() => scrollToTop('#mystorage-body')} size="sm">
    <ChevronDoubleUpOutline />
  </Button>
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
    width: 35%;
    z-index: 2 !important;
  }

  #background {
    position: fixed;
    bottom: 2%;
    left: 25%;
    transform: translate(-50%, -50%);
    z-index: 1;
    opacity: 0.3;
    overflow: visible;
    transition: 800ms;
    animation: sway 5s ease-in-out infinite;
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
    animation: sway 5s ease-in-out infinite;
  }

  @keyframes sway {
    0% {
      transform: translate(-100%, -120%);
    }
    10% {
      transform: translate(-80%, -100%);
    }
    20% {
      transform: translate(-60%, -80%);
    }
    30% {
      transform: translate(-40%, -60%);
    }
    40% {
      transform: translate(-20%, -40%);
    }
    50% {
      transform: translate(-40%, -30%);
    }
    60% {
      transform: translate(-50%, -50%);
    }
    70% {
      transform: translate(-60%, -70%);
    }
    80% {
      transform: translate(-70%, -90%);
    }
    90% {
      transform: translate(-90%, -110%);
    }
    100% {
      transform: translate(-100%, -120%);
    }
  }
</style>

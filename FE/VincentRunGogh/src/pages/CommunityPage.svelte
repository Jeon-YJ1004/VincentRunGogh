<script lang="ts">
  import FeedArticle from '@/components/cards/FeedArticle.svelte';
  import Tabbar from '@components/common/Tabbar.svelte';
  import { replace } from 'svelte-spa-router';
  import Swal from 'sweetalert2';
  import { HeartOutline, HeartSolid } from 'flowbite-svelte-icons';
  import { Card, Button, Avatar } from 'flowbite-svelte';
  import Header from '@/components/common/Header.svelte';
  import { userStore } from '@/stores/userStore';
  import { get } from 'svelte/store';
  import { onMount } from 'svelte';
  import { dislikeArticle, getArticleList, likeArticle } from '@/api/communityApi';

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

  // 검색범위 조절
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
        // 여기서 range 변수를 사용하여 추가 로직을 구현
      }
    });
  }

  // 게시물 조회하기
  async function getAllArticleInfo() {
    let articleListParams: {
      lat: number;
      lng: number;
      type: string;
    } = {
      lat: currentLat,
      lng: currentLng,
      type: 'all',
    };
    try {
      let responseAllArticle = await getArticleList(articleListParams);
      articleList = responseAllArticle.data.boardList;
    } catch (error) {
      replace('/');
      throw error;
    }
  }

  // 작성시간 파악 함수
  function timeAgo(created: Date): string {
    const now = new Date();
    const diffInMilliseconds = now.getTime() - created.getTime();

    const diffInSeconds = Math.floor(diffInMilliseconds / 1000);
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInSeconds < 60) {
      return `${diffInSeconds}초 전`;
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes}분 전`;
    } else if (diffInHours < 24) {
      return `${diffInHours}시간 전`;
    } else {
      return `${diffInDays}일 전`;
    }
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

  onMount(() => {
    // 유저정보 저장

    // 현재 위치 지정
    window.navigator.geolocation.getCurrentPosition(
      (position) => {
        // 성공 시 현재 위치를 처리
        handlePosition(position);
        getAllArticleInfo();
      },
      failPosition, // 위치 가져오기 실패 시 처리
      positionOption
    );
  });
</script>

<div id="community-body">
  <Header title="커뮤니티" to={'/'} />
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <!-- svelte-ignore a11y-interactive-supports-focus -->
  <div
    id="community-mystorage"
    role="button"
    class="absolute top-5 right-3"
    on:click={() => replace('/community/mystorage')}
  >
    <Avatar src={userInfo.profile} dot={{ color: 'green' }} />
  </div>
  <div id="search-control">
    <Button size="sm" on:click={searchCondition}>검색 반경 설정</Button>
    <p class="my-3 font-bold">반경 {range}km내의 루트만 표시됩니다.</p>
  </div>
  <div id="community-content">
    {#if articleList.filter((article) => article.distanceFromUser <= range).length === 0}
      반경 {range}km 내 게시글이 없습니다!
    {/if}
    {#each articleList as article}
      {#if article.distanceFromUser <= range}
        <Card class="mb-5">
          <div class="flex mb-3 items-center text-center">
            <img
              src={article.profile}
              alt=""
              style="width: 50px; height: 50px; border-radius:50%;"
            />
            <p class="ml-5 font-bold">{article.nickname}</p>
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
          <div class="flex justify-between items-center">
            {timeAgo(new Date(article.created))}
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
      {/if}
    {/each}
  </div>
  <div id="community-tabbar">
    <Tabbar />
  </div>
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

<script lang="ts">
  import { Button, Input } from 'flowbite-svelte';
  import { GradientButton } from 'flowbite-svelte';
  import { ArrowRightOutline, RedoOutline, PenOutline, UploadSolid } from 'flowbite-svelte-icons';
  import { postArticle } from '@/api/communityApi';
  import Swal from 'sweetalert2';

  export let onClose: any;
  export let article: {
    drawingId: number;
    title: string;
    artImage: string;
    drawingImage: string;
    updated: string;
  };

  let inputComment: string = '';

  // 취소시  돌아가기
  function goBack() {
    if (onClose) onClose();
  }

  // 게시글 생성
  async function clickPostArticle() {
    let postArticleForm = {
      comment: inputComment,
      drawingId: article.drawingId,
    };
    Swal.fire({
      title: "<div class='text-lg'>" + '게시글을 올리는 중입니다...' + '</div>',
      imageUrl: '/saveroute.gif',
      imageWidth: 300,
      imageHeight: 200,
      imageAlt: 'running',
      showConfirmButton: false,
      didOpen: () => {
        postArticle(postArticleForm)
          .then(() => {
            Swal.close();
            goBack(); // 비동기 작업이 끝난 후에 모달 닫기
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
</script>

<div class="flex flex-col items-center gap-3 min-h-[200px]">
  <h2 class="font-bold my-4 text-xl">덧붙일 말을 적어주세요!</h2>
  {#if article.artImage}
    <img src={article.artImage} alt="" />
  {:else}
    <img src={article.drawingImage} alt="" />
  {/if}
  <Input
    bind:value={inputComment}
    id="input-comment"
    type="text"
    color="base"
    placeholder="최대 30글자까지 입력가능합니다"
  >
    <PenOutline slot="left" />
  </Input>
  <div class="w-60 flex justify-between">
    <GradientButton color="pinkToOrange" size="sm" on:click={goBack} pill>
      <RedoOutline class="me-2" size="sm" />돌아가기
    </GradientButton>
    <GradientButton color="greenToBlue" size="sm" on:click={clickPostArticle} pill>
      <UploadSolid class="me-2" size="sm" /> 게시하기
    </GradientButton>
  </div>
</div>

<style>
</style>

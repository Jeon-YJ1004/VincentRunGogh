<script lang="ts">
  import { replace } from 'svelte-spa-router';
  export let onClose: any;
  export let imgSrc: any = '/4.png';
  export let route: { id: number; name: string; near: number; length: number; time: number } = {
    id: 13,
    name: '내루트1',
    near: 1.2,
    length: 2.3,
    time: 1234,
  };

  // 너무 멀리 떨어진 루트 상세 접근시 시작버튼 비활성화
  let isFar: boolean = route.near > 5.0;

  // 취소시 홈으로 돌아가기
  function goHome() {
    replace('/');
    if (onClose) onClose();
  }

  // 시작 시 이동
  function startDrawing() {
    replace('/drawingmap');
    if (onClose) onClose();
  }
</script>

<div>
  <img src={imgSrc} alt="" />
  <h2>{route.name}</h2>
  <p>나와의 거리 {route.near}km</p>
  <div class="flex justify-around">
    <button on:click={goHome}>취소</button>
    <button on:click={startDrawing} disabled={isFar}>시작하기</button>
  </div>
</div>

<style>
</style>

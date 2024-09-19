<script lang="ts">
  import { onMount } from 'svelte';
  export let params;

  onMount(() => {
    //TODO - 드로잉 상세 조회 api 연결
    console.log(params.id);
  });
</script>

<div></div>

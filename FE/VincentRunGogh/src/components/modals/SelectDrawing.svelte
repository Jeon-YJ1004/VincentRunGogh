<script lang="ts">
  export let onClose: any;
  import { replace } from 'svelte-spa-router';

  function freeDrawing() {
    replace('/drawingmap');
    if (onClose) onClose();
  }

  function routeList() {
    replace('/routelist');
    if (onClose) onClose();
  }
</script>

<div class="flex justify-around">
  <button on:click={freeDrawing}>자유 드로잉</button>
  <button on:click={routeList}>루트 드로잉</button>
</div>

<style>
</style>

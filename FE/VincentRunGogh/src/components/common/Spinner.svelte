<script>
  import { Spinner } from 'flowbite-svelte';
</script>

<div class="text-center spinner" id="spinner">
  <Spinner color="yellow" size={8} />
</div>

<style>
  .spinner {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5);
    display: none;
    justify-content: center;
    align-items: center;
    pointer-events: none;
  }
</style>

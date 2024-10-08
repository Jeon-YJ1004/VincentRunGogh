<script lang="ts">
  import { createEventDispatcher } from 'svelte';

  export let isMakeRoute: boolean | null = null;

  // Dispatching events
  const dispatch = createEventDispatcher<{
    'click-reset': void;
  }>();
</script>

<button
  type="button"
  class={isMakeRoute ? 'single-click mt-[8vh]' : 'single-click'}
  on:click={(e) => {
    e.stopPropagation();
    dispatch('click-reset');
  }}
  title="Reset View"
>
  <svg xmlns="http://www.w3.org/2000/svg" width="2.8em" height="2.8em" viewBox="0 0 13 13"
    ><path
      fill="currentColor"
      d="M6 7.75a1.75 1.75 0 1 0 0-3.5a1.75 1.75 0 0 0 0 3.5m.75-5.68a4 4 0 0 1 3.18 3.18h1.32a.75.75 0 0 1 0 1.5H9.93a4 4 0 0 1-3.183 3.18l.003.07v1.25a.75.75 0 0 1-1.5 0V10q0-.035.003-.07A4 4 0 0 1 2.07 6.747L2 6.75H.75a.75.75 0 0 1 0-1.5H2q.035 0 .07.003A4 4 0 0 1 5.25 2.07V.75a.75.75 0 0 1 1.5 0zM8.5 6a2.5 2.5 0 1 0-5 0a2.5 2.5 0 0 0 5 0"
    /></svg
  >
</button>

<style>
  button {
    width: 2rem;
    height: 2rem;
    border: 0;
    background-color: transparent;
    transition-property: background-color, background-opacity;
    transition-duration: 250ms;
    border-radius: 0.375rem;
  }

  .single-click:hover {
    background-opacity: 50%;
    background-color: lightgray;
  }
</style>

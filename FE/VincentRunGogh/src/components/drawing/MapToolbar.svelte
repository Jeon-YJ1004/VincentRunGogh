<script lang="ts">
  import { createEventDispatcher } from 'svelte';

  // Dispatching events
  const dispatch = createEventDispatcher<{
    'click-reset': void;
  }>();
</script>

<button
  type="button"
  class="single-click"
  on:click={() => dispatch('click-reset')}
  title="Reset View"
>
  <svg
    fill="none"
    stroke-linecap="round"
    stroke-linejoin="round"
    stroke-width="2"
    viewBox="0 0 24 24"
    stroke="currentColor"
  >
    <path
      d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4"
    />
  </svg>
</button>

<!-- gps 데이터  -->

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

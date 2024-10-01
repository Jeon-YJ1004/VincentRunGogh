<script lang="ts">
  import { formatSecToMMSS } from '@/utils/formatter';
  import { Carousel, Indicator } from 'flowbite-svelte';

  export let title;
  export let artImage;
  export let drawingImage;
  export let distance;
  export let time;
</script>

<div class="relative max-w-4xl">
  <div class="absolute top-3 -left-13 z-30">
    <div class="text-left ms-3 mb-3 text-gray-700 dark:text-gray-400 leading-tight">
      <span>{title}</span>
    </div>
  </div>
  <Carousel
    class="w-full"
    images={[
      { src: drawingImage, alt: 'drawingImage' },
      { src: artImage, alt: 'artImage' },
    ]}
    let:Indicators
  >
    <Indicators
      let:selected
      class="flex absolute bottom-5 start-3/4 z-30 space-x-3 rtl:space-x-reverse -translate-x-1/2 rtl:translate-x-1/2 float-right mr-4"
    >
      <Indicator
        color={selected ? 'red' : 'gray'}
        class={selected ? 'opacity-100' : 'opacity-80'}
      />
    </Indicators>
  </Carousel>
  <div class="absolute bottom-0.5 -left-13 z-30">
    <div class="text-left ms-3 mb-3 text-gray-700 dark:text-gray-400 leading-tight">
      <span class="inline-block">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="1.22em"
          height="1.5em"
          viewBox="0 0 416 512"
          class="align-middle"
          ><path
            fill="currentColor"
            d="M272 96c26.51 0 48-21.49 48-48S298.51 0 272 0s-48 21.49-48 48s21.49 48 48 48M113.69 317.47l-14.8 34.52H32c-17.67 0-32 14.33-32 32s14.33 32 32 32h77.45c19.25 0 36.58-11.44 44.11-29.09l8.79-20.52l-10.67-6.3c-17.32-10.23-30.06-25.37-37.99-42.61M384 223.99h-44.03l-26.06-53.25c-12.5-25.55-35.45-44.23-61.78-50.94l-71.08-21.14c-28.3-6.8-57.77-.55-80.84 17.14l-39.67 30.41c-14.03 10.75-16.69 30.83-5.92 44.86s30.84 16.66 44.86 5.92l39.69-30.41c7.67-5.89 17.44-8 25.27-6.14l14.7 4.37l-37.46 87.39c-12.62 29.48-1.31 64.01 26.3 80.31l84.98 50.17l-27.47 87.73c-5.28 16.86 4.11 34.81 20.97 40.09c3.19 1 6.41 1.48 9.58 1.48c13.61 0 26.23-8.77 30.52-22.45l31.64-101.06c5.91-20.77-2.89-43.08-21.64-54.39l-61.24-36.14l31.31-78.28l20.27 41.43c8 16.34 24.92 26.89 43.11 26.89H384c17.67 0 32-14.33 32-32s-14.33-31.99-32-31.99"
          /></svg
        >
      </span>
      <span>{distance}km</span>
      <span class="inline-block">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="1.5em"
          height="1.5em"
          viewBox="0 0 16 16"
          class="align-middle"
          ><path
            fill="currentColor"
            fill-rule="evenodd"
            d="M6.25 0a.75.75 0 0 0 0 1.5h3.5a.75.75 0 0 0 0-1.5zM13 9A5 5 0 1 1 3 9a5 5 0 0 1 10 0m1.5 0a6.5 6.5 0 1 1-13 0a6.5 6.5 0 0 1 13 0m-2.28-7.28a.75.75 0 0 1 1.06 0l1.5 1.5a.75.75 0 0 1-1.06 1.06l-1.5-1.5a.75.75 0 0 1 0-1.06M8.75 6a.75.75 0 0 0-1.5 0v3a.75.75 0 0 0 .3.6l1.333 1a.75.75 0 1 0 .9-1.2L8.75 8.625z"
            clip-rule="evenodd"
          /></svg
        >
      </span>
      <span>{formatSecToMMSS(time)}</span>
    </div>
  </div>
</div>

<style>
</style>

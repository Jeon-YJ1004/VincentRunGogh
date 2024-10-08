<script lang="ts">
  import { formatSecToHMS } from '@/utils/formatter';
  import { Carousel, Indicator } from 'flowbite-svelte';

  export let title;
  export let artImage;
  export let drawingImage;
  export let distance: number | null = null;
  export let time: number | null = null;
</script>

<div class="relative max-w-4xl">
  <div class="absolute top-3 -left-13 z-30">
    <div
      class="text-left ms-3 mb-3 text-gray-700 dark:text-gray-400 leading-tight bg-gray-600 text-white bg-opacity-70 rounded-md p-1"
    >
      <span>{title}</span>
    </div>
  </div>
  {#if artImage && drawingImage}
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
  {:else if drawingImage}
    <img
      src={drawingImage}
      alt="drawingImage"
      style="width: 100%; height: 30vh; object-fit:cover; border-radius:3%"
    />
  {:else}
    <img
      src={artImage}
      alt="artImage"
      style="width: 100%; height: 30vh; object-fit:cover; border-radius:3%"
    />
  {/if}
  {#if distance && time !== null}
    <div class="absolute bottom-0.5 -left-13 z-30">
      <div
        class="text-left ms-3 mb-3 text-gray-700 dark:text-gray-400 leading-tight bg-gray-600 text-white bg-opacity-70 rounded-md p-1"
      >
        <span class="inline-block">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            xmlns:xlink="http://www.w3.org/1999/xlink"
            fill="#FFF"
            version="1.1"
            id="Capa_1"
            width="1.5em"
            height="1.5em"
            viewBox="0 0 480.543 480.543"
            xml:space="preserve"
          >
            <g>
              <g>
                <path
                  d="M225.278,278.426c0-48.641-39.572-88.213-88.214-88.213c-48.641,0-88.212,39.572-88.212,88.213    c0,14.254,3.401,27.727,9.431,39.654l78.781,162.463l78.795-162.488C221.88,306.133,225.278,292.669,225.278,278.426z     M137.064,327.243c-26.917,0-48.817-21.9-48.817-48.817s21.9-48.817,48.817-48.817c26.918,0,48.817,21.9,48.817,48.817    S163.982,327.243,137.064,327.243z"
                />
                <path
                  d="M387.021,0c-24.63,0-44.669,20.038-44.669,44.668c0,7.218,1.723,14.04,4.776,20.081l39.893,82.267l39.899-82.28    c3.049-6.037,4.77-12.855,4.77-20.067C431.69,20.038,411.65,0,387.021,0z M387.021,69.388c-13.631,0-24.72-11.089-24.72-24.72    c0-13.63,11.089-24.72,24.72-24.72c13.629,0,24.719,11.09,24.719,24.72C411.74,58.299,400.65,69.388,387.021,69.388z"
                />
                <path
                  d="M348.116,289.787c-1.812-8.523-4.166-16.875-7.131-25.075c-3.974-10.987-8.74-21.724-12.391-32.836    c-6.823-20.771,4.948-38.105,7.16-41.256c3.876-5.517,8.408-10.58,13.19-15.322c9.207-9.128,19.361-17.288,29.984-24.803    c-1.316-2.039-2.591-4.1-3.805-6.199c-15.279,5.99-93.854,33.452-91.938,94.121c0.384,12.162,5.083,23.737,7.979,35.526    c1.395,5.681,2.605,11.412,3.345,17.218c0.319,2.614,0.515,5.243,0.729,7.87c0.006-0.02,0.004-0.034-0.008-0.045    c0.007,0.076,0.202,8.326-0.164,11.9c-1.147,11.189-2.799,22.494-7.516,32.758c-4.387,9.543-11.006,17.842-19.193,24.402    c-18.716,14.994-43.514,20.988-66.913,23.398c-1.114,0.117-2.228,0.217-3.342,0.314l-41.779,86.152    c20.614-1.281,41.175-4.102,61.235-9.039c23.607-5.811,46.613-14.588,67.227-27.598c20.596-12.996,38.308-30.578,50.099-51.984    c12.335-22.391,17.277-48.038,16.225-73.452C350.752,307.156,349.928,298.307,348.116,289.787z"
                />
              </g>
            </g>
          </svg>
        </span>

        <span>{distance}km</span>
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
        <span>{formatSecToHMS(time)}</span>
      </div>
    </div>
  {/if}
</div>

<style>
</style>

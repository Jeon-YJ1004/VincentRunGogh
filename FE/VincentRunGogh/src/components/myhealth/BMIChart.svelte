<script lang="ts">
  import { onMount } from 'svelte';
  import { writable } from 'svelte/store';

  const MIN_BMI = 15;
  const MAX_BMI = 40;
  export let bmi: number = 22.5; // 예제 BMI 값

  const bmiCategoryColors = {
    Thinness: { bg: 'from-blue-200 to-blue-300', border: 'border-blue-300' },
    Normal: { bg: 'from-green-200 to-green-300', border: 'border-green-300' },
    Overweight: { bg: 'from-yellow-200 to-yellow-300', border: 'border-yellow-300' },
    Obesity: { bg: 'from-red-200 to-red-300', border: 'border-red-300' },
  };

  const groupList = [
    {
      title: 'Thinness',
      range: [MIN_BMI, 18.5],
      color: bmiCategoryColors.Thinness,
    },
    {
      title: 'Normal',
      range: [18.5, 25],
      color: bmiCategoryColors.Normal,
    },
    {
      title: 'Overweight',
      range: [25, 30],
      color: bmiCategoryColors.Overweight,
    },
    {
      title: 'Obesity',
      range: [30, MAX_BMI],
      color: bmiCategoryColors.Obesity,
    },
  ];

  const calculateWidth = (min: number, max: number): number => {
    return ((max - min) / (MAX_BMI - MIN_BMI)) * 100; // returns a percentage
  };

  onMount(() => {});
</script>

<div class="p-4 rounded-lg w-[90%]">
  <div class="text-3xl font-semibold text-center">{bmi}</div>
  <div class="mt-4 flex w-full h-3 rounded overflow-hidden">
    {#each groupList as { range, title, color }}
      <div
        class={`flex-grow-0 h-full bg-gradient-to-r ${color.bg}`}
        style="width: {calculateWidth(range[0], range[1])}%"
      ></div>
    {/each}
  </div>
</div>

<style>
  /* Additional styles can be placed here */
</style>

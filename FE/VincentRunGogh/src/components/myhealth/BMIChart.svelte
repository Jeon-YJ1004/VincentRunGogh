<script lang="ts">
  import { Badge } from 'flowbite-svelte';

  const MIN_BMI = 15;
  const MAX_BMI = 40;
  export let bmi: number = 22.5; // 예제 BMI 값

  const bmiCategoryColors = {
    Thinness: { bg: 'from-blue-200 to-blue-300', badge: 'indigo' },
    Normal: { bg: 'from-green-200 to-green-300', badge: 'green' },
    Overweight: { bg: 'from-yellow-200 to-yellow-300', badge: 'yellow' },
    Obesity: { bg: 'from-red-200 to-red-300', badge: 'red' },
  };

  const groupList = [
    {
      title: '저체중',
      range: [MIN_BMI, 18.5],
      color: bmiCategoryColors.Thinness,
    },
    {
      title: '정상',
      range: [18.5, 25],
      color: bmiCategoryColors.Normal,
    },
    {
      title: '과체중',
      range: [25, 30],
      color: bmiCategoryColors.Overweight,
    },
    {
      title: '비만',
      range: [30, MAX_BMI],
      color: bmiCategoryColors.Obesity,
    },
  ];

  const calculateWidth = (min: number, max: number): number => {
    return ((max - min) / (MAX_BMI - MIN_BMI)) * 100; // returns a percentage
  };

  // Reactive declarations
  $: markerPosition = () => {
    if (bmi < MIN_BMI) return '0%'; // 최소 범위 이하일 때
    if (bmi > MAX_BMI) return '100%'; // 최대 범위 이상일 때
    return `${((bmi - MIN_BMI) / (MAX_BMI - MIN_BMI)) * 100}%`;
  };

  $: currentCategory =
    groupList.find((category) => bmi >= category.range[0] && bmi < category.range[1]) ??
    groupList[0];
  $: markerStyle = `left: ${markerPosition()};`;
</script>

<div class="pt-1 p-4 rounded-lg w-[90%] h-[120px] flex flex-col items-center justify-between">
  <div class=" text-center text-3xl font-bold tracking-tight text-gray-900 dark:text-white">
    {bmi}
  </div>
  <div class="flex w-full h-3 rounded overflow-visible relative">
    <div
      class="absolute top-[-1.5rem] -translate-y-1/2 transform -translate-x-1/2"
      style={markerStyle}
    >
      <Badge rounded large color={currentCategory.color.badge} class=" relative rounded-md  badge">
        {currentCategory.title}
      </Badge>
    </div>
    {#each groupList as { range, title, color }}
      <div
        id="hover"
        class={`flex-grow-0 h-full bg-gradient-to-r ${color.bg}`}
        style="width: {calculateWidth(range[0], range[1])}%"
      ></div>
    {/each}
  </div>
</div>

<style>
  .badge::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    width: 0;
    height: 0;
    border: 10px solid transparent;
    border-top-color: inherit;
    border-bottom: 0;
    margin-left: -10px;
    margin-bottom: -10px;
    z-index: 100; /* Z-index를 추가하여 보이도록 합니다 */
    transform: translateY(100%); /* 꼬리가 보이도록 위치를 조정합니다 */
  }
</style>

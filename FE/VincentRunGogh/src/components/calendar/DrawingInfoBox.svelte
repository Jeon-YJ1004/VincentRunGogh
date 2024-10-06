<script lang="ts">
  import { Card, Button, Toggle } from 'flowbite-svelte';
  import { formatSecToHMS } from '@/utils/formatter';

  import DrawingSummaryInfo from '@components/calendar/DrawingSummaryInfo.svelte';
  export let time: number;
  export let averagePace: number;
  export let distance: number;
  export let imgSrc: string;
</script>

<div>
  <div class="space-y-4">
    <Card img={imgSrc} horizontal size="md">
      <DrawingSummaryInfo {time} averagePace={formatSecToHMS(averagePace)} {distance} />
    </Card>
  </div>
</div>

<style>
</style>

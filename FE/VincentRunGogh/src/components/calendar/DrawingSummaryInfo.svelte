<script lang="ts">
  export let time: number;
  export let averagePace: string;
  export let distance: number;
</script>

<div class="summary-info">
  <div class="info">
    <div class="label">Time(H)</div>
    <div class="value">{time}</div>
  </div>
  <div class="info">
    <div class="label">Avr Pace</div>
    <div class="value">{averagePace}</div>
  </div>
  <div class="info">
    <div class="label">Distance(KM)</div>
    <div class="value">{distance}</div>
  </div>
</div>

<style>
  .summary-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  .info {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 14px;
  }
  .label {
    color: #666;
    font-weight: bold;
    margin-bottom: 5px;
  }
  .value {
    font-size: 16px;
    color: #333;
  }
</style>

<script lang="ts">
  import { Navbar } from 'flowbite-svelte';
  import BackButton from '@components/buttons/BackButton.svelte';
  export let title: string = '';
  export let to: string;
</script>

<div id="header" class="flex justify-center items-center text-center font-bold font-black">
  <BackButton {to} />
  <h2 class="text-center">
    {title}
  </h2>
</div>

<style>
  #header {
    height: 10vh;
    width: 100%;
    z-index: 2;
  }
</style>

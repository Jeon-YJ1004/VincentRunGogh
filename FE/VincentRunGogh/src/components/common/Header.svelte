<script lang="ts">
  import { Navbar } from 'flowbite-svelte';
  import BackButton from '@components/buttons/BackButton.svelte';
  export const title: string = '';
</script>

<Navbar>
  <BackButton />
  <span>
    {title}
  </span>
</Navbar>

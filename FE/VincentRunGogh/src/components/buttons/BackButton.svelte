<script lang="ts">
  import { AngleLeftOutline } from 'flowbite-svelte-icons';
  import { replace } from 'svelte-spa-router';
  import Swal from 'sweetalert2';

  export let to: string;

  function clickBackButton() {
    Swal.fire({
      title: "<div class='text-lg'>" + '이전으로 돌아가시겠습니까?' + '</div>',
      showCancelButton: true,
      confirmButtonColor: '#5E8358',
      cancelButtonColor: '#697386',
      confirmButtonText: '뒤로',
      cancelButtonText: '취소',
    }).then((result) => {
      if (result.isConfirmed) {
        replace(to);
        Swal.close();
      }
    });
  }
</script>

<button id="backbutton" on:click={clickBackButton}>
  <AngleLeftOutline />
</button>

<style>
  #backbutton {
    position: fixed;
    top: 4%;
    left: 4%;
    z-index: 1000;
  }
</style>

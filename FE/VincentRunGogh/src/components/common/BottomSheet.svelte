<script>
  import { onMount, onDestroy } from 'svelte';
  import { CupertinoPane } from 'cupertino-pane';

  export let Component; // 외부에서 전달받은 자식 컴포넌트를 사용할 수 있도록 함
  export let props = {}; // 외부에서 전달받은 속성
  export let onClose;
  let pane = null;
  let container = null;

  onMount(() => {
    pane = new CupertinoPane(container, {
      initialBreak: 'middle',
      breaks: {
        middle: { enabled: true, height: 400 },
        bottom: { enabled: true, height: 100 },
      },

      bottomClose: true,
    });
    pane.on('onDidDismiss', (ev) => {
      onClose();
      pane.destroy();
    });
    pane.present();
    document.addEventListener(
      'backbutton',
      function () {
        pane.destroy({ animate: true });
      },
      false
    );
  });

  onDestroy(() => {
    if (pane) {
      pane.destroy();
    }
  });
</script>

<div bind:this={container} class="pane-container">
  <div class="pane-content">
    <svelte:component this={Component} {...props} />
    <!-- 자식 컴포넌트 동적 렌더링 -->
  </div>
</div>

<style>
  .pane-container {
    position: relative;
    width: 100%;
    height: auto;
  }

  .pane-content {
    padding: 20px;
    background-color: white;
    height: 100%;
    border-top-left-radius: 20px;
    border-top-right-radius: 20px;
  }
</style>

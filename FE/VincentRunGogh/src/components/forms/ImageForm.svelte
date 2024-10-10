<script lang="ts">
  import { Input, Fileupload, Label, Button, Helper, ButtonGroup } from 'flowbite-svelte';

  import { onMount, SvelteComponent } from 'svelte';
  import { profileFormStore } from '@/stores/profileFormStore';
  import { userStore } from '@/stores/userStore';

  const { values, helpers, validateProfileImage } = profileFormStore;

  // 이미지 업로드 핸들러
  function handleImageUpload(event: Event & { currentTarget: HTMLInputElement }) {
    const file = event.target?.files[0];

    if (file) {
      validateProfileImage(file);
      values.update((v) => ({ ...v, profileImage: file }));
    }
  }
</script>

<div class="mb-6">
  <Label for="profileImg" class="pb-2">프로필 이미지</Label>
  <input type="file" id="profileImg" on:change={handleImageUpload} />
  <Helper style="color: {$helpers.profileImage.color};">{$helpers.profileImage.message}</Helper>
</div>

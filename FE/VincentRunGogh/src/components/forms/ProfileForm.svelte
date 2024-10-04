<script lang="ts">
  import { Input, Fileupload, Label, Button, Helper, ButtonGroup } from 'flowbite-svelte';

  import { onMount, SvelteComponent } from 'svelte';
  import { profileFormStore } from '@/stores/profileFormStore';
  import { userStore } from '@/stores/userStore';
  import { get } from 'svelte/store';

  const {
    values,
    helpers,
    validateNickname,
    validateHeight,
    validateWeight,
    validateProfileImage,
    checkNicknameAvailability,
  } = profileFormStore;

  async function handleCheckNickname() {
    const currentUser = get(userStore);
    console.log(currentUser.nickname, $values.nickname);
    if ($values.nickname.length > 0 && $values.nickname !== currentUser.nickname) {
      await checkNicknameAvailability($values.nickname);
    }
  }

  onMount(() => {
    userStore.subscribe(($user) => {
      if ($user) {
        values.update((v) => ({
          ...v,
          nickname: $user.nickname,
          height: $user.height,
          weight: $user.weight,
        }));
      }
    });
  });
</script>

<div class="mb-6">
  <Label for="nickname" class="block mb-2">별명</Label>
  <ButtonGroup class="w-full">
    <Input
      id="nickname"
      type="text"
      placeholder="별명"
      bind:value={$values.nickname}
      on:input={(e) => validateNickname(e.target.value)}
    />
    <Button on:click={handleCheckNickname}>중복 확인</Button>
  </ButtonGroup>
  <Helper style="color: {$helpers.nickname.color};">{$helpers.nickname.message}</Helper>
</div>

<div class="mb-6">
  <Label for="height" class="block mb-2">키</Label>
  <Input
    id="height"
    type="text"
    placeholder="키"
    bind:value={$values.height}
    on:input={(e) => validateHeight(e.target.value)}
  />
  <Helper style="color: {$helpers.height.color};">{$helpers.height.message}</Helper>
</div>

<div class="mb-6">
  <Label for="weight" class="block mb-2">몸무게</Label>
  <Input
    id="weight"
    type="text"
    placeholder="몸무게"
    bind:value={$values.weight}
    on:input={(e) => validateWeight(e.target.value)}
  />
  <Helper style="color: {$helpers.weight.color};">{$helpers.weight.message}</Helper>
</div>

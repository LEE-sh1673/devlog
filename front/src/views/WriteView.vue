<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

const title = ref('');
const content = ref('');

const router = useRouter();

const write = () => {

  const requestBody = {
    title: title.value,
    content: content.value
  };
  axios.post("/api/posts", requestBody)
      .then(() => router.replace({name: "home"}))
      .catch((error) => console.log(error));
}
</script>

<template>

  <div>
    <el-input
        type="text"
        v-model="title"
        clearable
        maxlength="30"
        show-word-limit
        placeholder="제목을 입력해주세요."
    />
  </div>

  <div class="mt-2">
    <el-input
        type="textarea"
        v-model="content"
        maxlength="300"
        show-word-limit
        placeholder="본문을 입력해주세요."
        :autosize="{ minRows: 15, maxRows: 4 }"
    />
  </div>

  <div class="mt-2">
    <el-button type="primary" @click="write()">글 작성완료</el-button>
  </div>

</template>

<style scoped>

</style>
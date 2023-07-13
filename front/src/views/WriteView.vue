<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";
import { ElMessage } from "element-plus";

const title = ref('');
const content = ref('');

const router = useRouter();

const write = () => {

  const requestBody = {
    title: title.value,
    content: content.value
  };
  axios.post("/posts", requestBody)
      .then(() => {
        router.replace({name: "home"});
        ElMessage({
          message: '글이 등록되었습니다.',
          type: 'success',
          duration: 2000,
          showClose: true
        });
      })
      .catch((error) => console.log(error));
}
</script>

<template>

  <div class="write-form">
    <div>
      <el-input
          type="text"
          v-model="title"
          clearable
          maxlength="50"
          show-word-limit
          placeholder="제목을 입력해주세요."
      />
    </div>

    <div class="mt-3">
      <el-input
          type="textarea"
          v-model="content"
          maxlength="1000"
          show-word-limit
          placeholder="본문을 입력해주세요."
          :autosize="{ minRows: 15, maxRows: 4 }"
      />
    </div>

    <div class="mt-3">
      <div class="d-flex justify-content-end">
        <el-button type="primary" @click="write()">글 작성완료</el-button>
      </div>
    </div>
  </div>

</template>

<style scoped>
</style>
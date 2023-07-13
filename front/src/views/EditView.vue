<script setup lang="ts">

import { onMounted, ref } from "vue";
import axios from "axios";
import {useRouter} from "vue-router";
import { ElMessage } from "element-plus";

const router = useRouter();

const props = defineProps({
  postId: {
    type: [Number, String],
    required: true
  }
});

const post = ref({
  id: 0,
  title: "",
  content: ""
});

onMounted(() => {
  axios({
    baseURL: `/posts/${props.postId}`,
    method: 'GET'
  })
  .then((response) => post.value = response.data.response)
  .catch((error) => console.log(error));
});

const edit = () => {
  console.log(post.value);

  axios.patch(`/api/posts/${props.postId}`, post.value)
      .then(() => {
        router.replace({ name: 'read', params: {postId: props.postId} });
        ElMessage({
          message: '수정되었습니다.',
          type: 'success',
          duration: 2000,
          showClose: true
        });
      })
      .catch((error) => console.log(error));
};

</script>

<template>

  <div>
    <el-input
        type="text"
        v-model="post.title"
        clearable
        maxlength="50"
        show-word-limit
        placeholder="제목을 입력해주세요."
    />
  </div>

  <div class="mt-3">
    <el-input
        type="textarea"
        v-model="post.content"
        maxlength="1000"
        show-word-limit
        placeholder="본문을 입력해주세요."
        :autosize="{ minRows: 15, maxRows: 4 }"
    />
  </div>

  <div class="mt-3">
    <div class="d-flex justify-content-end">
    <el-button type="warning" @click="edit()">글 수정완료</el-button>
    </div>
  </div>

</template>
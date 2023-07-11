<script setup lang="ts">

import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

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

  const loading = ref(true);

  onMounted(() => {
    axios
        .get(`/api/posts/${props.postId}`)
        .then((response) => {
          post.value = response.data.response;
          loading.value = false;
        })
        .catch((error) => console.log(error));
  });

  const moveToEdit = () => {
    router.push({name: "edit", params: {postId: props.postId }});
  }
</script>

<template>
  <div class="post" v-loading="loading">
    <el-row>
      <el-col>
        <h2 class="title">{{post.title}}</h2>

        <div class="sub d-flex gap-2 align-items-center">
          <div class="category">개발</div>
          <div class="dot">|</div>
          <div class="regDate">2022-06-01</div>
        </div>
      </el-col>
    </el-row>

    <el-row class="mt-3">
      <el-col>
        <div class="content">{{post.content}}</div>
      </el-col>
    </el-row>

    <el-row class="mt-3">
      <el-col>
        <div class="d-flex justify-content-end">
          <el-button type="success" @click="moveToEdit()">수정</el-button>
        </div>
      </el-col>
    </el-row>

  </div>
</template>

<style scoped lang="scss">
  .post {

    .title {
      font-size: 2rem;
    }

    .content {
      line-height: 1.75;
      word-break: keep-all;
      font-size: 1.0625rem;
      white-space: break-spaces;
    }

    .sub {
      color: var(--text-secondary);
      font-size: 0.875rem;

      .category {
        font-size: 0.813rem;
        border-radius: .5rem;
        background: var(--primary-color-lighter);
        padding: .25rem .5rem;
        color: var(--primary-color);
        font-weight: 600;
      }
    }
  }
</style>
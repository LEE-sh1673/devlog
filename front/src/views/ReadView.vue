<script setup lang="ts">

import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";

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
  axios.get(`/api/posts/${props.postId}`)
      .then((response) => {
        // post.value = response.data.response;
        // loading.value = false;
        setInterval(() => {
          post.value = response.data.response;
          loading.value = false;
        }, 1000);
      }).catch((error) => console.log(error));
});

const moveToEdit = () => {
  router.push({name: "edit", params: {postId: props.postId}});
}

const deletePost = () => {
  axios.delete(`/api/posts/${props.postId}`)
      .then(() => {
        router.replace({name: 'home'});
        ElMessage({
          message: '삭제되었습니다.',
          type: 'success',
          duration: 2000,
          showClose: true
        });
      }).catch((error) => console.log(error));
};

const openDeleteConfirmBox = () => {
  ElMessageBox.confirm(
      '정말로 글을 삭제하시겠습니까?',
      'Warning',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning',
      }
  ).then(() => deletePost())
};
</script>

<template>
  <el-space direction="vertical">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="post">
          <el-row>
            <el-col>
              <el-skeleton-item variant="h1" class="title" style="width: 80%; height: 60px"/>

              <div class="sub d-flex gap-2 align-items-center" style="height: 27px;">
                <el-skeleton-item variant="text" style="width: 35px; height: 27px;"/>
                <el-skeleton-item variant="text" style="width: 2px; height: 27px;"/>
                <el-skeleton-item variant="text" style="width: 100px; height: 27px;"/>
              </div>
            </el-col>
          </el-row>

          <el-row class="post__content">
            <el-col>
              <div class="content">
                <el-skeleton :rows="5" style="width: 760px;"/>
              </div>
            </el-col>
          </el-row>

          <el-row class="post__menu">
            <el-col>
              <div class="d-flex justify-content-end gap-2">
                <el-skeleton-item variant="button" class="el-button" style="width: 55px; height: 34px;"/>
                <el-skeleton-item variant="button" class="el-button" style="width: 55px; height: 34px;"/>
              </div>
            </el-col>
          </el-row>
        </div>
      </template>

      <template #default>
        <div class="post">
          <el-row>
            <el-col>
              <h2 class="title">{{ post.title }}</h2>

              <div class="sub d-flex gap-2 align-items-center">
                <div class="category">개발</div>
                <div class="dot">|</div>
                <div class="regDate time">2022-06-01</div>
              </div>
            </el-col>
          </el-row>

          <el-row class="post__content">
            <el-col>
              <div class="content">{{ post.content }}</div>
            </el-col>
          </el-row>

          <el-row class="post__menu">
            <el-col>
              <div class="d-flex justify-content-end gap-2">
                <el-button type="danger" @click="openDeleteConfirmBox()">삭제</el-button>
                <el-button type="success" @click="moveToEdit()">수정</el-button>
              </div>
            </el-col>
          </el-row>
        </div>
      </template>


    </el-skeleton>
  </el-space>
</template>

<style scoped lang="scss">
.post {

  max-width: 900px;

  .title {
    font-size: 2.5rem;
    margin: 1.75rem 0;
  }

  .content {
    line-height: 1.75;
    word-break: keep-all;
    font-size: 1rem;
    white-space: break-spaces;
  }

  .post__content {
    margin-top: 1.5rem;
  }

  .post__menu {
    border-top: 1px solid var(--border);
    padding-top: 1.5rem;
    margin-top: 1.5rem;
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
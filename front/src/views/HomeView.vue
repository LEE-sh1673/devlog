<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";

const router = useRouter();
const posts = ref([]);
const loading = ref(true);

axios.get("/api/posts")
.then((response) => {
  response.data.response.forEach(post => posts.value.push(post));
  loading.value = false;
})
.catch((error) => console.log(error));

const moveToRead = () => {
  router.push({name: "read"});
};
</script>

<template>
  <p class="nums__post">전체 글 <span>{{ posts.length }}</span> 개</p>
  <ul class="posts" v-loading="loading">
    <li v-for="post in posts" :key="post.id" @click="moveToRead()">
      <div class="title">
        <router-link :to="{ name: 'read', params: {postId: post.id} }">
          {{ post.title }}
        </router-link>
      </div>

      <div class="content">
        {{ post.content }}
      </div>

      <div class="sub d-flex gap-2 align-items-center">
        <div class="category">개발</div>
        <div class="dot">|</div>
        <div class="regDate">2022-06-01</div>
      </div>

    </li>
  </ul>
</template>

<style scoped lang="scss">

.nums__post {
  margin-bottom: 1.25rem;
  padding-bottom: 1.25rem;

  & > span {
    color: var(--primary-color);
    font-weight: 600;
  }
}

ul.posts {
  list-style: none;
  padding: 0;

  li {
    margin-bottom: 2rem;

    &:last-child {
      margin-bottom: 0;
    }

    .title {
      font-size: 1.25rem;

      & > a {
        font-weight: 600;
        text-decoration: none;
        color: var(--text-color);
      }
    }

    .content {
      margin-top: 8px;
      overflow:hidden;
      max-height: 8rem;
      -webkit-box-orient: vertical;
      display: block;
      display: -webkit-box;
      text-overflow: ellipsis;
      -webkit-line-clamp: 3;
    }
  }

  .sub {
    color: var(--text-secondary);
    margin-top: 12px;
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

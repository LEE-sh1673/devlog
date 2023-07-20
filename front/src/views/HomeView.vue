<script setup lang="ts">

import axios from "axios";
import {onMounted, ref} from "vue";

const pageNumber = ref(0);
const pageSize = ref(4);
const totalElements = ref(0);
const testPosts: any = ref([]);

onMounted(() => {
  fetchPosts(pageNumber.value, pageSize.value);
});

const fetchPosts = (page: number, size: number) => {
  axios
      .get("/testPosts", {params: {page: page, size: size}})
      .then((response) => initPages(response.data.response))
      .catch((error) => console.log(error));
}

const initPages = (res : any) => {
  testPosts.value = res.content;
  pageNumber.value = res.number;
  pageSize.value = res.size;
  totalElements.value = res.totalElements;
}

const changePage = (page: number) => {
  pageNumber.value = page;
  fetchPosts(pageNumber.value, pageSize.value);
};
</script>

<template>
  <p class="nums__post">전체 글 <span>{{ totalElements }}</span> 개</p>
  <ul class="testPosts">
    <li v-for="testPost in testPosts" :key="testPost!.id">
      <div class="title">
        <router-link :to="{ name: 'read', params: {postId: testPost!.id} }">
          {{ testPost!.title }}
        </router-link>
      </div>

      <div class="content">
        {{ testPost!.content }}
      </div>

      <div class="sub d-flex gap-2 align-items-center">
        <div class="category">개발</div>
        <div class="dot">|</div>
        <div class="regDate">2022-06-01</div>
      </div>

    </li>
  </ul>

  <el-pagination @current-change="changePage"
                 :total="totalElements"
                 :page-size="pageSize"
                 layout="prev, pager, next"/>
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

ul.testPosts {
  list-style: none;
  padding: 0;

  li {
    margin-bottom: 2.5rem;

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
      font-size: 0.875rem;
      margin-top: 8px;
      overflow: hidden;
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

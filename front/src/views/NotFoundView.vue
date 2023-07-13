<template>
  <h1>해당 페이지를 찾지 못했습니다.</h1>
  <p>주소가 잘못되었거나 더 이상 제공되지 않는 페이지입니다.</p>
  <el-button
      type="text"
      class="home-btn"
      @click="goHome()"
  >홈으로 이동
  </el-button>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";

const router = useRouter();
const goHome = () => {
  router.push({name: "home"});
}
</script>

<style scoped>


.home-btn {
  font-size: 1.25rem;
  color: var(--primary-color-dark);
}

.home-btn:hover {
  color: var(--primary-color) !important;
}
</style>

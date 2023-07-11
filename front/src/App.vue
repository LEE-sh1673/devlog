<script setup lang="ts">
import Header from "@/components/Header.vue";
import Content from "@/components/Content.vue";
</script>

<template>
  <Header/>
  <Content/>
</template>

<style>

:root {
  --text-color: #213547;
  --text-secondary: rgba(60, 60, 60, .7);

  --primary-color: #42b883;
  --primary-color-light: #42d392;
  --primary-color-lighter: rgba(66, 211, 146, .12);
  --primary-color-dark: #33a06f;

  --border: rgba(60, 60, 60, .12);

  --el-menu-active-color: var(--primary-color-dark) !important;
  --el-menu-hover-text-color: var(--primary-color) !important;
  --el-menu-hover-bg-color: var(--primary-color-lighter) !important;
}

body {
  margin: 0;
  width: 100%;
  font-family: "Pretendard Variable", Pretendard, -apple-system, BlinkMacSystemFont, system-ui, Roboto,
  "Helvetica Neue", "Segoe UI", "Apple SD Gothic Neo", "Noto Sans KR", "Malgun Gothic", "Apple Color Emoji",
  "Segoe UI Emoji", "Segoe UI Symbol", sans-serif;
  color: var(--text-color);
  font-size: 16px;
  font-weight: 400;
  line-height: 1.5;
}

a {
  color: var(--primary-color);
}

a:hover {
  color: var(--primary-color-light);
}
</style>
<template>
  <el-header class="header">
    <el-menu
        mode="horizontal"
        :default-active="$route.name"
        @select="menuSelect"
    >
      <el-menu-item index="home">
        <span>
          <el-icon style="vertical-align: middle">
            <HomeFilled/>
          </el-icon>
          <span>Home</span>
        </span>

      </el-menu-item>
      <el-menu-item index="write">
        <span>
          <el-icon style="vertical-align: middle">
            <Edit/>
          </el-icon>
          <span>글 작성</span>
        </span>
      </el-menu-item>
    </el-menu>
  </el-header>
  <!--    <nav>-->
  <!--      <RouterLink to="/">Home</RouterLink>-->
  <!--      <RouterLink to="/write">글 작성</RouterLink>-->
  <!--    </nav>-->
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {Edit, HomeFilled} from "@element-plus/icons-vue";

const router = useRouter();

const menuSelect = (index: String) => {
  router.push({ name: index });
}
</script>

<style scoped>
.header {
  padding: 0;
  height: 60px;
}

.header .el-menu-item i {
  color: var(--primary-color)
}
</style>

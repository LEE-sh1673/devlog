<script setup lang="ts">

import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";

const router = useRouter();
const posts = ref([]);

axios
    .get("/api/posts")
    .then((response) => response.data.response.forEach(post => posts.value.push(post)))
    .catch((error) => console.log(error));

const moveToRead = () => {
  router.push({name: "read"});
};
</script>

<template>

  <ul>
    <li v-for="post in posts" :key="post.id" @click="moveToRead()">
      <div>
        <router-link :to="{ name: 'read', params: {postId: post.id} }">{{ post.title }}</router-link>
      </div>

      <div>{{ post.content }}</div>
    </li>
  </ul>
</template>

<style scoped>
ul {
  list-style: none;
}

li {
  margin-bottom: 1rem;
}

li:last-child {
  margin-bottom: 0;
}
</style>

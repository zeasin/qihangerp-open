<template>
  <div class="p-5">路由参数type：{{ query }}</div>
</template>

<script setup>
defineOptions({
  name: "Other",
  inheritAttrs: false,
});
import { useRoute } from "vue-router";

// 获取query参数
const query = useRoute().query.type;
</script>

<style lang="scss" scoped></style>

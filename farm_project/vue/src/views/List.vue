<template>
  <div class="item" v-for="item in data.noticeList" :key="item.id">
    <h3>{{item.title}}</h3>
    <p>{{item.content}}</p>
    <p>{{item.time}}</p>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "../utils/request";


const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  noticeList: []
})

// 请求后台的公告数据
request.get('/notice/selectAll').then(res => {
  data.noticeList = res.data
})


</script>


<style scoped>
.item {
  width: 100%;
  height: auto;
  box-sizing: border-box;
  border-bottom: 1px solid black;
  margin-bottom: 0%;
  padding: 5%;
}

.item h3 {
  font-size: 22px;
  margin-bottom: 10px;
  overflow: hidden;
  /*white-space: nowrap;*/
  /*text-overflow: ellipsis;*/
}

.item p {
  color: #999;
  font-size: 14px;
  line-height: 26px;
  margin-bottom: 10px;
  overflow: hidden;
}

.item p:last-of-type {
  margin-bottom: 0;
}
</style>


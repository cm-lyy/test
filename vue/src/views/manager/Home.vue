<template>
  <el-carousel :interval="3000" type="card" height="300px" style="margin-left:1%; width: 98%" indicator-position="none">
    <el-carousel-item v-for="(imagePath,index) in images" :key="index" style="border: 2px solid black;height: 280px">
      <h3 text="2xl" justify="center">
        <img :src="imagePath" alt="carousel-image" style="max-height:280px;width: 700px">
      </h3>  <!--630px*280px-->
    </el-carousel-item>
  </el-carousel>

<!--  <div class="container">-->
  <div style="display: flex">
    <el-card style="width: 50%;">
      <div class="list" style="height:280px;box-sizing: border-box">
        <List />
      </div>
    </el-card>

    <el-card style="width: 50%;margin-left: 2%">
      <template #header>系统介绍</template>
      <div class="content">
        农产品销售管理系统通过信息化手段，实时监控农产品从田间到餐桌的各个环节，
        包括种植、采摘、仓储、运输和销售等。该系统利用现代信息技术，将农产品的生产者、供应商、零售商和消费者紧密连接起来，实现了从田间到餐桌的全程无缝对接。
        我们致力于帮助各种规模的农业企业和个体农民，特别是那些希望提高销售效率、优化供应链管理、提升品牌影响力的企业。
        此外，该系统也适用于农产品批发市场、零售商以及电商平台等销售渠道。
        欢迎联系我们，咨询电话16527368653！！！！！
      </div>
      <div style="margin-top: 5%">
        <el-button plain @click="dialogFormVisible = true">
          欢迎意见反馈
        </el-button>
        <el-dialog v-model="dialogFormVisible" title="意见反馈" width="500" >
          <el-form :model="form">
            <el-form-item label="姓名" :label-width="formLabelWidth">
              <el-input v-model="form.name"  />
            </el-form-item>
            <el-form-item label="公司" :label-width="50">
              <el-input v-model="form.company"  />
            </el-form-item>
            <el-form-item label="意见" :label-width="50">
              <el-input v-model="form.options" style="height: 100%" type="textarea" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="dialogFormVisible = false">取消</el-button>
              <el-button type="primary" @click="dialogFormVisible = false">
                提交意见
              </el-button>
            </div>
          </template>
        </el-dialog>
      </div>
    </el-card>
  </div>

</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
const dialogFormVisible = ref(false)
const formLabelWidth = '50px'

const form = reactive({
  name: '',
  company: '',
  options: ''
})

</script>

<script lang="ts">
import { defineComponent } from "vue";
import List from '../List.vue'
import {Box} from "@element-plus/icons-vue";

export default defineComponent({
  components: {Box, List},
  setup() {

  },
  data() {
    return {
      images: [
        '/imgs/a.jpg',
        '/imgs/a.jpeg',
        '/imgs/d.png',
        '/imgs/cherry.png',
        '/imgs/egg.jpg',
        '/imgs/fruit.jpeg',
        '/imgs/mangosteen.png',
        '/imgs/mi.jpg',
        '/imgs/strawberry.png',
        '/imgs/wugonghai.jpeg',
      ]
    };
  },
})
</script>

<style>
*{
  margin: 0;
  padding: 0;
  list-style: none;
}
body {
  /*background: #fef0db;*/
  border-bottom: rgba(179, 229, 252, 0.5);
  background-color: #b3e5fc;
}
</style>

<style scoped>
.container {
  width: 80%;
  height: 50%;
  display: flex;
  margin-left: 10%;
  margin-top: 1%;
  border: 5px solid black;

}
.content {
  font-size: 15px;
  font-family: 思源宋体;
}

.list {
  width: 100%;
  overflow-y: scroll;
}

.el-carousel__item h3 {
  color: #99a9bf;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #99a9bf;
}
.carousel-image {
  max-height: 280px;
  display: block;
}
</style>
<template>
    <div style="display:flex; height: 100vh;">
      <el-aside :width="sideWide + '%'"  style="height:100vh;background-color: white; box-shadow: 2px 0 6px rgb(0 21 41 / 35%);
        overflow:hidden">
        <el-menu
            router
            style="min-height: 100%;"
            :default-active="router.currentRoute.value.path"
            :default-openeds="['1', '4']"
            background-color="rgb(48,65,86)"
            text-color="#fff"
            active-text-color="#F9B44C"
            :collapse-transition="false"
            :collapse="isCollapse"
            @collapse="handleCollapse"
        >
          <div style="height: 60px; line-height: 60px; text-align: center">
            <img src="../assets/imgs/pingguo.png" alt="logo" style="width: 25px; height:25px; position: relative; top: 5px; margin-right: 2px">
            <b style="color: white; margin-left: 10px; font-size: 120%" v-show="LogoTextShow">农产品销售系统</b>

          </div>
          <el-sub-menu index="1">
            <template #title>
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </template>
            <el-menu-item index="/home">
              <el-icon><ChatLineSquare /></el-icon>
              <span>系统公告</span>
            </el-menu-item>
            <el-menu-item index="/notice" v-if="data.user.role === 'ADMIN'">
              <el-icon><Bell /></el-icon>
              <span>系统公告管理</span>
            </el-menu-item>
            <el-menu-item index="/total" v-if="data.user.role === 'ADMIN'">
              <el-icon><Histogram /></el-icon>
              <span>数据统计</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><List /></el-icon>
              <span>订单管理</span>
            </template>
          <el-menu-item index="/orders">
            <el-icon><Tickets /></el-icon>
            <span>订单查询</span>
          </el-menu-item>
            <el-menu-item index="/buy" v-if="data.user.role === 'USER'">
              <el-icon><Goods /></el-icon>
              <span>农产品购买</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Platform /></el-icon>
              <span>农产品管理</span>
            </template>
            <el-menu-item index="/category">
              <el-icon><Menu /></el-icon>
              <span>农产品分类管理</span>
            </el-menu-item>
            <el-menu-item index="/goods">
              <el-icon><Goods /></el-icon>
              <span>农产品管理</span>
            </el-menu-item>
            <el-menu-item index="/goodsStock">
              <el-icon><SoldOut /></el-icon>
              <span>农产品进货管理</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="4" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Avatar /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin">
              <el-icon><UserFilled /></el-icon>
              <span>管理员信息</span>
            </el-menu-item>
            <el-menu-item index="/user">
              <el-icon><User /></el-icon>
              <span>普通用户信息</span>
            </el-menu-item>
          </el-sub-menu>

        </el-menu>
      </el-aside>
<!--      //顶部-->
      <el-container :width="100 - sideWide + '%'" style="height: 100vh;margin: 0 auto">
        <el-header style="width: 100%;display: flex;justify-content:space-between;background-color: #8c939d; padding: 0%;height: 8%">
            <button style="background-color:#8c939d;border: 0px" @click="toggleCollapse">
              <el-icon style="font-size: 20px;width: 50px;height: 50px"><Fold /></el-icon>
            </button>
            <div class="demo-type" style="width: fit-content;margin-right: 3%; align-items: center;">
              <el-avatar :icon="UserFilled"
                         :src="data.user.avatar || data.user.name " />
              <el-dropdown>
                <el-button type="text"  class="el-dropdown-link" style="margin-left:5px; width: 70px;color: white">{{ data.user.name }}
                  <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="person">个人资料</el-dropdown-item>
                    <el-dropdown-item @click="password">修改密码</el-dropdown-item>
                    <el-dropdown-item @click="logout">退出</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

        </el-header>
        <el-main style="height: 72%;width: 100%;">
          <router-view @updateUser="updateUser" />
        </el-main>
      </el-container>


    </div>
</template>

<script>
export default {
  data() {
    return {
      isCollapse: false,
      sideWide: 14,
      LogoTextShow: true
    };
  },
  methods: {
    handleCollapse(value) {
      this.isCollapse = value;
    },
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
      if(this.isCollapse) {
        this.LogoTextShow = false
        this.sideWide = 4
      } else {
        this.LogoTextShow = true
        this.sideWide = 14
      }
    }
  }
};
</script>

<script setup>
import { reactive } from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import {Fold, FolderRemove} from "@element-plus/icons-vue";
import { ArrowDown } from '@element-plus/icons-vue'
import { UserFilled } from '@element-plus/icons-vue'

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}')
})

if (!data.user?.id) {
  ElMessage.error('请登录！')
  router.push('/login')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('system-user') || '{}')
}

const logout = () => {
  ElMessage.success('退出成功')
  localStorage.removeItem('system-user')
  router.push('/login')
}

const person = () => {
  router.push('/person')
}

const password = () => {
  router.push('/password')
}

</script>

<style scoped>
span{
  font-size: 1rem;
}
*{
  /*padding: 0 !important;*/
  /*margin: 0 !important;*/
}

:deep(th)  {
  color: #333;
}
.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}

.demo-type {
  display: flex;
}
.demo-type > div {
  flex: 1;
  text-align: center;
}

.demo-type > div:not(:last-child) {
  border-right: 1px solid var(--el-border-color);
}
</style>
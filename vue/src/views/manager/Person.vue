<template>
  <div style="width: 50%;">
    <div class="card" style="padding: 30px">
      <el-form :model="data.user" style="padding-right: 50px">
        <div style="margin: 20px; text-align: center">
          <el-upload :show-file-list="false" class="avatar-uploader" :action="uploadUrl" :on-success="handleFileUpload">
            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
        <el-form-item label="账号">
          <el-input disabled v-model="data.user.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="data.user.name" autocomplete="off" />
        </el-form-item>
        <div v-if="data.user.role === 'USER'">
          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="data.user.sex">
              <el-radio label="男"></el-radio>
              <el-radio label="女"></el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="data.user.phoneNumber" autocomplete="off" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="data.user.email" autocomplete="off" />
          </el-form-item>
        </div>
        <div style="text-align: center">
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    imageUrl: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      cropper: null
    };
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.$nextTick(() => {
          this.initCropper();
        });
      } else {
        if (this.cropper) {
          this.cropper.destroy();
          this.cropper = null;
        }
      }
    }
  },
  methods: {
    initCropper() {
      const image = this.$refs.image;
      this.cropper = new Cropper(image, {
        aspectRatio: 1,
        viewMode: 1,
        // 其他配置选项...
      });
    },
    cropImage() {
      if (this.cropper) {
        this.cropper.getCroppedCanvas().toBlob((blob) => {
          // 处理裁剪后的图片，比如上传到服务器
          console.log(blob);
          // 关闭对话框（可选）
          this.$emit('update:visible', false);
        });
      }
    },
    cancelCrop() {
      this.$emit('update:visible', false);
    }
  }
};
</script>

<script setup>
import {reactive} from "vue"
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import { ref } from 'vue'

const centerDialogVisible = ref(false)

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
})

const handleFileUpload = (file) => {
  data.user.avatar = file.data
}
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const emit = defineEmits(["updateUser"])
// 把当前修改的用户信息存储到后台数据库
const save = () => {
  // 在提交之前移除密码字段
  const userData = { ...data.user };  // 创建一个副本，以避免直接修改原数据
  // 删除密码字段，避免在请求时传递密码
  delete userData.password;
  delete userData.newPassword;


  if (data.user.role === 'ADMIN') {
    request.put('/admin/update', userData).then(res => {
      if (res.code === '200') {
        ElMessage.success('更新成功')
        //把更新后的用户信息存储到缓存
        localStorage.setItem('system-user', JSON.stringify(data.user))
        emit('updateUser')
      } else {
        ElMessage.error(res.msg)
      }
    })
  } else {
    request.put('/user/update', userData).then(res => {
      if (res.code === '200') {
        ElMessage.success('更新成功')
        //把更新后的用户信息存储到缓存
        localStorage.setItem('system-user', JSON.stringify(data.user))
        emit('updateUser')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }
}
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
}
.image-container {
  width: 100%;
  height: 400px;
  position: relative;
}
.image-container img {
  width: 100%;
  height: 100%;
  display: block;
}
.cropper-buttons {
  text-align: right;
  margin-top: 10px;
}
</style>
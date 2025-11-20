<template>
  <div class="email-container">
    <h2>测试发送邮件</h2>
    <el-form :model="form" ref="formRef" label-width="80px">
      <el-form-item label="收件人">
        <el-input v-model="form.to" placeholder="请输入收件人邮箱" />
      </el-form-item>
      <el-form-item label="主题">
        <el-input v-model="form.subject" placeholder="请输入邮件主题" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input
            type="textarea"
            v-model="form.text"
            placeholder="请输入邮件内容"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="sendEmail">发送</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const form = ref({
  to: "",
  subject: "",
  text: "",
});

const sendEmail = async () => {
  if (!form.value.to || !form.value.subject || !form.value.text) {
    ElMessage.error("请完整填写邮件信息");
    return;
  }

  try {
    request.post('/email/send', form.value).then((res) => {
    console.log(res.code)
    if (res.code === "200") {
      ElMessage.success("邮件发送成功！");
    } else {
      ElMessage.error(res.message || "邮件发送失败");
    }
    });
  } catch (error) {
    ElMessage.error("邮件发送请求失败，请稍后重试");
  }
};
</script>

<style scoped>
.email-container {
  width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  background-color: #f9f9f9;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  width: 100%;
}
</style>

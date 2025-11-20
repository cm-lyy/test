<template>
  <div class="register-container">
    <div class="register-boxOne">
      <div>
        <img class="register-image"  src="../assets/imgs/login-left-qte69_UC.svg" alt="login">
      </div>
      <div class="register-boxTwo">
        <div class="register-title" >
          找 回 帐 号
        </div>
        <el-form :model="data.form" ref="formRef" :rules="data.rules">
          <el-form-item prop="Email">
            <el-input :prefix-icon="Message" size="large" v-model="data.form.email" placeholder="请输入账号绑定的邮箱" />
          </el-form-item>
          <el-form-item prop="captcha">
            <div style="display: flex;">
              <el-input
                  size="large"
                  v-model="data.form.captcha"
                  placeholder="请输入验证码"
                  style="flex: 1;width:150px;"
              />
              <el-image
                  style="width: 175px; height: 40px; margin-left: 10px; "
                  @click="refreshCaptcha"
                  :src="captchaUrl"
                  fit="contain"
              />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button size="large" type="primary" style="width: 100%; margin-top: 2%" @click="register">找回账号</el-button>
          </el-form-item>
        </el-form>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <a href="/byebye">忘记邮箱？</a>
          </div>
          <div>
            想起来了？请 <a href="/login">登录</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";
import {Message} from "@element-plus/icons-vue";

// 验证码图片 URL 和加载状态
const captchaUrl = ref('');
const loading = ref(false);

// 定义 formRef
const formRef = ref(null);

// 获取验证码图片
const refreshCaptcha = async () => {
  try {
    loading.value = true;
    const response = await request.get('/Captcha/image', { responseType: 'blob' });
    if (response && response.data instanceof Blob) {
      captchaUrl.value = URL.createObjectURL(response.data);
    } else {
      ElMessage.error('验证码获取失败，返回的数据格式错误');
    }
  } catch (error) {
    if (error.response) {
      // 检查状态码
      if (error.response.status === 429) {
        ElMessage.error('请求过于频繁，请稍后再试');
      } else {
        ElMessage.error(`验证码获取失败，状态码: ${error.response.status}`);
      }
    } else {
      ElMessage.error(`验证码获取失败，请稍后重试。错误信息: ${error.message}`);
    }
  } finally {
    loading.value = false;
  }
};

// 验证图片验证码
const validateCaptcha = async (rule, value, callback) => {
  try {
    const response = await request.post('/Captcha/validate', { captcha: value });
    if (response.success) {
      callback();
    } else {
      callback(new Error(response.msg || '验证码错误'));
    }
  } catch (error) {
    callback(new Error('验证码验证失败，请重试'));
  }
};

// 表单数据和验证规则
const data = reactive({
  form: {
    email: '',
  },
  rules: {
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
    ],
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { validator: validateCaptcha, trigger: 'blur' },
    ]
  },
});

const isSubmitting = ref(false);  // 添加一个标志变量来跟踪请求状态

// 注册逻辑
const register = () => {
  if (isSubmitting.value) return; // 防止重复提交
  isSubmitting.value = true;

  // 验证表单是否有效
  formRef.value.validate((valid) => {
    if (valid) {
      // 表单验证通过后发送请求
      request.post('/ForgotUsername', data.form)
          .then((res) => {
            isSubmitting.value = false;
            if (res.code === '200') {
              ElMessage.success("账号名已发送到您的邮箱，请注意查收");
              // router.push('/login');
            } else {
              ElMessage.error(res.msg || '找回账号失败');
            }
          })
          .catch(() => {
            isSubmitting.value = false;
            ElMessage.error('请求失败，请稍后重试');
          });
    } else {
      isSubmitting.value = false; // 重置状态
      ElMessage.error('请完善表单信息');
    }
  });
};

// 页面加载时，初始化验证码
onMounted(() => {
  refreshCaptcha();
});
</script>

<style scoped>
.register-container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/imgs/bg.png");
  background-size: cover;
}

.register-boxTwo {
  height: 100%;
  width: 30%;
  margin-top: 0%;
  margin-left: 2%;
  padding: 40px 30px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, .1);
  background-color: rgba(255, 255, 255, .8);
}

.register-boxOne {
  width: 90%;
  height: 90%;
  padding: 50px 30px;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, .1);
  background-color: rgba(255, 255, 255, .8);
  display: flex;
  justify-content: center;
}

.register-image {
  width: 140%;
  height: 140%;
  margin-left: -25%;
  margin-top: -8%;
}

.register-title {
  font-weight: bold;
  font-size: 24px;
  text-align: center;
  margin-bottom: 30px;
  color: #1450aa;
  font-family: '思源宋体';
  margin-top: -2%;
}

@media (max-width: 992px) {
  .register-image {
    display: none;
  }
  .register-boxTwo {
    width: 80%;
    max-width: 400px;
    height: 100%; /* 调高右侧输入框容器的高度 */
    margin: auto;
  }
}
</style>

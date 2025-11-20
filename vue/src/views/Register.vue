<template>
  <div class="register-container">
    <div class="register-boxOne">
      <div>
        <img class="register-image"  src="../assets/imgs/login-left-qte69_UC.svg" alt="register">
      </div>
      <div class="register-boxTwo">
        <div class="register-title" >
          欢 迎 注 册
        </div>
        <el-form :model="data.form" ref="formRef" :rules="data.rules">
          <el-form-item prop="username">
            <el-input :prefix-icon="User" size="large" v-model="data.form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input :prefix-icon="Lock" size="large" v-model="data.form.password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input :prefix-icon="Lock" size="large" v-model="data.form.confirmPassword" placeholder="请输入确认密码" show-password />
          </el-form-item>
          <el-form-item prop="email">
            <el-input :prefix-icon="Message" size="large" v-model="data.form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item prop="phoneNumber">
            <el-input :prefix-icon="Phone" size="large" v-model="data.form.phoneNumber" placeholder="请输入手机号" />
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
          <el-form-item prop="smsCode">
            <div style="display: flex;">
              <el-input
                  size="large"
                  v-model="data.form.smsCode"
                  placeholder="请输入手机验证码"
                  style="flex: 1;width:150px;"
              />
              <el-button
                  size="large"
                  style="margin-left: 10px; width: 175px;"
                  :disabled="isSmsCodeButtonDisabled"
                  @click="sendCode"
              >
                {{ countdown > 0 ? countdown + '秒' : '获取手机验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button size="large" type="primary" style="width: 100%; margin-top: 2%" @click="register">注册</el-button>
          </el-form-item>
        </el-form>
        <div style="text-align: right;">
          已有账号？请 <a href="/login">登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { User, Lock, Phone, Message } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";
import router from "@/router";

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== data.form.password) {
    callback(new Error('密码和确认密码不一致'));
  } else {
    callback();
  }
};

// 验证码图片 URL 和加载状态
const captchaUrl = ref('');
const loading = ref(false);

const countdown = ref(0);  // 控制倒计时的秒数
const isSmsCodeButtonDisabled = ref(false);  // 控制验证码按钮是否禁用
const isRequestingCode = ref(false);  // 防止并发请求
const timer = ref(null);  // 用于存储计时器的ID

// 定义 formRef
const formRef = ref(null);

const startCountdown = () => {
  countdown.value = 60;  // 设置倒计时时间为60秒
  isSmsCodeButtonDisabled.value = true;  // 禁用按钮

  // 每秒减少1秒，直到倒计时结束
  timer.value = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value -= 1;
    } else {
      clearInterval(timer.value);  // 清除定时器
      isSmsCodeButtonDisabled.value = false;  // 启用按钮
    }
  }, 1000);
};


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

// 发送手机验证码
const sendCode = async () => {
  if (countdown.value > 0) return;  // 如果倒计时未结束，直接返回
  if (isRequestingCode.value) return;  // 如果正在请求验证码，阻止重复请求

  isRequestingCode.value = true;  // 设置标志，标识当前正在请求验证码

  // 表单验证
  formRef.value.validateField(['username', 'password', 'confirmPassword', 'captcha', 'email'], async (valid) => {
    if (valid) {
      try {
        const response = await request.post('/Sms/Sender', {
          phoneNumber: data.form.phoneNumber
        });
        if (response.success) {
          ElMessage.success('验证码发送成功');
          startCountdown();  // 启动倒计时
        } else {
          ElMessage.error(response.message || '验证码发送失败');
        }
      } catch (error) {
        ElMessage.error('验证码请求失败，请稍后重试');
      } finally {
        isRequestingCode.value = false;  // 请求完成后，重置标志
      }
    } else {
      ElMessage.error('请先填写完整的账号、密码和图片验证码');
      isRequestingCode.value = false;  // 表单验证失败时，也需要重置标志
    }
  });
};

// 验证手机验证码
const isSmsCodeValidating = ref(false);  // 添加标志

const validateSmsCode = async (rule, value, callback) => {
  if (isSmsCodeValidating.value) {
    return;  // 如果正在验证，直接返回
  }
  isSmsCodeValidating.value = true;  // 设置为正在验证状态
  try {
    const response = await request.post('/Sms/Verify', {
      smsCode: value,
      phoneNumber: data.form.phoneNumber
    });
    if (response.success) {
      callback();
    } else {
      callback(new Error(response.message || '手机验证码错误'));
    }
  } catch (error) {
    callback(new Error('手机验证码验证失败，请重试'));
  } finally {
    isSmsCodeValidating.value = false;  // 请求完成后重置状态
  }
};


// 表单数据和验证规则
const data = reactive({
  form: {
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    phoneNumber: '',
    captcha: '',
    smsCode: '', // 手机验证码字段
  },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    confirmPassword: [
      { required: true, message: '请输入确认密码', trigger: 'blur' },
      { validator: validateConfirmPassword, trigger: 'blur' }
    ],
    phoneNumber: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { pattern: /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/, message: '请输入有效的邮箱地址', trigger: 'blur' }
    ],
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { validator: validateCaptcha, trigger: 'blur' },
    ],
    smsCode: [
      { required: true, message: '请输入手机验证码', trigger: 'submit' },
      { validator: validateSmsCode, trigger: 'submit' }
    ]
  },
});

const isSubmitting = ref(false);  // 添加一个标志变量来跟踪请求状态

// 注册逻辑
const register = () => {
  if (isSubmitting.value) return;  // 如果正在提交，则不执行
  isSubmitting.value = true;  // 设置为正在提交

  formRef.value.validate((valid) => {
    if (valid) {
      request.post('/register', data.form).then((res) => {
        isSubmitting.value = false;  // 请求完成后重置状态
        if (res.code === '200') {
          ElMessage.success("注册成功");
          router.push('/login');
        } else {
          ElMessage.error(res.msg);
        }
      }).catch(() => {
        isSubmitting.value = false;  // 如果请求失败，也要重置状态
      });
    } else {
      isSubmitting.value = false;  // 如果表单验证失败，重置状态
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

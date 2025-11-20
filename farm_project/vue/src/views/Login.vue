<template>
  <div class="login-container">
    <div class="login-boxOne" style="display: flex">
      <div>
        <img class="login-image"  src="../assets/imgs/login-left-qte69_UC.svg" alt="login">
      </div>
      <div class="login-boxTwo">
        <div class="login-title" >
          农 产 品 销 售 管 理 系 统
        </div>
        <el-form :model="data.form" ref="formRef" :rules="data.rules">
          <el-form-item prop="username">
            <el-input  :prefix-icon="User" size="large" v-model="data.form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input  :prefix-icon="Lock" size="large" v-model="data.form.password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item prop="role">
            <el-select  size="large" style="width: 100%" v-model="data.form.role">
              <el-option value="ADMIN" label="管理员"></el-option>
              <el-option value="USER" label="普通用户"></el-option>
            </el-select>
          </el-form-item>


          <el-form-item prop="captcha">
            <div  style="display: flex;">
              <el-input
                  size="large"
                  v-model="data.form.captcha"
                  placeholder="请输入验证码"
                  style="flex: 1;width:158px;"

              />
              <el-image
                  style="width: 175px; height: 40px; margin-left: 10px;"
                  @click="refreshCaptcha"
                  :src="captchaUrl"
                  fit="contain"

              />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button size="large" type="primary" style="width: 100%; margin-top: 2%" @click="login">登 录</el-button>
          </el-form-item>
        </el-form>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <a href="/ForgotPassword">忘记账号或密码？</a>
          </div>
          <div>
            如没有账号？请 <a href="/register">注册</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

// 验证码图片 URL 和加载状态
const captchaUrl = ref('');
const loading = ref(false);

// 定义 formRef
const formRef = ref(null);

// 获取验证码图片
const refreshCaptcha = async () => {
  try {
    loading.value = true;
    // 请求验证码图片
    const response = await request.get('/Captcha/image', { responseType: 'blob' });
    // 确认 response 是一个包含 Blob 的对象
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


const validateCaptcha = async (rule, value, callback) => {
  try {
    // 使用 request.post 发送 POST 请求
    const response = await request.post('/Captcha/validate', {
      captcha: value,  // 将验证码数据发送到后端
    });
    // 判断响应是否成功
    if (response.success) {
      callback();  // 验证通过
    } else {
      callback(new Error(response.msg || '验证码错误'));
      ElMessage.error("验证码都输入不对，跳楼吧");
    }
  } catch (error) {
    callback(new Error('验证码验证失败，请重试'));
  }
};

// 表单数据和验证规则
const data = reactive({
  form: {
    username: '',
    password: '',
    captcha: '',
    role: 'ADMIN'
  },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { validator: validateCaptcha, trigger: 'blur' },
    ],
  },
});


// 登录逻辑
const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 调用后台登录接口
      request.post('/login', data.form).then((res) => {
        if (res.code === '200') {
          ElMessage.success("登录成功");
          // 将用户数据存入 localStorage
          localStorage.setItem('system-user', JSON.stringify(res.data));
          // 登录成功后，跳转到系统页面
          router.push('/');
          console.log(data.form.role)
          if (data.form.role === 'USER'){
            // 获取用户信息并检查各个字段
            checkUserInfo(res.data);
          }
        } else {
          ElMessage.error(res.msg);
        }
      }).catch((error) => {
        console.error(error);
        // 刷新验证码
        refreshCaptcha();
      });
    }
  });
};

// 检查用户信息（邮箱、性别、手机号、头像）
const checkUserInfo = (userData) => {
  let missingFields = [];  // 用来记录缺失的字段

  // 检查各个字段
  if (!userData.email) missingFields.push('邮箱');
  if (!userData.sex) missingFields.push('性别');
  if (!userData.phoneNumber) missingFields.push('手机号');
  if (!userData.avatar) missingFields.push('头像');

  // 如果有缺失的字段，弹出提示框
  if (missingFields.length > 0) {
    // 提示用户缺失的字段
    ElMessageBox.confirm(
        `您的以下信息未填写：${missingFields.join('，')}，请完善个人信息。`,
        '提示',
        {
          confirmButtonText: '去修改',
          cancelButtonText: '稍后',
          type: 'warning'
        }
    ).then(() => {
      // 用户点击 "去修改"，跳转到个人信息修改页面
      router.push('/person');
    }).catch(() => {
      // 用户点击 "稍后" 或关闭弹窗，不做任何操作
    });
  }
};



//页面加载时，初始化验证码
onMounted(() => {
  refreshCaptcha();
});

</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/imgs/bg.png");
  background-size: cover;
}

.login-boxTwo {
  height: 80%;
  width: 30%;
  margin-top: 4%;
  margin-left: 2%;
  padding: 40px 30px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, .1);
  background-color: rgba(255, 255, 255, .8);
}

.login-boxOne {
  width: 90%;
  height: 90%;
  padding: 50px 30px;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, .1);
  background-color: rgba(255, 255, 255, .8);
  display: flex;
  justify-content: center;
}

.login-title {
  font-weight: bold;
  font-size: 24px;
  text-align: center;
  margin-bottom: 30px;
  color: #1450aa;
  font-family: '思源宋体';
  margin-top: -2%;
}

.login-image {
  width: 140%;
  height: 140%;
  margin-left: -25%;
  margin-top: -8%;
}

@media (max-width: 992px) {
  .login-image {
    display: none;
  }
  .login-boxTwo {
    width: 80%;
    max-width: 400px;
    height: 80%; /* 调高右侧输入框容器的高度 */
    margin: auto;
  }
}
</style>

<template xmlns="">
  <div>
    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.name" style="width: 300px; margin-right: 10px" placeholder="请输入用户名或名称查询"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </div>
      <el-table :data="data.tableData" stripe>
        <el-table-column label="用户名" prop="username"></el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="头像">
          <template #default="scope">
            <el-image :src="scope.row.avatar" style="width: 40px; height: 40px; border-radius: 50%"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="角色" prop="role">
          <template #default="scope">
            <span v-if="scope.row.role === 'ADMIN'">管理员</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination @current-change="load" background layout="prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>
    <el-dialog title="信息" width="40%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <!-- 添加 ref="formRef" 来引用表单实例 -->
      <el-form ref="formRef" :model="data.form" :rules="formRules" label-width="100px" style="padding-right: 50px">
        <el-form-item label="头像" prop="avatar">
          <el-upload :action="uploadUrl" list-type="picture" :on-success="handleImgSuccess">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="data.form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" />
        </el-form-item>
        <!-- 添加密码输入框 -->
        <el-form-item label="密码" prop="password">
          <el-input v-model="data.form.password" type="password" autocomplete="off" />
        </el-form-item>
        <!-- 添加确认密码输入框 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="data.form.confirmPassword" type="password" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">保 存</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive} from "vue";
import { ref } from 'vue';
import {ElMessageBox, ElMessage} from "element-plus";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

// 自定义校验规则
const validatePass = (rule, value, callback) => {
  // 如果没有输入密码，则 confirmPassword 也不能输入
  if (!data.form.password && value) {
    callback(new Error('请先输入密码'));
  } else if (data.form.password) {  // 当 password 有输入时
    if (!value) {  // 如果 confirmPassword 没有输入
      callback(new Error('请确认密码'));
    } else if (value !== data.form.password) {  // 如果 confirmPassword 和 password 不一致
      callback(new Error('两次输入密码不一致'));
    } else {
      callback(); // 验证通过
    }
  } else {
    callback();  // 如果 password 为空，直接通过验证
  }
}

const formRules = {
  username: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 15, message: "账号长度应在 3 到 15 个字符之间", trigger: "blur" },
  ],
  name: [
    { required: true, message: "请输入名称", trigger: "blur" },
    { min: 1, max: 20, message: "名称长度应在 1 到 20 个字符之间", trigger: "blur" },
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        if (value) {
          if (value.length < 5) {
            callback(new Error("密码长度不能小于 5 位"));
          } else if (value.length > 20) {
            callback(new Error("密码长度不能大于 20 位"));
          } else {
            callback(); // 验证通过
          }
        } else {
          callback(); // 如果没有输入密码，直接通过验证
        }
      },
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { validator: validatePass, trigger: 'blur' },
  ],
};



// 获取表单实例
const formRef = ref(null);

// 响应式数据
const data = reactive({
  username: '',
  name: '',
  pageNum: 1,
  pageSize: 10,
  tableData: [],
  total: 0,
  form: {},
  formVisible: false,
});

// 加载数据
const load = () => {
  data.username = data.name;  // 将 name 的值赋给 username
  request.get('/admin/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
      username: data.username
    }
  }).then(res => {
    data.tableData = res.data?.list;
    data.total = res.data?.total;
  });
};



// 新增
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.form.password = ''; // 清空密码字段，不展示加密密码
  data.formVisible = true
}

// 新增保存
const add = () => {
  request.post('/admin/add', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 编辑保存
const update = () => {
  request.put('/admin/update', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}


// 弹窗保存
const save = () => {
  // 校验表单
  formRef.value.validate((valid) => {
    if (valid) {
      // 如果是新增操作，直接提交表单
      if (!data.form.id) {
        // 执行新增操作
        add();
      } else {
        // 执行更新操作，检查密码是否为空
        if (!data.form.password) {
          // 如果密码为空，删除 password 字段以防止更新为空密码
          delete data.form.password;
        }
        update();
      }
    } else {
      // 如果表单验证失败，显示错误消息
      ElMessage.error("会不会好好输入！！");
    }
  });
};


// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/admin/delete/' + id).then(res => {
      if (res.code === '200') {
        load()
        ElMessage.success('操作成功')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

// 重置
const reset = () => {
  data.name = null
  load()
}

// 处理文件上传的钩子
const handleImgSuccess = (res) => {
  data.form.avatar = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
}

load()
</script>
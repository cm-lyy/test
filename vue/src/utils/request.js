import { ElMessage } from 'element-plus'
import router from '../router'
import axios from "axios";

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000,  // 后台接口超时时间设置
    // withCredentials: true // 确保携带凭据
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
});

/// response 拦截器
request.interceptors.response.use(
    response => {
        // 如果是返回的文件 (Blob)，直接返回
        if (response.config.responseType === 'blob') {
            return response; // 返回整个 response 对象
        }
        let res = response.data;
        // 确保返回的数据是对象，不是字符串
        if (typeof res === 'string') {
            try {
                res = JSON.parse(res);
            } catch (e) {
                console.error("解析响应数据失败:", e);
                return Promise.reject(new Error('响应数据格式不正确'));
            }
        }
        // 检查返回的数据格式是否符合预期
        if (res.code === '401') {
            ElMessage.error(res.msg);
            router.push("/login");
            return Promise.reject(new Error('未授权'));
        }

        // 如果后端未提供 `success` 字段，需直接返回完整数据
        if (!('success' in res)) {
            // console.warn('返回数据缺少 success 字段:', res);
            return res; // 返回数据，避免 undefined 问题
        }

        return res; // 返回解析后的数据
    },
    error => {
        if (error.response.status === 429) {
            ElMessage.error("你tmd要按多少次");
        }else {
            console.log('请求错误:', error);
            ElMessage.error('请求失败，请稍后重试');

        }
        return Promise.reject(error);
    }
);


export default request

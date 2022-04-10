import axios from 'axios'
import store from '@/store'
import storage from 'store'
import router from '@/router'
import { notification } from 'ant-design-vue'
import { VueAxios } from './axios'
import { ACCESS_TOKEN,REFRESH_TOKEN,EXPIRE_TIME } from '@/store/mutation-types'
import { Modal } from 'ant-design-vue';
import { refresh } from '@/api/auth/login'


// 请求超时时间，10s
const requestTimeOut = 10 * 1000
const success = 200
// 更换令牌的时间区间
const checkRegion = 5 * 60 * 1000
// 提示信息显示时长
const messageDuration = 5 * 1000

// 创建 axios 实例
const request = axios.create({
  // API 请求的默认前缀
  baseURL: <string>import.meta.env.VITE_APP_BASE_API,
  // timeout: 6000, // 请求超时时间
  timeout: requestTimeOut,
  responseType: 'json',
  validateStatus(status) {
    return status === success
  }
})

// 异常拦截处理器
const errorHandler = (error) => {
  if (error.response) {
    const data = error.response.data
    // 从 localstorage 获取 token
    const token = storage.get(ACCESS_TOKEN)
    if (error.response.status === 403) {
      notification.error({
        message: 'Forbidden',
        description: data.message
      })
    }
    if (error.response.status === 401 && !(data.result && data.result.isLogin)) {
      notification.error({
        message: 'Unauthorized',
        description: 'Authorization verification failed'
      })
      if (token) {
        store.dispatch('Logout').then(() => {
          setTimeout(() => {
            window.location.reload()
          }, 1500)
        })
      }
    }
  }
  return Promise.reject()
}

// request interceptor
request.interceptors.request.use(config => {
  let _config = config
  try {
    const expireTime = storage.get(EXPIRE_TIME)
    if (expireTime) {
      const left = expireTime - new Date().getTime()
      const refreshToken = storage.get(REFRESH_TOKEN)
      if (left < checkRegion && refreshToken) {
        if (left < 0) {
          store.commit('SET_TOKEN', '')
          store.commit('SET_REFRESH_TOKEN', '')
          store.commit('SET_EXPIRE_TIME', '')
        }
        _config = queryRefreshToken(_config, refreshToken)
      } else {
        const accessToken = storage.get(ACCESS_TOKEN)
        if (accessToken) {
          _config.headers['Authorization'] = 'bearer ' + accessToken
        }
      }
    }
  } catch (e) {
    console.error(e)
  }
  return _config
}, errorHandler)

async function queryRefreshToken(config, refreshToken) {
  refresh({refreshToken:refreshToken}).then(result => {
    if (result.status === success) {
      saveData(result.data)
      config.headers['Authorization'] = 'bearer ' + storage.get(ACCESS_TOKEN)
    }
  })
  return config
}

function saveData(data) {
  store.commit('SET_TOKEN', data.access_token)
  store.commit('SET_REFRESH_TOKEN', data.refresh_token)
  const current = new Date()
  const expireTime = current.setTime(current.getTime() + 1000 * data.expires_in)
  store.commit('SET_EXPIRE_TIME', expireTime)
}

// response interceptor
request.interceptors.response.use((response) => {
  return response
  }, (response) => {
    if (response) {
      const errorMessage = response.data === null ? '系统内部异常，请联系网站管理员' : response.data.data.msg
      switch (response.status) {
        case 404:
          notification.error({
            message: '很抱歉，资源未找到',
            description: '很抱歉，资源未找到'
          })
          break
        case 403:
          notification.error({
            message: '很抱歉，您暂无该操作权限',
            description: '很抱歉，您暂无该操作权限'
          })
          break
        case 401:
          notification.error({
            message: '很抱歉，认证已失效，请重新登录',
            description: '很抱歉，认证已失效，请重新登录'
          })
          break
        default:
          if (errorMessage === 'refresh token无效') {
            Modal.confirm({
              title: "提示",
              content: "登录已过期，请重新登录', '温馨提示",
              onOk: () => {
                router.push('/login')
              }
            })
          } else {
            notification.error({
              message: errorMessage,
              description: errorMessage
            })
          }
          break
      }
    }
    return Promise.reject()
})

const installer = {
  vm: {},
  install (Vue) {
    Vue.use(VueAxios, request)
  }
}

export default request

export {
  installer as VueAxios,
  request as axios
}

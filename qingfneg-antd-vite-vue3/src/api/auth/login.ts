import request from '@/utils/request'
import defaultSettings from '@/config/defaultSettings'

/**
 * 登录
 * @param params 
 * @returns 
 */
export function login (params) {
  params = {...params , grant_type : 'password'}
  return request({
    url: '/oauth/token',
    method: 'post',
    data: params,
    transformRequest: [(params) => {
      return tansParams(params)
    }],
    headers: {
      'Authorization': defaultSettings.authorizationValue
    },
  })
}

/**
 * refresh刷新token
 * @param params 
 * @returns 
 */
export function refresh (params) {
  params = {...params , grant_type : 'refresh_token'}
  return request({
    url: '/oauth/token',
    method: 'post',
    data: params,
    transformRequest: [(params) => {
      return tansParams(params)
    }],
    headers: {
      'Authorization': defaultSettings.authorizationValue
    },
  })
}

function tansParams(params) {
  let result = ''
  Object.keys(params).forEach((key) => {
    if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
      result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
    }
  })
  return result
}

/**
 * getInfo获取用户详情
 * @returns 
 */
export function getInfo () {
  return request({
    url: '/system/user/findUserInfo',
    method: 'get',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
    },
  })
}

/**
 * logout登出
 * @returns 
 */
export function logout () {
  return request({
    url: '/auth/signout',
    method: 'delete',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
    },
  })
}

/**
 * getLoginUser获取登录用户
 * @returns 
 */
export function getLoginUser () {
  return request({
    url: '/system/user/findLoginUser',
    method: 'get',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
    },
  })
}

/**
 * updateUser更新用户
 * @param params 
 * @returns 
 */
export function updateMyUser (params) {
  return request({
    url: '/system/user/updateUser',
    method: 'post',
    data: params,
  })
}

/**
 * updatePwd更新密码
 * @param params 
 * @returns 
 */
export function updateMyPwd (params) {
  return request({
    url: '/system/user/updateMyPwd',
    method: 'post',
    data: params,
  })
}

/**
 * updateSwitchOrganize更新组织切换
 * @param parameter 
 * @returns 
 */
export function updateSwitchOrganize (parameter) {
  return request({
    url: '/system/user/updateSwitchOrganize',
    method: 'post',
    data: parameter,
  })
}


export function getCurrentUserNav (token: any) {
  return request({
    url: '/api/user/nav',
    method: 'get'
  })
}

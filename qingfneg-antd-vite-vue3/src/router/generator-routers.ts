// eslint-disable-next-line
import { findMenuList } from "@/api/system/menu"; 
// eslint-disable-next-line
// import { BasicLayout, BlankLayout, PageView, RouteView } from '@/layouts'
import BasicLayout from '@/layouts/BasicLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'
import RouteView from '@/layouts/RouteView.vue'
import PageView from '@/layouts/PageView.vue'
import {CustemRouter} from '@/router/types';
import router from '../router'

// 前端路由表
const constantRouterComponents = {
  // 基础页面 layout 必须引入
  BasicLayout: BasicLayout,
  BlankLayout: BlankLayout,
  RouteView: RouteView,
  PageView: PageView,
  '403': () => import('@/views/exception/403.vue'),
  '404': () => import('@/views/exception/404.vue'),
  '500': () => import('@/views/exception/500.vue'),
}

// 前端未找到页面路由（固定不用改）
const notFoundRouter: CustemRouter = {
  path: "/:pathMatch(.*)*",
  redirect: '/404',
  hidden: true
}

// 根级菜单
const rootRouter: CustemRouter = {
  key: '',
  name: 'index',
  path: '',
  component: 'BasicLayout',
  redirect: '/dashboard',
  meta: {
    title: 'menu.home'
  },
  children: []
}

/**
 * 动态生成菜单
 * @param token
 * @returns {Promise<Router>}
 */
export const generatorDynamicRouter = roles => {
  return new Promise((resolve, reject) => {
    findMenuList({ types: '0,1,3,4' })
      .then(res => {
        // console.log('generatorDynamicRouter response:', res)
        const { data } = res.data
        const treeData = fommat({
          arrayList: data,
          pidStr: "parent_id",
        });
        const accessedRouters = filterAsyncRouter(treeData, roles)
        const finalRouters = filterEmptyDirectory(accessedRouters)
        resolve(finalRouters)
      })
      .catch(err => {
        reject(err)
      })
  })
}

const modules = import.meta.glob("../views/**/**.vue")

/**
 * 过滤账户是否拥有某一个权限，并将菜单从加载列表移除
 *
 * @param permission
 * @param route
 * @returns {boolean}
 */
 function hasPermission (permission, route) {
  if (route.meta && route.meta.permission) {
    let flag = false
    for (let i = 0, len = permission.length; i < len; i++) {
      flag = route.meta.permission.includes(permission[i])
      if (flag) {
        return true
      }
    }
    return false
  }
  return true
}

function filterAsyncRouter (routerMap, roles) {
  return routerMap.filter(route => {
    if (hasPermission(roles.permissionList, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, roles)
      }
      return true
    }
    return false
  })
}

function filterEmptyDirectory(routerMap) {
  const accessedRouters = routerMap.filter(route => {
    if (route.children && route.children.length) {
      route.children = filterEmptyDirectory(route.children)
      return true
    } else {
      // if (route.meta.permission.includes('system')) {
      //   return false
      // } else {
      //   return true
      // }
      return true
    }
  })
  return accessedRouters
}

function fommat({
  arrayList,
  pidStr = "parent_id",
  idStr = "id",
  childrenStr = "children",
}) {
  arrayList.push({
    path: "/",
    name: "index",
    component: BasicLayout,
    title: '主页',
    redirect: '/dashboard/welcome',
    id: "1",
    child_num: 1
  });
  let listOjb = {}; // 用来储存{key: obj}格式的对象
  let treeList = []; // 用来储存最终树形结构数据的数组
  // 将数据变换成{key: obj}格式，方便下面处理数据
  for (let i = 0; i < arrayList.length; i++) {
    var data = arrayList[i];
    // if(data.path.indexOf('?')!=-1){
    //   data.param = data.path.substring(data.path.indexOf('?'));
    //   data.path = data.path.substring(0,data.path.indexOf('?'));
    // }

    data.key = data.id;
    data.icon = "";
    //处理菜单格式信息
    if (data.type == '0') {//目录
      data.component = RouteView
    } else if (data.type == '1') {
      const views = data.component;
      if (views == '/dashboard/welcome' || views == '/dashboard/Welcome') {
        data.component = () => import(`@/views/dashboard/Welcome.vue`)
      } else {
        let vurl = `../views${views}/Index.vue`;
        data.component = modules[vurl] as any;
        // data.component = () => import(`@/views${views}/Index.vue`)
        // data.component = (resolve) => require([`@/views${views}/Index`], resolve)
      }
      // data.component = () => import(`@${views}`)
      // data.component = () => import('@/views${component}')
    } else if (data.type == '4') {
      data.component = () => import('@/views/system/Iframe/Index.vue')
    }
    let role_ids = data.role_ids?.split(',')
    if (data.child_num > 0) {
      if (role_ids == undefined) {
        role_ids = ['system']
      } else {
        role_ids.push('system');
      }
    }

    if (data.type == '3') {
      data.meta = {
        title: data.title,
        keepAlive: data.keepAlive,
        icon: data.qf_icon,
        permission: role_ids,
        target: '_black'
      }
    } else {
      data.meta = {
        title: data.title,
        keepAlive: data.keepAlive,
        icon: data.qf_icon,
        permission: role_ids
      }
    }

    // console.log(data.meta)
    listOjb[arrayList[i][idStr]] = data;
  }
  // 根据pid来将数据进行格式化
  for (let j = 0; j < arrayList.length; j++) {
    // 判断父级是否存在
    let haveParent = listOjb[arrayList[j][pidStr]];
    if (haveParent) {
      // 如果有没有父级children字段，就创建一个children字段
      !haveParent[childrenStr] && (haveParent[childrenStr] = []);
      // 在父级里插入子项
      haveParent[childrenStr].push(arrayList[j]);
    } else {
      // 如果没有父级直接插入到最外层
      treeList.push(arrayList[j]);
    }
  }
  return treeList;
}

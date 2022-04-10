import type { App, Directive, DirectiveBinding } from 'vue';
import store from '@/store'


const authDirective: Directive = {
  mounted (el, binding) {
    const actionName = binding.arg
    const roles = store.getters.roles
    const permissionButtonList = roles.permissionButtonList
    if(permissionButtonList!=null){
      if(!permissionButtonList.includes(actionName)){
        el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
      }
    }
  },
};

/**
 * Action 权限指令
 * 指令用法：
 *  - 在需要控制 action 级别权限的组件上使用 v-action:[method] , 如下：
 *    <a-button v-action:add >添加用户</a-button>
 *    <a-button v-action:delete>删除用户</a-button>
 *    <a v-action:edit @click="edit(record)">修改</a>
 *
 *  - 当前用户没有权限时，组件上使用了该指令则会被隐藏
 *  - 当后台权限跟 提供的模式不同时，只需要针对这里的权限过滤进行修改即可
 */
export function setupActionDirective(app: App) {
  app.directive('action', authDirective);
}
export default authDirective;




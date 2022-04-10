import type { App, Directive, DirectiveBinding } from 'vue';
import store from '@/store'


const authDirective: Directive = {
  mounted (el, binding) {
      const roles = store.getters.roles
      const permissionButtonList = roles.permissionButtonList
      const authOrganize = roles.authOrganize;
      const record = binding.arg;
      const actionName = record.auth;
  
      let authOrgIds = authOrganize.authOrgIds;
      let authParams = authOrganize.authParams;
      let user_id = authOrganize.user_id;
      var createParams = record.create_organize + ":Y";
  
      let bol = false;
      if (permissionButtonList.includes(actionName)) {
        if (authOrgIds == "" || authOrgIds == undefined) {
          if (user_id == record.create_user) {
            bol = true;
          }
        } else {
          if (
            authParams.indexOf(createParams) > -1 ||
            user_id == record.create_user
          ) {
            bol = true;
          }
        }
      }
      if (!bol) {
        el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
      }
    },
};

/**
 * Action 权限指令 - 数据权限
 * 指令用法：
 *  - 在需要控制 action 级别权限的组件上使用 v-action:[method] , 如下：
 *    <a-button v-action:add >添加用户</a-button>
 *    <a-button v-action:delete>删除用户</a-button>
 *    <a v-action:edit @click="edit(record)">修改</a>
 *
 *  - 当前用户没有权限时，组件上使用了该指令则会被隐藏
 *  - 当后台权限跟 提供的模式不同时，只需要针对这里的权限过滤进行修改即可
 */
export function setupActionDAuthDirective(app: App) {
  app.directive('action-dauth', authDirective);
}
export default authDirective;

import { createApp } from 'vue'
import App from './App.vue'
import store from '@/store';
import 'vite-plugin-svg-icons/register';
import i18n from '@/locales'
import bootstrap from '@/core/bootstrap'
import { lazyApp } from '@/core/lazy_use'
import ProLayout, { PageContainer } from '@ant-design-vue/pro-layout'
import './style/global.less' // global style
import permission from './permission'
import router from '@/router'
import { Tree ,Transfer } from 'ant-design-vue'
//导入所有图标库
import * as Icons from "@ant-design/icons-vue";

import { setupGlobDirectives } from '@/core/directives';

const vueApp = createApp(App)

vueApp.use(router)
vueApp.use(store)
vueApp.use(i18n)
vueApp.use(ProLayout)
vueApp.use(PageContainer)
vueApp.component('page-header-wrapper', PageContainer)
vueApp.use(lazyApp)
vueApp.use(bootstrap)
vueApp.use(Tree)
vueApp.use(Transfer)
vueApp.use(permission)

// 全局指令
setupGlobDirectives(vueApp);

vueApp.mount('#app')

// 全局使用图标
const icons = Icons;
for(const i in icons){
    vueApp.component(i,icons[i]);
}
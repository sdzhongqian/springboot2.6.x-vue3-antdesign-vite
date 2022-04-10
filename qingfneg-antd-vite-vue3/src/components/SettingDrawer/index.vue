<template>
  <a-drawer v-model:visible="visible" :width="300" placement="right" :closable="false">
    <template #handle>
      <div class="ant-pro-setting-drawer-handle" @click="handleShowDrawer">
        <SettingOutlined v-if="!visible" />
        <CloseOutlined v-else />
      </div>
    </template>



    <div class="margin-bottom: 24px">
      <h3>整体风格设置</h3>
      <a-radio-group
        :value="modelValue.navTheme"
        @change="({ target }: { target: HTMLInputElement }) => updateConf(target.value, 'navTheme')"
      >
        <a-radio value="dark">黑色主题</a-radio>
        <a-radio value="light">亮色主题</a-radio>
      </a-radio-group>
      <a-divider />

      <h3>主题色</h3>
      <a-tooltip
        class="setting-drawer-theme-color-colorBlock"
        v-for="(item, index) in colorList"
        :key="index"
      >
        <template #title>{{ item.key }}</template>
        <a-tag :color="item.color" @click="changeColor(item.color)">
          <CheckOutlined v-if="item.color === modelValue.primaryColor" />
        </a-tag>
      </a-tooltip>
      <a-divider />

      <h3>导航模式</h3>
      <a-radio-group
        :value="modelValue.layout"
        @change="({ target }: { target: HTMLInputElement }) => updateConf(target.value, 'layout')"
      >
        <a-radio value="side">左侧菜单布局</a-radio>
        <a-radio value="top">顶部菜单布局</a-radio>
        <a-radio value="mix">混合菜单布局</a-radio>
      </a-radio-group>
      <a-divider />
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">固定 Header</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.fixedHeader"
            @change="(checked: CheckedType) => updateConf(checked, 'fixedHeader')"
          />
        </a-col>
      </a-row>
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">固定 左侧菜单</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.fixSiderbar"
            @change="(checked: CheckedType) => updateConf(checked, 'fixSiderbar')"
          />
        </a-col>
      </a-row>
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">自动分割菜单</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.splitMenus"
            @change="(checked: CheckedType) => updateConf(checked, 'splitMenus')"
          />
        </a-col>
      </a-row>

      <a-divider />
      <h3>内容区域</h3>
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">顶栏</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.headerRender === undefined"
            @change="(checked: CheckedType) => updateConf(checked === true && undefined, 'headerRender')"
          />
        </a-col>
      </a-row>
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">页脚</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.footerRender === undefined"
            @change="(checked: CheckedType) => updateConf(checked === true && undefined, 'footerRender')"
          />
        </a-col>
      </a-row>
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">菜单</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            disabled
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.menu === undefined"
            @change="(checked: CheckedType) => updateConf(checked === true && undefined, 'menu')"
          />
        </a-col>
      </a-row>
      <a-row style="margin-bottom: 12px">
        <a-col :span="12">菜单头</a-col>
        <a-col :span="12" style="text-align: right">
          <a-switch
            checked-children="开"
            un-checked-children="关"
            :checked="modelValue.menuHeaderRender === undefined"
            @change="(checked: CheckedType) => updateConf(checked === true && undefined, 'menuHeaderRender')"
          />
        </a-col>
      </a-row>
    </div>
    <div :style="{ marginBottom: '24px' }">
        <a-button @click="doCopy" block>
          <template #icon>
            <CopyOutlined />
            拷贝设置
          </template>
        </a-button>
        <a-alert type="warning" :style="{ marginTop: '24px' }">
          <template #message>
            <span>
              配置的设置可以复制到defaultSettings.ts中作为项目的默认设置。修改配置文件后，需要清空本地缓存和LocalStorage src/config/defaultSettings.ts
            </span>
          </template>
        </a-alert>
      </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { defineComponent, ref, toRaw } from 'vue'
import { updateTheme } from './updateTheme'
import { colorList } from './settingConfig'
import useClipboard from 'vue-clipboard3'
import {message, notification} from 'ant-design-vue'

type CheckedType = boolean | string | number;
    type ConfType = 'layout' | 'fixedHeader' | 'fixSiderbar' | string;

    const props = defineProps<{
      modelValue: Record<string, any>;
    }>();
    const emit = defineEmits(['update:modelValue']);

    const visible = ref<boolean>(false);
    const handleShowDrawer = () => {
      visible.value = !visible.value;
    };

    const updateConf = (val: any, type: ConfType) => {
      // props.modelValue.primaryColor = "#009688";
      // props.modelValue.navTheme = "light";
      const newVal = {
        ...toRaw(props.modelValue),
        [`${type}`]: val
      };
      
      console.log('newConf', newVal);
      emit('update:modelValue', newVal);
      // updateTheme(props.modelValue.primaryColor);
    };
    
    const changeColor = (color) => {
      props.modelValue.primaryColor = color;
      emit('update:modelValue.primaryColor', color)
      console.log(color)
      updateTheme(color)
    }

    const { toClipboard } = useClipboard()
    const doCopy = () => {
       const text = `export default {
        navTheme: `+props.modelValue.navTheme+`, // theme for nav menu
        primaryColor: `+props.modelValue.primaryColor+`, // primary color of ant design
        layout: `+props.modelValue.layoutMode+`, // nav menu position: side or top or mix
        contentWidth: `+props.modelValue.contentWidth+`, // layout of content: Fluid or Fixed, only works when layout is topmenu
        autoHideHeader: `+props.modelValue.autoHideHeader+`,
        fixedHeader: `+props.modelValue.fixedHeader+`, // sticky header
        fixSiderbar: `+props.modelValue.fixSiderbar+`, // sticky siderbar
        colorWeak: `+props.modelValue.colorWeak+`,
        menu: {
          locale: true
        },
        multiTab: false,
        title: '青锋系统',
        pwa: false,
        iconfontUrl: '',
        production: process.env.NODE_ENV === 'production' && process.env.VUE_APP_PREVIEW !== 'true'
      }`
      toClipboard(text)
        .then((msg) => {
          message.success('复制完毕')
        })
        .catch((err) => {
          message.error('复制失败' + err)
        })
    }
</script>

<style lang="less">
.setting-drawer-theme-color-colorBlock {
  width: 20px;
  height: 20px !important;
  border-radius: 2px;
  float: left;
  cursor: pointer;
  margin-right: 8px;
  padding: 0px !important;
  text-align: center;
  color: #fff;
  font-weight: 700;

  i {
    font-size: 14px;
  }
}
.ant-pro-setting-drawer-handle {
  position: absolute;
  top: 240px;
  right: 300px;
  z-index: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  font-size: 16px;
  text-align: center;
  background: @primary-color;
  border-radius: 4px 0 0 4px;
  cursor: pointer;
  pointer-events: auto;

  > span {
    color: rgb(255, 255, 255);
    font-size: 20px;
  }
}
</style>

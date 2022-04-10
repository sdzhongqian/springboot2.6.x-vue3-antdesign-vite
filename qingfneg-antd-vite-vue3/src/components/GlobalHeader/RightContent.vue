<template>
   <div :style="{ display: 'inline', margin: '0 8px', fontSize: '20px' }" @click="onLockScreen">
      <a-tooltip title="锁屏">
        <LockOutlined />
      </a-tooltip>
    </div>
  <div :style="{ margin: '0 8px', fontSize: '16px', cursor: 'pointer' }" :class="wrpCls">
    <avatar-dropdown :current-user="currentUser" :class="prefixCls" />
  </div>
  <div :style="{ display: 'flex', margin: '0 8px', fontSize: '20px', cursor: 'pointer' }" :class="wrpCls">
    <select-lang :class="prefixCls" />
  </div>
</template>

<script lang="ts">
import {defineComponent, computed, onMounted, reactive, UnwrapRef} from 'vue'
import AvatarDropdown from './AvatarDropdown.vue'
import SelectLang from '@/components/SelectLang'
import storage from 'store';
// import store from '@/store';
import { useStore } from 'vuex';
import { SET_LOCK_SCREEN } from '@/store/mutation-types'

export default defineComponent({
  name: 'RightContent',
  components: {
    AvatarDropdown,
    SelectLang
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-global-header-index-action'
    },
    isMobile: {
      type: Boolean,
      default: () => false
    },
    topMenu: {
      type: Boolean,
      required: false
    },
    theme: {
      type: String,
      required: false
    }
  },
  setup(props){
    const currentUser: UnwrapRef<{[p: string]: any}> = reactive({name: String})
    const wrpCls = computed(() => {
      return {
        'ant-pro-global-header-index-right': true,
        [`ant-pro-global-header-index-${(props.isMobile || !props.topMenu) ? 'light' : props.theme}`]: true
      }
    })
    const store = useStore();
    const onLockScreen = () => {
      store.commit(SET_LOCK_SCREEN, true)
      storage.set(SET_LOCK_SCREEN,true);
    }

    onMounted(() =>{
      setTimeout( () => {
        currentUser.name = store.getters.nickname
      }, 1000)
    })

    return{
      wrpCls,
      currentUser,
      onLockScreen
    }
  }
})
</script>

<style lang="less" scoped>
</style>

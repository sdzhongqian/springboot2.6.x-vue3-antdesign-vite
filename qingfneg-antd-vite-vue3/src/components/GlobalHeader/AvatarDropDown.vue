<template>
  <a-dropdown v-if="currentUser && currentUser.name" placement="bottomRight">
    <template #overlay>
      <a-menu class="ant-pro-drop-down menu" :selected-keys="[]">
        <a-menu-item v-if="menu" key="center" @click="updateUser()">
          <template #icon>
            <SettingOutlined />
          </template>
          <span> {{ $t("menu.account.center") }}</span>
        </a-menu-item>
        <a-menu-item v-if="menu" key="settings" @click="updatePwd()">
          <template #icon>
            <SettingOutlined />
          </template>
          <span>{{ $t("menu.password.settings") }}</span>
        </a-menu-item>
        <a-menu-item v-if="menu" key="settings" @click="switchOrganize()">
          <template #icon>
            <SettingOutlined />
          </template>
          <span>{{ $t("menu.organize.switch") }}</span>
        </a-menu-item>
        <a-menu-divider v-if="menu" />
        <a-menu-item key="logout" @click="handleLogout">
          <template #icon>
            <LogoutOutlined />
          </template>
          <span>{{ $t("menu.account.logout") }}</span>
        </a-menu-item>
      </a-menu>
    </template>
    <span class="ant-pro-account-avatar">
      <a-avatar size="small">
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <span>{{ currentUser.name }}</span>
    </span>
  </a-dropdown>
  <span v-else>
    <a-spin size="small" :style="{ marginLeft: 8, marginRight: 8 }" />
  </span>
  <edit-user
    v-if="visibleEditUser"
    ref="editUserModal"
    :visible="visibleEditUser"
    :loading="confirmLoading"
    :model="mdl"
    @cancel="handleEditUserCancel"
    @ok="handleEditUserOk"
  />
  <edit-user-pwd
    v-if="visibleEditUserPwd"
    ref="editUserPwdModal"
    :visible="visibleEditUserPwd"
    :loading="confirmLoading"
    :model="mdl"
    @cancel="handleEditUserPwdCancel"
    @ok="handleEditUserPwdOk"
  />
  <switch-organize
    v-if="visibleSwitchOrganize"
    ref="switchOrganizeModal"
    :visible="visibleSwitchOrganize"
    :loading="confirmLoading"
    :model="mdl"
    @cancel="handleSwitchOrganizeCancel"
    @ok="handleSwitchOrganizeOk"
  />
</template>

<script lang="ts">
import { defineComponent, computed, ref, unref } from "vue";
import { message, Form, Modal } from "ant-design-vue";
import { useI18n } from "vue-i18n";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

import EditUser from "./EditUser.vue";
import EditUserPwd from "./EditUserPwd.vue";
import SwitchOrganize from "./SwitchOrganize.vue";
import {
  updateMyPwd,
  updateMyUser,
  updateSwitchOrganize,
} from "@/api/auth/login";

export default defineComponent({
  name: "AvatarDropDown",
  props: {
    currentUser: {
      type: Object,
      "{[p: string]: any}": () => null,
    },
    menu: {
      type: Boolean,
      default: true,
    },
  },
  components: {
    EditUser,
    EditUserPwd,
    SwitchOrganize,
  },
  setup() {
    const router = useRouter();
    const { t } = useI18n();
    const store = useStore();
    //modal
    const editUserModal = ref(null);
    const editUserPwdModal = ref(null);
    const switchOrganizeModal = ref(null);
    // 是否展示弹框
    const visibleEditUser = ref(false);
    const visibleEditUserPwd = ref(false);
    const visibleSwitchOrganize = ref(false);

    const confirmLoading = ref(false);
    const mdl = ref(null);

    const updateUser = (record) => {
      mdl.value = null;
      visibleEditUser.value = true;
    };

    const updatePwd = (record) => {
      mdl.value = null;
      visibleEditUserPwd.value = true;
    };

    const switchOrganize = (record) => {
      mdl.value = null;
      visibleSwitchOrganize.value = true;
    };

    //取消弹框
    const handleEditUserCancel = () => {
      visibleEditUser.value = false;
    };
    const handleEditUserPwdCancel = () => {
      visibleEditUserPwd.value = false;
    };
    const handleSwitchOrganizeCancel = () => {
      visibleSwitchOrganize.value = false;
    };

    //处理确认事件
    const handleEditUserOk = () => {
      editUserModal.value.validate().then((result) => {
        confirmLoading.value = true;
        new Promise((resolve, reject) => {
          updateMyUser(result).then((response) => {
            resolve(response);
          });
        }).then((res) => {
          confirmLoading.value = false;
          if (res.data.data.success) {
            visibleEditUser.value = false;
            message.success(res.data.data.msg);
          } else {
            message.error(res.data.data.msg);
          }
        });
      });
    };
    const handleEditUserPwdOk = () => {
      editUserPwdModal.value.validate().then((result) => {
        confirmLoading.value = true;
        new Promise((resolve, reject) => {
          updateMyPwd(result).then((response) => {
            resolve(response);
          });
        }).then((res) => {
          confirmLoading.value = false;
          if (res.data.data.success) {
            visibleEditUserPwd.value = false;
            message.success(res.data.data.msg);
          } else {
            message.error(res.data.data.msg);
          }
        });
      });
    };
    const handleSwitchOrganizeOk = () => {
      confirmLoading.value = true;
      updateSwitchOrganize({
        organize_id: switchOrganizeModal.value.organize_id,
      }).then((response) => {
        if (response.status == 200) {
          message.success("组织切换成功，请重新登录");
          //需要重新登录
          return store.dispatch("Logout").then(() => {
            router.push({ name: "login" });
          });
        } else {
          message.error("组织切换失败");
        }
      });
      visibleSwitchOrganize.value = false;
    };

    const handleLogout = () => {
      Modal.confirm({
        title: t("layouts.usermenu.dialog.title"),
        content: t("layouts.usermenu.dialog.content"),
        onOk: () => {
          // return new Promise((resolve, reject) => {
          //   setTimeout(Math.random() > 0.5 ? resolve : reject, 1500)
          // }).catch(() => console.log('Oops errors!'))
          return store.dispatch("Logout").then(() => {
            router.push({ name: "login" });
          });
        },
      });
    };
    return {
      handleLogout,
      editUserModal,
      editUserPwdModal,
      switchOrganizeModal,
      visibleEditUser,
      visibleEditUserPwd,
      visibleSwitchOrganize,
      confirmLoading,
      mdl,
      updateUser,
      updatePwd,
      switchOrganize,
      handleEditUserCancel,
      handleEditUserPwdCancel,
      handleSwitchOrganizeCancel,
      handleEditUserOk,
      handleEditUserPwdOk,
      handleSwitchOrganizeOk,
    };
  },
});
</script>

<style lang="less" scoped>
</style>

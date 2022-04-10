<!--  -->
<template>
  <div>
    <a-divider orientation="left"> 单选用户 </a-divider>
    <a-input v-model:value="user.user_id" placeholder="用户id" />
    <a-input v-model:value="user.user_name" placeholder="用户名称" />
    <a-button @click="selectOneUser" type="primary"> 选择单用户 </a-button>

    <a-divider orientation="left"> 多选用户 </a-divider>
    <a-input v-model:value="users.user_ids" placeholder="用户ids" />
    <a-input v-model:value="users.user_names" placeholder="用户名称s" />
    <a-button @click="selectMoreUser" type="primary"> 选择多用户 </a-button>

    <a-divider orientation="left"> 单选组织 </a-divider>
    <a-input v-model:value="organize.organize_id" placeholder="组织id" />
    <a-input v-model:value="organize.organize_name" placeholder="组织名称" />
    <a-button @click="selectOneOrganize" type="primary"> 选择单组织 </a-button>

    <a-divider orientation="left"> 多选组织 </a-divider>
    <a-input v-model:value="organizes.organize_ids" placeholder="组织ids" />
    <a-input v-model:value="organizes.organize_names" placeholder="组织名称s" />
    <a-button @click="selectMoreOrganize" type="primary"> 选择多组织 </a-button>

    <select-one-user
      v-if="visibleOneUser"
      ref="oneUserModal"
      :visible="visibleOneUser"
      :loading="confirmLoading"
      :model="mdl"
      @cancel="handleCancel('oneUser')"
      @ok="handleOk('oneUser')"
    />
    <select-more-user
      v-if="visibleMoreUser"
      ref="moreUserModal"
      :visible="visibleMoreUser"
      :loading="confirmLoading"
      :model="mdl"
      @cancel="handleCancel('moreUser')"
      @ok="handleOk('moreUser')"
    />
    <select-one-organize
      v-if="visibleOneOrganize"
      ref="oneOrganizeModal"
      :visible="visibleOneOrganize"
      :loading="confirmLoading"
      :model="mdl"
      @cancel="handleCancel('oneOrganize')"
      @ok="handleOk('oneOrganize')"
    />
    <select-more-organize
      v-if="visibleMoreOrganize"
      ref="moreOrganizeModal"
      :visible="visibleMoreOrganize"
      :loading="confirmLoading"
      :model="mdl"
      @cancel="handleCancel('moreOrganize')"
      @ok="handleOk('moreOrganize')"
    />
  </div>
</template>

<script>
import { defineComponent, computed, ref, unref } from "vue";
import SelectOneUser from "@/views/system/user/SelectOneUser.vue";
import SelectMoreUser from "@/views/system/user/SelectMoreUser.vue";
import SelectOneOrganize from "@/views/system/organize/SelectOneOrganize.vue";
import SelectMoreOrganize from "@/views/system/organize/SelectMoreOrganize.vue";

export default defineComponent({
  name: "TableList",
  components: {
    SelectOneUser,
    SelectMoreUser,
    SelectOneOrganize,
    SelectMoreOrganize,
  },
  setup() {
    //选择数据
    const user = ref({
      user_id: "",
      user_name: "",
    });
    const users = ref({
      user_ids: "",
      user_names: "",
    });
    const organize = ref({
      organize_id: "",
      organize_name: "",
    });
    const organizes = ref({
      organize_ids: "",
      organize_names: "",
    });
    //弹框model
    const oneUserModal = ref(null);
    const moreUserModal = ref(null);
    const oneOrganizeModal = ref(null);
    const moreOrganizeModal = ref(null);
    // 是否展示弹框
    const visibleOneUser = ref(false);
    const visibleMoreUser = ref(false);
    const visibleOneOrganize = ref(false);
    const visibleMoreOrganize = ref(false);
    const confirmLoading = ref(false);
    const mdl = ref(null);

    //执行保存
    const handleOk = (type) => {
      confirmLoading.value = true;
      if (type == "oneUser") {
        const sdata = oneUserModal.value.sdata;
        if (sdata != "" && sdata != undefined) {
          new Promise((resolve, reject) => {
            user.value.user_id = sdata.split(":")[0];
            user.value.user_name = sdata.split(":")[1];
          });
        } else {
          user.value.user_id = "";
          user.value.user_name = "";
        }
        visibleOneUser.value = false;
        confirmLoading.value = false;
      } else if (type == "moreUser") {
        const sdata = moreUserModal.value.sdata;
        if (sdata != "" && sdata != undefined) {
          new Promise((resolve, reject) => {
            let ids = [];
            let names = [];
            sdata.forEach((item) => {
              console.log(item);
              ids.push(item.split(":")[0]);
              names.push(item.split(":")[1]);
            });
            users.value.user_ids = ids.join(",");
            users.value.user_names = names.join(",");
          });
        } else {
          users.value.user_ids = "";
          users.value.user_names = "";
        }
        visibleMoreUser.value = false;
        confirmLoading.value = false;
      } else if (type == "oneOrganize") {
        const sdata = oneOrganizeModal.value.sdata;
        if (sdata != "" && sdata != undefined) {
          new Promise((resolve, reject) => {
            organize.value.organize_id = sdata.split(":")[0];
            organize.value.organize_name = sdata.split(":")[1];
          });
        } else {
          organize.value.organize_id = "";
          organize.value.organize_name = "";
        }
        visibleOneOrganize.value = false;
        confirmLoading.value = false;
      } else if (type == "moreOrganize") {
        const sdata = moreOrganizeModal.value.sdata;
        if (sdata != "" && sdata != undefined) {
          new Promise((resolve, reject) => {
            let ids = [];
            let names = [];
            sdata.forEach((item) => {
              console.log(item);
              ids.push(item.split(":")[0]);
              names.push(item.split(":")[1]);
            });
            organizes.value.organize_ids = ids.join(",");
            organizes.value.organize_names = names.join(",");
          });
        } else {
          organizes.value.organize_ids = "";
          organizes.value.organize_names = "";
        }
        visibleMoreOrganize.value = false;
        confirmLoading.value = false;
      }
    };

    //取消弹框
    const handleCancel = (type) => {
      if (type == "oneUser") {
        visibleOneUser.value = false;
      } else if (type == "moreUser") {
        visibleMoreUser.value = false;
      } else if (type == "oneOrganize") {
        visibleOneOrganize.value = false;
      } else if (type == "moreOrganize") {
        visibleMoreOrganize.value = false;
      }
    };

    //展示新增页面
    const selectOneUser = () => {
      mdl.value = {
        ...user.value,
      };
      visibleOneUser.value = true;
    };
    const selectMoreUser = () => {
      mdl.value = {
        ...users.value,
      };
      visibleMoreUser.value = true;
    };
    const selectOneOrganize = () => {
      mdl.value = {
        ...organize.value,
      };
      visibleOneOrganize.value = true;
    };
    const selectMoreOrganize = () => {
      mdl.value = {
        ...organizes.value,
      };
      visibleMoreOrganize.value = true;
    };

    return {
      user,
      users,
      organize,
      organizes,
      oneUserModal,
      moreUserModal,
      oneOrganizeModal,
      moreOrganizeModal,
      visibleOneUser,
      visibleMoreUser,
      visibleOneOrganize,
      visibleMoreOrganize,
      confirmLoading,
      mdl,
      handleOk,
      handleCancel,
      selectOneUser,
      selectMoreUser,
      selectOneOrganize,
      selectMoreOrganize,
    };
  },
});
</script>
<style scoped>
/* @import url(); 引入css类 */
</style>
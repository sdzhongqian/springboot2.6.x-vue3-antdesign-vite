<template>
  <a-modal
    :title="title"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
  >
    <a-transfer
      :list-style="{
        width: '320px',
        height: '300px',
        marginLeft: '0px',
      }"
      v-model:target-keys="targetKeys"
      v-model:selected-keys="selectedKeys"
      :data-source="authData"
      :titles="['角色', '授权角色']"
      :render="(item) => item.title"
      @change="handleChange"
      @selectChange="handleSelectChange"
      @scroll="handleScroll"
    />
  </a-modal>
</template>
<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import { findRoleAuth } from "@/api/system/organize";
export default defineComponent({
  props: {
    visible: {
      type: Boolean,
      required: true,
    },
    loading: {
      type: Boolean,
      default: () => false,
    },
    model: {
      type: Object,
      default: () => null,
    },
  },
  setup(props) {
    const { model } = props;
    const title = ref("权限分配");

    const authData = ref([]);
    const targetKeys = ref<string[]>([]);
    const selectedKeys = ref<string[]>([]);

    const handleChange = (
      nextTargetKeys: string[],
      direction: string,
      moveKeys: string[]
    ) => {
      //   console.log("targetKeys: ", nextTargetKeys);
      //   console.log("direction: ", direction);
      //   console.log("moveKeys: ", moveKeys);
    };
    const handleSelectChange = (
      sourceSelectedKeys: string[],
      targetSelectedKeys: string[]
    ) => {
      //   console.log("sourceSelectedKeys: ", sourceSelectedKeys);
      //   console.log("targetSelectedKeys: ", targetSelectedKeys);
    };
    const handleScroll = (direction: string, e: Event) => {
      //   console.log("direction:", direction);
      //   console.log("target:", e.target);
    };

    // 自动请求并暴露内部方法
    onMounted(() => {
      getRoleAuth();
    });

    const getRoleAuth = () => {
      findRoleAuth({ id: model?.id }).then((response) => {
        let respData = response.data.data;
        for (let i = 0; i < respData.roleLs.length; i++) {
          respData.roleLs[i].key = respData.roleLs[i].id;
          respData.roleLs[i].title = respData.roleLs[i].name;
          respData.roleLs[i].description = respData.roleLs[i].name;
          authData.value.push({
            key: respData.roleLs[i].id,
            title: respData.roleLs[i].name,
            description: respData.roleLs[i].name,
          });
        }
        for (let i = 0; i < respData.myRoleLs.length; i++) {
          authData.value.push({
            key: respData.myRoleLs[i].id,
            title: respData.myRoleLs[i].name,
            description: respData.myRoleLs[i].name,
          });
          targetKeys.value.push(respData.myRoleLs[i].id);
        }
      });
    };

    return {
      authData,
      targetKeys,
      selectedKeys,
      handleChange,
      handleSelectChange,
      handleScroll,
      title,
    };
  },
});
</script>


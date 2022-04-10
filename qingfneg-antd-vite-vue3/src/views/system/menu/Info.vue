<template>
  <a-modal
    :title="title"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @cancel="
      () => {
        $emit('cancel');
      }
    "
  >
    <template #footer>
      <a-button
        key="submit"
        type="primary"
        :loading="loading"
        @click="
          () => {
            $emit('cancel');
          }
        "
        >确定</a-button
      >
    </template>
    <a-descriptions-item label="父级名称">{{
      model?.parent_name
    }}</a-descriptions-item>
    <a-descriptions title="">
      <a-descriptions-item label="菜单类型"
        ><span v-if="model?.type == '0'"> 目录 </span>
        <span v-if="model?.type == '1'"> 菜单 </span>
        <span v-if="model?.type == '2'"> 按钮 </span>
        <span v-if="model?.type == '3'"> 外链 </span>
        <span v-if="model?.type == '4'"> iframe </span></a-descriptions-item
      >
      <a-descriptions-item label="菜单名称">{{
        model?.short_name
      }}</a-descriptions-item>
      <a-descriptions-item
        label="路由地址"
        v-show="
          model?.type == '0' ||
          model?.type == '1' ||
          model?.type == '3' ||
          model?.type == '4'
        "
        >{{ model?.path }}</a-descriptions-item
      >

      <a-descriptions-item label="重定向地址" v-show="model?.type == '0'">{{
        model?.redirect
      }}</a-descriptions-item>
      <a-descriptions-item label="组件路径" v-show="model?.type == '1'">{{
        model?.component
      }}</a-descriptions-item>
      <a-descriptions-item
        label="权限标识"
        v-show="model?.type == '1' || model?.type == '2'"
        >{{ model?.permission }}</a-descriptions-item
      >
      <a-descriptions-item
        label="菜单图标"
        v-show="
          model?.type == '0' ||
          model?.type == '1' ||
          model?.type == '3' ||
          model?.type == '4'
        "
        >{{ model?.icon }}</a-descriptions-item
      >
      <a-descriptions-item
        label="是否缓存"
        v-show="
          model?.type == '0' ||
          model?.type == '1' ||
          model?.type == '3' ||
          model?.type == '4'
        "
      >
        <span v-if="model?.keepAlive == 'true'"> 缓存 </span>
        <span v-if="model?.keepAlive == 'false'"> 不缓存 </span>
      </a-descriptions-item>
      <a-descriptions-item label="排序">{{
        model?.order_by
      }}</a-descriptions-item>
      <a-descriptions-item label="备注">{{
        model?.remark
      }}</a-descriptions-item>
      <a-descriptions-item label="创建时间">{{
        model?.create_time
      }}</a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form } from "ant-design-vue";
// import pick from 'lodash.pick'

interface Fields {
  id?: number;
  name?: string;
  short_name?: string;
  order_by?: string;
  remark?: string;
}

const useForm = Form.useForm;
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
    const formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
    };

    const title = ref("新增数据");

    // 自动请求并暴露内部方法
    onMounted(() => {});

    return {
      formLayout,
      title,
    };
  },
});
</script>

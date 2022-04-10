<template>
  <a-modal
    :title="title"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
  >
    <div>
      <p>当前组织：{{ organize_name }}</p>
      <a-divider style="background: #1890ff" />
      <a-radio-group
        name="organize_id"
        v-for="(item, index) in data"
        v-model:value="current_organize"
        @change="onChange"
        :key="item.id"
      >
        <a-radio :value="item.organize_id + ':' + item.organize_name">
          {{ item.organize_name }}
        </a-radio>
      </a-radio-group>
    </div>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form } from "ant-design-vue";
import { getLoginUser } from "@/api/auth/login";

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
    const title = ref("组织切换");

    const current_organize = ref("");
    const organize_id = ref("");
    const organize_name = ref("");
    const data = ref([]);

    // 自动请求并暴露内部方法
    onMounted(() => {
      getLoginUser().then((response) => {
        data.value = response.data.data.organizeList;
        for (let i = 0; i < data.value.length; i++) {
          if (data.value[i].use_status == "0") {
            current_organize.value =
              data.value[i].organize_id + ":" + data.value[i].organize_name;
            organize_id.value = data.value[i].organize_id;
            organize_name.value = data.value[i].organize_name;
          }
        }
      });
    });

    const onChange = (e) => {
      const value = e.target.value.split(":");
      organize_id.value = value[0];
      organize_name.value = value[1];
    };

    return {
      formLayout,
      title,
      current_organize,
      organize_id,
      organize_name,
      data,
      onChange,
    };
  },
});
</script>

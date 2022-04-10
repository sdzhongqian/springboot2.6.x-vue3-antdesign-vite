<template>
  <a-modal
    :title="title"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="
      () => {
        $emit('ok');
      }
    "
    @cancel="
      () => {
        $emit('cancel');
      }
    "
  >
    <a-spin :spinning="loading">
      <a-form v-bind="formLayout">
        <!-- 检查是否有 id 并且大于0，大于0是修改。其他是新增，新增不显示主键ID -->
        <a-form-item v-show="false" label="主键ID">
          <a-input v-model:value="formRef.id" disabled />
        </a-form-item>
        <a-form-item v-bind="validateInfos.job_name" label="任务名称">
          <a-input v-model:value="formRef.job_name" placeholder="任务名称" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.job_group" label="任务分组">
          <a-input v-model:value="formRef.job_group" placeholder="任务分组" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.job_class_name" label="执行类">
          <a-input
            v-model:value="formRef.job_class_name"
            placeholder="执行类"
          />
        </a-form-item>
        <a-form-item v-bind="validateInfos.cron_expression" label="执行时间">
          <a-input
            v-model:value="formRef.cron_expression"
            placeholder="执行时间"
          />
        </a-form-item>
        <a-form-item v-bind="validateInfos.description" label="任务描述">
          <a-textarea
            v-model:value="formRef.description"
            placeholder="任务描述"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form } from "ant-design-vue";
// import pick from 'lodash.pick'

interface Fields {
  id?: number;
  job_name?: string;
  job_group?: string;
  job_class_name?: string;
  cron_expression?: string;
  description?: string;
  old_job_name?: string;
  old_job_group?: string;
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
    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      job_name: "",
      job_group: "",
      job_class_name: "",
      cron_expression: "",
      description: "",
      old_job_name: "",
      old_job_group: "",
    });

    const title = ref("新增数据");

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model != null && model != undefined) {
        formRef.id = model?.id;
        formRef.job_name = model?.job_name;
        formRef.job_group = model?.job_group;
        formRef.job_class_name = model?.job_class_name;
        formRef.cron_expression = model?.cron_expression;
        formRef.description = model?.description;
        formRef.old_job_name = model?.job_name;
        formRef.old_job_group = model?.job_group;
        title.value = "编辑数据";
      }
    });

    const rulesRef = reactive({
      id: [{ required: false }],
      job_name: [
        { required: true, max: 50, message: "请输入任务名称且不多余50个字符" },
      ],
      job_group: [
        { required: true, max: 50, message: "请输入任务分组且不多余50个字符" },
      ],
      job_class_name: [
        { required: true, max: 50, message: "请输入执行类且不多余50个字符" },
      ],
      cron_expression: [
        { required: true, max: 50, message: "请输入执行时间且不多余50个字符" },
      ],
      description: [
        {
          min: 0,
          max: 500,
          message: "长度不得大于500个字符",
        },
      ],
      old_job_name: [{ required: false }],
      old_job_group: [{ required: false }],
    });
    const { validate, validateInfos } = useForm(formRef, rulesRef);

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
    };
  },
});
</script>

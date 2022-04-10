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
        <a-form-item v-bind="validateInfos.notice_user" label="接收人">
          <a-input v-model:value="formRef.notice_user" placeholder="接收人" />
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.cron_expression"
          label="执行时间表达式"
        >
          <a-input
            v-model:value="formRef.cron_expression"
            placeholder="执行时间表达式"
          />
        </a-form-item>
        <a-form-item v-bind="validateInfos.description" label="任务描述">
          <a-textarea
            v-model:value="formRef.description"
            placeholder="任务描述"
            :rows="4"
          />
        </a-form-item>
        <a-form-item v-bind="validateInfos.order_by" label="排序">
          <a-input v-model:value="formRef.order_by" placeholder="排序" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.remark" label="备注">
          <a-textarea
            v-model:value="formRef.remark"
            placeholder="备注"
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
  notice_user?: string;
  cron_expression?: string;
  description?: string;
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
    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      job_name: "",
      notice_user: "",
      cron_expression: "",
      description: "",
      order_by: "",
      remark: "",
      oldJobName: "",
      oldJobGroup: "",
    });

    const title = ref("新增数据");

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model != null && model != undefined) {
        formRef.id = model?.id;
        formRef.job_name = model?.job_name;
        formRef.notice_user = model?.notice_user;
        formRef.cron_expression = model?.cron_expression;
        formRef.description = model?.description;
        formRef.order_by = model?.order_by;
        formRef.remark = model?.remark;
        formRef.oldJobName = model?.job_name;
        formRef.oldJobGroup = model?.job_group;
        title.value = "编辑数据";
      }
    });

    const rulesRef = reactive({
      id: [{ required: false }],
      job_name: [
        { required: true, max: 50, message: "请输入任务名称且不多余50个字符" },
      ],
      notice_user: [
        { required: true, max: 50, message: "请输入接收人且不多余50个字符" },
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
      order_by: [{ max: 50, message: "内容不得多余50个字符" }],
      remark: [
        {
          min: 0,
          max: 500,
          message: "长度不得大于500个字符",
        },
      ],
      oldJobName: [{ required: false }],
      oldJobGroup: [{ required: false }],
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

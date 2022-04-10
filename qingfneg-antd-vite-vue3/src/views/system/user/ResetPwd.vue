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
        <a-form-item v-bind="validateInfos.login_password" label="登录密码">
          <a-input-password
            v-model:value="formRef.login_password"
            placeholder="登录密码"
          />
        </a-form-item>
        <a-form-item v-bind="validateInfos.confirm_password" label="确认密码">
          <a-input-password
            v-model:value="formRef.confirm_password"
            placeholder="确认密码"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form } from "ant-design-vue";

interface Fields {
  ids?: string;
  names?: string;
  login_password?: string;
  confirm_password?: string;
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
      ids: "",
      names: "",
      login_password: "",
      confirm_password: "",
    });

    const title = ref("密码重置");

    // 自动请求并暴露内部方法
    onMounted(() => {
      formRef.ids = model?.ids;
      formRef.names = model?.names;
    });

    let confirm_password = async (_rule: RuleObject, value: string) => {
      if (value === "") {
        return Promise.reject("必填项不能为空");
      } else if (value !== formRef.login_password) {
        return Promise.reject("两次密码输入不一致!");
      } else {
        return Promise.resolve();
      }
    };

    const rulesRef = reactive({
      ids: [{ required: false }],
      names: [{ required: false }],
      login_password: [
        { required: true, message: "必填项不能为空" },
        { min: 0, max: 50, message: "长度不得大于50个字符" },
      ],
      confirm_password: [
        { required: true, validator: confirm_password, trigger: "change" },
        { min: 0, max: 50, message: "长度不得大于50个字符" },
      ],
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

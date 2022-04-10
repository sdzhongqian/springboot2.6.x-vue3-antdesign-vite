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

        <a-form-item v-bind="validateInfos.title" label="标题">
          <a-input v-model:value="formRef.title" placeholder="标题" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.appId" label="应用id">
          <a-row>
            <a-col :span="20"
              ><a-input
                disabled
                v-model:value="formRef.appId"
                placeholder="应用id"
            /></a-col>
            <a-col :span="4"
              ><a-button
                style="margin-left: 4px"
                @click="getAuthen"
                type="primary"
                ><reload-outlined /></a-button
            ></a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-bind="validateInfos.appSecret" label="应用秘钥">
          <a-input
            disabled
            v-model:value="formRef.appSecret"
            placeholder="应用秘钥"
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
import { findDictionaryList } from "@/api/system/dictionary";
import { getAuthenData } from "@/api/common/authen";

interface Fields {
  id?: string;
  title: string;
  appId: string;
  appSecret: string;
  order_by: string;
  remark: string;
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
  components: {},
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

    const baseURL = <string>import.meta.env.VITE_APP_BASE_API;

    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      title: "",
      appId: "",
      appSecret: "",
      order_by: "",
      remark: "",
    });

    const title = ref("新增数据");

    // 自动请求并暴露内部方法
    onMounted(() => {
      formRef.parent_id = model?.parent_id;
      formRef.parent_name = model?.parent_name;
      if (model?.id != null && model?.id != undefined) {
        formRef.id = model?.id;
        formRef.title = model?.title;
        formRef.appId = model?.appId;
        formRef.appSecret = model?.appSecret;
        formRef.order_by = model?.order_by;
        formRef.remark = model?.remark;
        title.value = "编辑数据";
      } else {
        getAuthen();
      }
      initData();
    });

    const rulesRef = reactive({
      id: [{ required: false }],
      title: [
        { required: true, max: 50, message: "请输入标题且不多余50个字符" },
      ],
      appId: [
        { required: true, max: 50, message: "请输入应用id且不多余50个字符" },
      ],
      appSecret: [
        { required: true, max: 50, message: "请输入应用秘钥且不多余50个字符" },
      ],
      order_by: [{ required: false }],
      remark: [{ required: false }],
    });

    const getAuthen = () => {
      getAuthenData().then((response) => {
        console.log(response);
        formRef.appId = response.data.data.appId;
        formRef.appSecret = response.data.data.appSecret;
      });
    };

    const initData = () => {};
    const { validate, validateInfos } = useForm(formRef, rulesRef);

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
      baseURL,
      getAuthen,
    };
  },
});
</script>

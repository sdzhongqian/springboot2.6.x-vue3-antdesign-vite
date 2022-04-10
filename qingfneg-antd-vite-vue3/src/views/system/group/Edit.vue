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
        <a-form-item v-bind="validateInfos.name" label="组名称">
          <a-input v-model:value="formRef.name" placeholder="组名称" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.short_name" label="组简称">
          <a-input v-model:value="formRef.short_name" placeholder="组简称" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.user_names" label="组用户">
          <a-input
            v-show="false"
            v-model:value="formRef.user_ids"
            placeholder="用户ids"
          />
          <a-textarea
            disabled
            v-model:value="formRef.user_names"
            placeholder="请选择用户"
          />
          <a-button size="small" type="primary" @click="selectMoreUser">
            选择
          </a-button>
        </a-form-item>
        <a-form-item label="排序">
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
    <select-more-user
      v-if="visibleMoreUser"
      ref="moreUserModal"
      :visible="visibleMoreUser"
      :loading="confirmLoading"
      :model="mdl"
      @cancel="handleCancel('moreUser')"
      @ok="handleOk('moreUser')"
    />
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form } from "ant-design-vue";
import SelectMoreUser from "@/views/system/user/SelectMoreUser.vue";
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
  components: {
    SelectMoreUser,
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
      name: "",
      short_name: "",
      user_ids: "",
      user_names: "",
      order_by: "",
      remark: "",
    });

    //弹框model
    const moreUserModal = ref(null);
    // 是否展示弹框
    const visibleMoreUser = ref(false);
    const confirmLoading = ref(false);
    const mdl = ref(null);

    const title = ref("新增数据");

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model != null && model != undefined) {
        formRef.id = model?.id;
        formRef.name = model?.name;
        formRef.short_name = model?.short_name;
        formRef.user_ids = model?.user_ids;
        formRef.user_names = model?.user_names;
        formRef.order_by = model?.order_by;
        formRef.remark = model?.remark;
        title.value = "编辑数据";
      }
    });

    const rulesRef = reactive({
      id: [{ required: false }],
      name: [
        { required: true, max: 50, message: "请输入角色名称且不多余50个字符" },
      ],
      short_name: [{ max: 50, message: "角色简称不能超过50个字符" }],
      user_ids: [{ required: false }],
      user_names: [{ required: false }],
      order_by: [{ max: 50, message: "不能超过50个字符" }],
      remark: [
        {
          min: 0,
          max: 500,
          message: "长度不得大于500个字符",
        },
      ],
    });
    const { validate, validateInfos } = useForm(formRef, rulesRef);

    const selectMoreUser = () => {
      mdl.value = {
        user_ids: formRef.user_ids,
        user_names: formRef.user_names,
      };
      visibleMoreUser.value = true;
    };

    //执行保存
    const handleOk = (type) => {
      confirmLoading.value = true;
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
          formRef.user_ids = ids.join(",");
          formRef.user_names = names.join(",");
        });
      } else {
        formRef.user_ids = "";
        formRef.user_names = "";
      }
      visibleMoreUser.value = false;
      confirmLoading.value = false;
    };

    //取消弹框
    const handleCancel = (type) => {
      visibleMoreUser.value = false;
    };

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
      confirmLoading,
      mdl,
      visibleMoreUser,
      selectMoreUser,
      handleOk,
      handleCancel,
      moreUserModal,
    };
  },
});
</script>

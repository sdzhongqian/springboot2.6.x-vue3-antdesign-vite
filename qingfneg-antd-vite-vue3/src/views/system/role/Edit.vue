<template>
  <a-modal
    :title=title
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  > 
    <a-spin :spinning="loading">
      <a-form v-bind="formLayout">
        <!-- 检查是否有 id 并且大于0，大于0是修改。其他是新增，新增不显示主键ID -->
        <a-form-item v-show="false" label="主键ID">
          <a-input v-model:value="formRef.id" disabled />
        </a-form-item>
        <a-form-item v-bind="validateInfos.name" label="角色名称">
          <a-input v-model:value="formRef.name" placeholder="角色名称" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.short_name" label="角色简称">
          <a-input v-model:value="formRef.short_name" placeholder="角色简称" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input v-model:value="formRef.order_by" placeholder="排序" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.remark" label="备注">
          <a-textarea v-model:value="formRef.remark" placeholder="备注" :rows="4" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, reactive, onMounted,ref} from 'vue'
import { Form} from 'ant-design-vue'
// import pick from 'lodash.pick'

interface Fields{
  id?: number,
  name?: string,
  short_name?: string,
  order_by?: string,
  remark?: string
}

const useForm = Form.useForm
export default defineComponent({
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    },
    model: {
      type: Object,
      default: () => null
    }
  },
  setup (props) {
    const {model} = props
    const formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
      }
    }
    // 表单字段
    const formRef = reactive(<Fields> {
      id: "",
      name: "",
      short_name: "",
      order_by: "",
      remark: "",
    })

    const title = ref("新增数据")

    // 自动请求并暴露内部方法
    onMounted(() => {
      if(model!=null&&model!=undefined){
        formRef.id = model?.id;
        formRef.name = model?.name;
        formRef.short_name = model?.short_name;
        formRef.order_by = model?.order_by;
        formRef.remark = model?.remark;
        title.value = "编辑数据"
      }
    })

    const rulesRef = reactive({
      id: [{required: false}],
      name: [{required: true, max: 50, message: '请输入角色名称且不多余50个字符'}],
      short_name: [{ max: 50, message: '角色简称不能超过50个字符'}],
      order_by: [{ max: 50, message: '不能超过50个字符'}],
      remark: [
          {
            min: 0,
            max: 500,
            message: "长度不得大于500个字符"
          },
        ]
    })
    const { validate, validateInfos } = useForm(formRef, rulesRef)

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title
    }
  }
})
</script>

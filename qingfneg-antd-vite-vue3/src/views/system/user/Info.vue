<template>
  <a-modal
    :title=title
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @cancel="() => { $emit('cancel') }"
  >
   <template #footer>
        <a-button key="submit" type="primary" :loading="loading" @click="() => { $emit('cancel') }">确定</a-button>
    </template>
     <a-descriptions title="">
        <a-descriptions-item label="登录账号">{{model?.login_name}}</a-descriptions-item>
        <a-descriptions-item label="姓名">{{model?.name}}</a-descriptions-item>
        <a-descriptions-item label="性别">{{model?.status=='1'?'男':'女'}}</a-descriptions-item>
        <a-descriptions-item label="手机号">{{model?.phone}}</a-descriptions-item>
        <a-descriptions-item label="邮箱">{{model?.email}}</a-descriptions-item>
        <a-descriptions-item label="出生日期">{{model?.birth_date}}</a-descriptions-item>
        <a-descriptions-item label="座右铭">{{model?.motto}}</a-descriptions-item>
        <a-descriptions-item label="排序">{{model?.order_by}}</a-descriptions-item>
        <a-descriptions-item label="居住地址">{{model?.live_address}}</a-descriptions-item>
        <a-descriptions-item label="出生地址">{{model?.birth_address}}</a-descriptions-item>
        <!-- <a-descriptions-item label="组织名称">{{model?.organize_name}}</a-descriptions-item>
        <a-descriptions-item label="职位">{{model?.position}}</a-descriptions-item> -->
        <a-descriptions-item label="备注">{{model?.remark}}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{model?.create_time}}</a-descriptions-item>
      </a-descriptions>
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

    const title = ref("用户信息")

    // 自动请求并暴露内部方法
    onMounted(() => {
    })

    return {
      formLayout,
      title
    }
  }
})
</script>

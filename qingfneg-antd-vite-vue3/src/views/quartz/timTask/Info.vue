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
        <a-descriptions-item label="任务名称">{{model?.job_name}}</a-descriptions-item>
        <a-descriptions-item label="任务分组">{{model?.job_group}}</a-descriptions-item>
        <a-descriptions-item label="执行类">{{model?.job_class_name}}</a-descriptions-item>
        <a-descriptions-item label="执行时间">{{model?.cron_expression}}</a-descriptions-item>
        <a-descriptions-item label="任务描述">{{model?.description}}</a-descriptions-item>
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

    const title = ref("详情数据")

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

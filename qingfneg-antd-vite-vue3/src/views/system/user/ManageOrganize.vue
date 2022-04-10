<template>
  <div>
    <a-modal :title="title" :width="800" :visible="visible">
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
      <div class="operate">
        <a-button type="dashed" style="width: 100%" @click="handleAdd"
          ><plus-outlined />添加</a-button
        >
      </div>
      <a-table
        ref="table"
        :row-key="(record) => record.id"
        :columns="columns"
        :data-source="data"
        :loading="loading"
        :pagination="pagination"
        bordered
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, text, record }">
          <span v-if="column.dataIndex === 'operation'">
            <a-button
              size="small"
              type="primary"
              @click="() => handleEdit(record)"
              >编辑</a-button
            >
            <a-button
              size="small"
              type="danger"
              v-show="record.type == '1'"
              @click="() => handleDel(record.id)"
              >删除</a-button
            >
          </span>
        </template>
      </a-table>
    </a-modal>
    <a-modal
      :title="设置组织"
      :width="640"
      :visible="visibleEdit"
      :confirmLoading="loading"
      @cancel="handCancel"
      @ok="handleOk"
    >
      <a-form layout="vertical" v-bind="formLayout">
        <a-form-item v-bind="validateInfos.name" label="组织类型">
          <a-input
            v-model:value="formRef.type_name"
            disabled
            placeholder="组织类型"
          />
        </a-form-item>
        <a-form-item v-bind="validateInfos.organize_name" label="组织名称">
          <a-row type="flex">
            <a-col :flex="4"
              ><a-input
                v-model:value="formRef.organize_id"
                v-show="false"
                placeholder="组织id" />
              <a-input
                v-model:value="formRef.organize_name"
                placeholder="组织名称"
            /></a-col>
            <a-col :flex="1"
              ><a-button @click="selectOneOrganize" type="primary">
                选择
              </a-button></a-col
            >
          </a-row>
        </a-form-item>
        <a-form-item v-bind="validateInfos.position" label="担任职务">
          <a-input v-model:value="formRef.position" placeholder="担任职务" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.order_by" label="排序">
          <a-input v-model:value="formRef.order_by" placeholder="排序" />
        </a-form-item>
      </a-form>
    </a-modal>
    <select-one-organize
      v-if="visibleOneOrganize"
      ref="oneOrganizeModal"
      :visible="visibleOneOrganize"
      :loading="confirmLoading"
      :model="mdl"
      @cancel="handleOrganizeCancel()"
      @ok="handleOrganizeOk()"
    />
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  computed,
  ref,
  unref,
  onMounted,
  reactive,
} from "vue";
import { Form, message, Modal } from "ant-design-vue";
import {
  getMyOrganizeListPage,
  saveOrUpdateUserOrganize,
  delUserOrganize,
} from "@/api/system/user";
import SelectOneOrganize from "@/views/system/organize/SelectOneOrganize.vue";

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
    SelectOneOrganize,
  },
  setup(props) {
    const table = ref(null);
    const { model } = props;
    const title = ref("组织管理");
    const data = ref([]);
    const visibleEdit = ref(false);

    const columns = [
      {
        title: "组织类型",
        dataIndex: "type",
        customRender: (record, row, index) => {
          if (record.value == "0") {
            return "主组织";
          } else if (record.value == "1") {
            return "兼职组织";
          } else {
            return "未知";
          }
        },
      },
      {
        title: "组织名称",
        dataIndex: "organize_name",
        ellipsis: true,
      },
      {
        title: "担任职务",
        dataIndex: "position",
        customRender: (record, row, index) => {
          if (record.text == "" || record.text == null) {
            return "未指定";
          } else {
            return record.text;
          }
        },
      },
      {
        title: "排序",
        dataIndex: "order_by",
      },
      {
        title: "操作",
        dataIndex: "operation",
        scopedSlots: { customRender: "operation" },
        width: "160px",
      },
    ];

    //定义分页拓展
    const pagination = ref({
      total: 10,
      current: 1,
      pageSize: 10,
      pageSizeOptions: ["5", "10", "20", "30", "40", "100"],
      showTotal: (total) => `共` + total + `条数据`,
    });

    const handleTableChange = (
      pag: { pageSize: number; current: number },
      filters: any,
      sorter: any
    ) => {
      pagination.value.current = pag.current;
      pagination.value.pageSize = pag.pageSize;
      loadData({});
    };

    // 是否展示弹框
    const visible = ref(false);

    const confirmLoading = ref(false);
    const mdl = ref(null);
    // 加载数据方法 必须为 Promise 对象
    const loadData = (parameter) => {
      const params = Object.assign({}, parameter);
      params.pageSize = pagination.value.pageSize;
      params.pageNum = pagination.value.current;
      params.user_id = model?.id;
      return getMyOrganizeListPage(params).then((response) => {
        data.value = response.data.data.rows;
        pagination.value.total = response.data.data.total;
        // let data = {
        //   data: response.data.data.rows,
        //   totalCount: response.data.data.total,
        // };
        // return data;
      });
    };

    // 自动请求并暴露内部方法
    onMounted(() => {
      loadData({});
    });

    const handleDel = (id) => {
      Modal.confirm({
        title: "确定要删除选择的数据吗?",
        content: "数据删除后不可恢复",
        onOk() {
          delUserOrganize(id).then((response) => {
            message.info("删除成功。");
            loadData();
          });
        },
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        onCancel() {},
      });
    };
    //弹框model
    const oneOrganizeModal = ref(null);
    // 是否展示弹框
    const visibleOneOrganize = ref(false);

    //打开选择单选组织
    const selectOneOrganize = () => {
      mdl.value = {
        organize_id: formRef.organize_id,
        organize_name: formRef.organize_name,
      };
      visibleOneOrganize.value = true;
    };

    //打开新增页面
    const handleAdd = () => {
      (formRef.organize_id = ""),
        (formRef.organize_name = ""),
        (formRef.order_by = ""),
        (formRef.position = ""),
        (visibleEdit.value = true);
    };

    //打开编辑页面
    const handleEdit = (record) => {
      (formRef.id = record.id),
        (formRef.organize_id = record.organize_id),
        (formRef.organize_name = record.organize_name),
        (formRef.order_by = record.order_by),
        (formRef.position = record.position),
        (visibleEdit.value = true);
    };

    //取消新增编辑
    const handCancel = () => {
      visibleEdit.value = false;
    };

    //保存/修改数据
    const handleOk = () => {
      validate().then((result) => {
        new Promise((resolve, reject) => {
          saveOrUpdateUserOrganize(result).then((response) => {
            resolve(result);
          });
        }).then((res) => {
          visibleEdit.value = false;
          loadData();
          if (result.id > 0) {
            message.info("修改成功");
          } else {
            message.info("新增成功");
          }
        });
      });
    };

    //执行保存
    const handleOrganizeOk = () => {
      confirmLoading.value = true;
      const sdata = oneOrganizeModal.value.sdata;
      if (sdata != "" && sdata != undefined) {
        new Promise((resolve, reject) => {
          formRef.organize_id = sdata.split(":")[0];
          formRef.organize_name = sdata.split(":")[1];
        });
      } else {
        formRef.organize_id = "";
        formRef.organize_name = "";
      }
      visibleOneOrganize.value = false;
      confirmLoading.value = false;
    };

    //取消弹框
    const handleOrganizeCancel = () => {
      visibleOneOrganize.value = false;
    };

    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      type: "1",
      type_name: "兼职组织",
      organize_id: "",
      organize_name: "",
      order_by: "",
      position: "",
      user_id: model?.id,
    });

    //校验规则
    const rulesRef = reactive({
      id: [{ required: false }],
      type: [{ required: false }],
      organize_id: [{ required: false }],
      organize_name: [{ required: true, message: "请选择组织" }],
      position: [{ max: 50, message: "不能超过50个字符" }],
      order_by: [{ max: 50, message: "不能超过50个字符" }],
      user_id: [{ required: false }],
    });
    const { validate, validateInfos } = useForm(formRef, rulesRef);

    return {
      title,
      columns,
      pagination,
      table,
      data,
      visibleEdit,
      handleAdd,
      handleEdit,
      handCancel,
      handleOk,
      handleDel,
      formRef,
      validateInfos,
      oneOrganizeModal,
      visibleOneOrganize,
      confirmLoading,
      mdl,
      selectOneOrganize,
      handleOrganizeOk,
      handleOrganizeCancel,
      handleTableChange,
    };
  },
});
</script>


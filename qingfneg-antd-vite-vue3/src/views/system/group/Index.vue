<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="6" :sm="24">
              <a-form-item label="组名称">
                <a-input
                  v-model:value="queryParam.name"
                  placeholder="组名称"
                  style="width: 160px"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="组简称">
                <a-input
                  v-model:value="queryParam.short_name"
                  placeholder="组简称"
                  style="width: 160px"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="使用状态">
                <a-select
                  v-model:value="queryParam.status"
                  placeholder="请选择"
                  default-value="0"
                  style="width: 160px"
                >
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="0">启用</a-select-option>
                  <a-select-option value="1">禁用</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-button type="primary" @click="table.refresh(true)"
                >查询</a-button
              >
              <a-button style="margin-left: 8px" @click="resetQueryParam"
                >重置</a-button
              >
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div class="table-operator">
        <a-button type="primary" @click="handleAdd"
          ><plus-outlined />新建</a-button
        >
        <a-dropdown v-if="rowSelection.selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu @click="handleMenuClick">
              <a-menu-item key="1"><delete-outlined />删除</a-menu-item>
            </a-menu>
          </template>
          <a-button style="margin-left: 8px">
            批量操作 <down-outlined />
          </a-button>
        </a-dropdown>
      </div>

      <s-table
        ref="table"
        size="default"
        rowKey="key"
        :row-key="(record) => record.id"
        :columns="columns"
        :data="loadData"
        :loading="loading"
        :pagination="pagination"
        :rowSelection="rowSelection"
        showPagination="auto"
        bordered
      >
        <template v-slot:suffix="{ column, record, index, text }">
          <span v-if="column.dataIndex === 'operation'">
            <a-button
              size="small"
              v-action:role:info
              @click="() => handleInfo(record)"
              >详情</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action-dauth:[{...record,auth:`role:edit`}]
              @click="() => handleEdit(record)"
              >编辑</a-button
            >
            <a-button
              size="small"
              type="danger"
              v-action-dauth:[{...record,auth:`role:del`}]
              @click="() => handleDel(record.id)"
              >删除</a-button
            >
            <a-button
              size="small"
              type="danger"
              v-action:role:status
              v-show="record.status == '0'"
              @click="() => editStatus(record.id, '1')"
              >禁用</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action:role:status
              v-show="record.status == '1'"
              @click="() => editStatus(record.id, '0')"
              >启用</a-button
            >
          </span>
        </template>
      </s-table>

      <edit
        v-if="visible"
        ref="createModal"
        :visible="visible"
        :loading="confirmLoading"
        :model="mdl"
        @cancel="handleCancel"
        @ok="handleOk"
      />
      <info
        ref="infoModal"
        :visible="visibleInfo"
        :loading="confirmLoading"
        :model="mdl"
        @cancel="handleCancel"
      />
    </a-card>
  </page-header-wrapper>
</template>

<script lang="ts">
import {
  defineComponent,
  computed,
  ref,
  unref,
  reactive,
  toRaw,
  createVNode,
} from "vue";
import { message, Form, Modal } from "ant-design-vue";
import { STable } from "@/components/index";
import {
  getListPage,
  del,
  updateStatus,
  saveOrUpdate,
} from "@/api/system/group";
import { findDataAuth } from "@/core/baseAction";
import Edit from "./Edit.vue";
import Info from "./Info.vue";

interface QueryParam {
  name?: string;
  short_name?: string;
  status?: string;
}

const useForm = Form.useForm;
export default defineComponent({
  name: "TableList",
  components: {
    STable,
    Edit,
    Info,
  },
  setup() {
    const table = ref(null);
    const createModal = ref(null);
    const modal = ref(null);
    const columns = [
      {
        title: "组名称",
        dataIndex: "name",
        sorter: true,
        ellipsis: true,
      },
      {
        title: "组简称",
        dataIndex: "short_name",
        ellipsis: true,
      },
      {
        title: "状态",
        dataIndex: "status",
        customRender: (text, row, index) => {
          if (text == "0") {
            return "启用";
          } else {
            return "禁用";
          }
        },
      },
      {
        title: "排序",
        dataIndex: "order_by",
      },
      {
        title: "创建时间",
        dataIndex: "create_time",
      },
      {
        title: "操作",
        dataIndex: "operation",
        scopedSlots: { customRender: "operation" },
        width: "280px",
      },
    ];

    //定义分页拓展
    const pagination = ref({
      current: 1,
      pageSize: 10,
      pageSizeOptions: ["5", "10", "20", "30", "40", "100"],
      showTotal: (total) => `共` + total + `条数据`,
    });

    const formRef = reactive(<QueryParam>{
      name: "",
      short_name: "",
      status: "",
    });
    const rulesRef = reactive({});
    const { resetFields, validate, validateInfos } = useForm(formRef, rulesRef);

    // 是否展示弹框
    const visible = ref(false);
    const visibleInfo = ref(false);
    const confirmLoading = ref(false);
    const mdl = ref(null);
    // 查询参数
    const queryParam = reactive(<QueryParam>{});
    // 加载数据方法 必须为 Promise 对象
    const loadData = (parameter) => {
      const params = Object.assign({}, parameter, queryParam);
      params.pageSize = pagination.value.pageSize;
      params.pageNum = params.pageNo;
      return getListPage(params)
        .then((response) => {
          let data = {
            data: response.data.data.rows,
            totalCount: response.data.data.total,
          };
          return data;
        })
        .catch((err) => {
          console.log("error", err);
        });
    };

    //选择table
    const selectedRowKeys = ref([]);
    const selectedRows = ref([]);
    const onSelectChange = (rowKeys, rows) => {
      selectedRowKeys.value = unref(rowKeys);
      selectedRows.value = toRaw(rows);
    };
    const rowSelection = computed(() => {
      return {
        selectedRowKeys: unref(selectedRowKeys),
        selectedRows: unref(selectedRows),
        onChange: onSelectChange,
      };
    });

    //重置查询参数
    const resetQueryParam = () => {
      queryParam.name = "";
      queryParam.short_name = "";
      queryParam.status = "";
      table.value.refresh(true);
    };

    //展示新增页面
    const handleAdd = () => {
      mdl.value = null;
      visible.value = true;
    };

    //展示编辑页面
    const handleEdit = (record) => {
      mdl.value = { ...record };
      visible.value = true;
    };

    //执行保存
    const handleOk = () => {
      confirmLoading.value = true;
      // @ts-ignore
      createModal.value
        .validate()
        .then((result) => {
          // @ts-ignore
          new Promise((resolve, reject) => {
            saveOrUpdate(result).then((response) => {
              resolve(result);
            });
          }).then((res) => {
            visible.value = false;
            confirmLoading.value = false;
            // 重置表单数据
            resetFields();
            // 刷新表格
            // @ts-ignore
            table.value.refresh(true);
            if (result.id > 0) {
              message.info("修改成功");
            } else {
              message.info("新增成功");
            }
          });
        })
        .catch(() => {
          confirmLoading.value = false;
          // console.log('error', err)
        });
    };

    //取消弹框
    const handleCancel = () => {
      visible.value = false;
      visibleInfo.value = false;
      // 清理表单数据（可不做）
      resetQueryParam();
    };

    //执行删除
    const handleDel = (ids) => {
      Modal.confirm({
        title: "确定要删除选择的数据吗?",
        content: "数据删除后不可恢复",
        onOk() {
          del(ids).then((response) => {
            message.info("删除成功。");
            table.value.refresh(true);
          });
        },
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        onCancel() {},
      });
    };

    //修改状态
    const editStatus = (id, status) => {
      const that = this;
      updateStatus(id, status).then((response) => {
        if (response.status == 200) {
          table.value.refresh(true);
        } else {
          message.error("状态修改异常");
        }
      });
    };

    //详情
    const handleInfo = (record) => {
      mdl.value = { ...record };
      visibleInfo.value = true;
    };

    //批量删除
    const handleMenuClick: MenuProps["onClick"] = (e) => {
      if (e.key == "1") {
        let delBol = true;
        for (let i = 0; i < selectedRows.value.length; i++) {
          const record = selectedRows.value[i];
          if (!findDataAuth(record)) {
            delBol = false;
          }
        }
        if (!delBol) {
          message.error("批量删除中存在没有删除权限的数据，请重新选择。");
          return;
        }
        handleDel(selectedRowKeys.value.join(","));
      }
    };

    return {
      pagination,
      table,
      createModal,
      modal,
      columns,
      formRef,
      resetFields,
      validate,
      validateInfos,
      visible,
      confirmLoading,
      mdl,
      queryParam,
      loadData,
      rowSelection,
      onSelectChange,
      resetQueryParam,
      handleAdd,
      handleEdit,
      handleOk,
      handleCancel,
      handleDel,
      editStatus,
      handleInfo,
      visibleInfo,
      handleMenuClick,
    };
  },
});
</script>

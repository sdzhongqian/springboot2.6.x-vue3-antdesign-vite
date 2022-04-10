<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="12" :sm="24">
              <a-form-item label="任务名称">
                <a-input
                  v-model:value="queryParam.job_name"
                  placeholder="任务名称"
                  style="width: 160px"
                />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
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
        <a-button type="primary" v-action:busTask:add @click="handleAdd"
          ><plus-outlined />新建</a-button
        >
        <a-dropdown v-if="rowSelection.selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu @click="handleDelClick">
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
              v-action:busTask:info
              @click="() => handleInfo(record)"
              >详情</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action:busTask:edit
              @click="() => handleEdit(record)"
              >编辑</a-button
            >
            <a-button
              size="small"
              type="danger"
              v-action:busTask:del
              @click="() => handleDel(record.id)"
              >删除</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action:busTask:execution
              @click="() => handleExecution(record)"
              >执行</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action:busTask:stopOrRestore
              v-show="record.trigger_state == 'N'"
              @click="() => handleStopOrRestore(record, 'Y')"
              >恢复</a-button
            >
            <a-button
              size="small"
              type="danger"
              v-action:busTask:stopOrRestore
              v-show="record.trigger_state == 'Y'"
              @click="() => handleStopOrRestore(record, 'N')"
              >停止</a-button
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
import { findDataAuth } from "@/core/baseAction";
import Edit from "./Edit.vue";
import Info from "./Info.vue";
import { mapState } from "vuex";
import {
  getListPage,
  saveOrUpdate,
  del,
  stopOrRestore,
  execution,
} from "@/api/quartz/busTask";
import { formatDate } from "@/utils/util";

interface QueryParam {
  name?: string;
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
        title: "任务名称",
        dataIndex: "job_name",
        sorter: true,
        ellipsis: true,
      },
      {
        title: "任务描述",
        dataIndex: "description",
        ellipsis: true,
      },
      {
        title: "执行时间表达式",
        dataIndex: "cron_expression",
        ellipsis: true,
      },
      {
        title: "状态",
        dataIndex: "trigger_state",
        customRender: (text, row, index) => {
          if (text == "Y") {
            return "执行中";
          } else {
            return "已停止";
          }
        },
      },
      {
        title: "排序",
        dataIndex: "order_by",
        ellipsis: true,
      },
      {
        title: "执行开始时间",
        dataIndex: "trigger_time",
        ellipsis: true,
      },
      {
        title: "操作",
        dataIndex: "operation",
        scopedSlots: { customRender: "operation" },
        width: "300px",
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
      job_name: "",
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
      queryParam.job_name = "";
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
              resolve(response);
            });
          }).then((res) => {
            if (res.data.success) {
              visible.value = false;
              confirmLoading.value = false;
              // 重置表单数据
              resetFields();
              // 刷新表格
              // @ts-ignore
              table.value.refresh(true);
              message.info("操作成功。");
            } else {
              message.error(res.data.msg);
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

    //详情
    const handleInfo = (record) => {
      mdl.value = { ...record };
      visibleInfo.value = true;
    };

    //批量删除
    const handleDelClick: MenuProps["onClick"] = (e) => {
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

    const handleExecution = (record) => {
      execution(record.job_name, record.job_group).then((response) => {
        console.log(response);
        if (response.status == 200) {
          message.success("执行成功");
        } else {
          message.error("执行失败");
        }
      });
    };

    const handleStopOrRestore = (record, status) => {
      stopOrRestore({
        id: record.id,
        job_name: record.job_name,
        job_group: record.job_group,
        trigger_state: status,
      }).then((response) => {
        if (response.status == 200) {
          table.value.refresh(true);
          message.success("操作成功");
        } else {
          message.error("执行失败");
        }
      });
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
      handleInfo,
      visibleInfo,
      handleDelClick,
      handleExecution,
      handleStopOrRestore,
    };
  },
});
</script>

<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="6" :sm="24">
              <a-form-item label="表名称">
                <a-input
                  v-model:value="queryParam.name"
                  placeholder="表名称"
                  style="width: 160px"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="表描述">
                <a-input
                  v-model:value="queryParam.table_comment"
                  placeholder="表描述"
                  style="width: 160px"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="表时间">
                <a-range-picker
                  :show-time="{ format: 'HH:mm' }"
                  format="YYYY-MM-DD HH:mm"
                  :placeholder="['开始时间', '结束时间']"
                  @change="selectDate"
                />
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
        <a-button
          type="primary"
          v-action:gencode:import
          @click="importTable({})"
          ><a-icon type="upload" />导入</a-button
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
              type="primary"
              v-action:gencode:viewCode
              @click="() => viewCode(record)"
              >预览</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action:gencode:gencode
              @click="() => handleGencode(record)"
              >生成</a-button
            >
            <a-button
              size="small"
              v-action:gencode:info
              @click="() => handleInfo(record)"
              >详情</a-button
            >
            <a-button
              size="small"
              type="primary"
              v-action:gencode:edit
              @click="() => handleEdit(record)"
              >编辑</a-button
            >
            <a-button
              size="small"
              type="danger"
              v-action:gencode:del
              @click="() => handleDel(record.id)"
              >删除</a-button
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
        v-if="visibleInfo"
        ref="infoModal"
        :visible="visibleInfo"
        :loading="confirmLoading"
        :model="mdl"
        @cancel="handleCancel"
      />
      <select-table
        v-if="visibleST"
        ref="stModal"
        :visible="visibleST"
        :loading="confirmLoading"
        :model="mdl"
        @cancel="handleSTCancel"
        @ok="handleSTOk"
      />
      <view-code
        v-if="visibleVC"
        ref="vcModal"
        :visible="visibleVC"
        :loading="confirmLoading"
        :model="mdl"
        @cancel="handleVCCancel"
        @ok="handleVCOk"
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
  delData,
  save,
  gencode,
  update,
} from "@/api/system/gencode";
import { findDataAuth } from "@/core/baseAction";
import Edit from "./Edit.vue";
import Info from "./Info.vue";
import SelectTable from "./SelectTable.vue";
import ViewCode from "./ViewCode.vue";

interface QueryParam {
  name?: string;
  table_comment?: string;
  start_time?: string;
  end_time?: string;
}

const useForm = Form.useForm;
export default defineComponent({
  name: "TableList",
  components: {
    STable,
    Edit,
    Info,
    SelectTable,
    ViewCode,
  },
  setup() {
    const table = ref(null);
    const createModal = ref(null);
    const stModal = ref(null);
    const vcModal = ref(null);
    const modal = ref(null);
    const columns = [
      {
        title: "表名称",
        dataIndex: "table_name",
        sorter: true,
        ellipsis: true,
      },
      {
        title: "表描述",
        dataIndex: "table_comment",
        ellipsis: true,
      },
      {
        title: "模板类型",
        dataIndex: "temp_type",
        customRender: (text, row, index) => {
          if (text == "0") {
            return "单表";
          } else if (text == "1") {
            return "树表";
          }
        },
      },
      {
        title: "生成包路径",
        dataIndex: "pack_path",
        ellipsis: true,
      },
      {
        title: "模块名称",
        dataIndex: "mod_name",
        ellipsis: true,
      },
      {
        title: "业务名称",
        dataIndex: "bus_name",
        ellipsis: true,
      },
      {
        title: "创建时间",
        dataIndex: "create_time",
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
      name: "",
      table_comment: "",
      start_time: "",
      end_time: "",
    });
    const rulesRef = reactive({});
    const { resetFields, validate, validateInfos } = useForm(formRef, rulesRef);

    // 是否展示弹框
    const visible = ref(false);
    const visibleInfo = ref(false);
    const visibleST = ref(false);
    const visibleVC = ref(false);

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
          console.log("*************************");
          console.log(response);
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
      queryParam.table_comment = "";
      queryParam.start_time = "";
      queryParam.end_time = "";
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
      createModal.value.validate().then((result) => {
        // @ts-ignore
        new Promise((resolve, reject) => {
          update(result).then((response) => {
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
          message.info("设置完成");
        });
      });
    };

    //取消弹框
    const handleCancel = () => {
      visible.value = false;
      visibleInfo.value = false;
      // 清理表单数据（可不做）
      resetQueryParam();
    };

    //导入数据表
    const handleSTOk = () => {
      confirmLoading.value = true;
      const rowSelection = stModal.value.rowSelection;
      const selectedRowKeys = stModal.value.selectedRowKeys;
      save({ table_names: selectedRowKeys.join(",") }).then((response) => {
        if (response.status == 200) {
          table.value.refresh(true);
        } else {
          message.error("数据表导入异常");
        }
      });
      visibleST.value = false;
      confirmLoading.value = false;
    };

    //取消数据表导入弹框
    const handleSTCancel = () => {
      visibleST.value = false;
    };

    //展示数据表导入页面
    const importTable = (record) => {
      console.log(record);
      mdl.value = record;
      visibleST.value = true;
    };

    //关闭代码预览弹框
    const handleVCOk = () => {
      confirmLoading.value = true;
      visibleVC.value = false;
    };

    //取消代码预览弹框
    const handleVCCancel = () => {
      visibleVC.value = false;
    };

    //展示代码预览页面
    const viewCode = (record) => {
      gencode(record.id).then((response) => {
        if (response.status == 200) {
          console.log(record);
          mdl.value = record;
          visibleVC.value = true;
        } else {
          message.error("代码生成失败。");
        }
      });
    };

    //执行删除
    const handleDel = (ids) => {
      Modal.confirm({
        title: "确定要删除选择的数据吗?",
        content: "数据删除后不可恢复",
        onOk() {
          delData(ids).then((response) => {
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

    const selectDate = (value, dateString) => {
      queryParam.start_time = dateString[0];
      queryParam.end_time = dateString[1];
    };

    const handleGencode = (record) => {
      gencode(record.id).then((response) => {
        if (response.status == 200) {
          if (record.gen_type == "0") {
            console.log(response);
            const baseURL = <string>import.meta.env.VITE_APP_BASE_API;
            window.location.href =
              baseURL + "/system/gencode/downloadCode?id=" + record.id;
          } else {
            message.success("代码生成完成。");
          }
        } else {
          message.error("代码生成失败。");
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
      stModal,
      handleSTOk,
      handleSTCancel,
      visibleST,
      importTable,
      selectDate,
      viewCode,
      vcModal,
      handleVCOk,
      handleVCCancel,
      visibleVC,
      handleGencode,
    };
  },
});
</script>

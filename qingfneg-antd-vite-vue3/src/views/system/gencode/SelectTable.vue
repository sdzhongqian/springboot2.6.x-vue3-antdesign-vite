<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="loading"
  >
    <page-header-wrapper>
      <a-card :bordered="false">
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="48">
              <a-col :md="8" :sm="24">
                <a-form-item label="表名称">
                  <a-input
                    v-model:value="queryParam.table_name"
                    placeholder="表名称"
                    style="width: 160px"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item label="表描述">
                  <a-input
                    v-model:value="queryParam.table_comment"
                    placeholder="表描述"
                    style="width: 160px"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
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
        <s-table
          ref="table"
          size="default"
          rowKey="key"
          :row-key="(record) => record.table_name"
          :columns="columns"
          :data="loadData"
          :loading="loading"
          :pagination="pagination"
          :rowSelection="rowSelection"
          showPagination="auto"
          bordered
        >
          <template v-slot:suffix="{ column, record, index, text }"> </template>
        </s-table>
      </a-card>
    </page-header-wrapper>
  </a-modal>
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
import { message, Form } from "ant-design-vue";
import { STable } from "@/components/index";
import { getTableListPage } from "@/api/system/gencode";
import { findDataAuth } from "@/core/baseAction";
import { formatDate } from "@/utils/util";

interface QueryParam {
  table_name?: string;
  table_comment?: string;
}

const useForm = Form.useForm;
export default defineComponent({
  name: "TableList",
  components: {
    STable,
  },
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
  setup() {
    const table = ref(null);
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
        title: "创建时间",
        dataIndex: "create_time",
        customRender: (text, row, index) => {
          if (text == "" || text == null) {
            return "";
          } else {
            return formatDate(text);
          }
        },
      },
      {
        title: "更新时间",
        dataIndex: "update_time",
        customRender: (text, row, index) => {
          if (text == "" || text == null) {
            return "";
          } else {
            return formatDate(text);
          }
        },
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
      table_name: "",
      table_comment: "",
    });
    const rulesRef = reactive({});
    const { resetFields, validate, validateInfos } = useForm(formRef, rulesRef);

    // 查询参数
    const queryParam = reactive(<QueryParam>{});
    // 加载数据方法 必须为 Promise 对象
    const loadData = (parameter) => {
      const params = Object.assign({}, parameter, queryParam);
      params.pageSize = pagination.value.pageSize;
      params.pageNum = params.pageNo;
      return getTableListPage(params)
        .then((response) => {
          console.log(response);
          let data = {
            data: response.data.data,
            totalCount: response.data.count,
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
      queryParam.table_name = "";
      queryParam.table_comment = "";
      table.value.refresh(true);
    };

    return {
      pagination,
      table,
      columns,
      formRef,
      resetFields,
      validate,
      validateInfos,
      queryParam,
      loadData,
      rowSelection,
      onSelectChange,
      resetQueryParam,
      selectedRowKeys,
    };
  },
});
</script>

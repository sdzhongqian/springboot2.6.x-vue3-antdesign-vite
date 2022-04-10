<template>
  <page-header-wrapper>
    <a-row :gutter="24">
      <a-col :md="24" :lg="6">
        <a-card :bordered="false">
          <tree ref="child" @selectTree="selectTree"></tree>
        </a-card>
      </a-col>
      <a-col :md="24" :lg="18">
        <a-card :bordered="false">
          <div class="table-page-search-wrapper">
            <a-form layout="inline">
              <a-row :gutter="48" :style="{width:'100%'}">

                <a-col :md="6" :sm="24">
                  <a-form-item label="字典名称">
                    <a-input v-model:value="queryParam.name" placeholder="字典名称" />
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
            <a-button type="primary" @click="handleAdd" v-action:tdemo:add
              ><plus-outlined />新建</a-button
            >
            <a-dropdown v-if="rowSelection.selectedRowKeys.length > 0" v-action:tdemo:del>
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

          <a-table
            :columns="columns"
            :row-key="(record) => record.id"
            :data-source="dataSource"
            :pagination="pagination"
            :loading="loading"
            :rowSelection="rowSelection"
            @change="handleTableChange"
            bordered
          >
            <template #bodyCell="{ column, text, record }">
              <span v-if="column.dataIndex === 'operation'">
                <a-button
                  size="small"
                  v-action:tdemo:info
                  @click="() => handleInfo(record)"
                  >详情</a-button
                >
                <a-button
                  size="small"
                  type="primary"
                  v-action-dauth:[{...record,auth:`tdemo:edit`}]
                  @click="() => handleEdit(record)"
                  >编辑</a-button
                >
                <a-button
                  size="small"
                  type="danger"
                  v-action-dauth:[{...record,auth:`tdemo:del`}]
                  @click="() => handleDel(record)"
                  >删除</a-button
                >
                <a-button
                  size="small"
                  type="danger"
                  v-action:tdemo:status
                  v-show="record.status == '0'"
                  @click="() => editStatus(record.id, '1')"
                  >禁用</a-button
                >
                <a-button
                  size="small"
                  type="primary"
                  v-action:tdemo:status
                  v-show="record.status == '1'"
                  @click="() => editStatus(record.id, '0')"
                  >启用</a-button
                >
              </span>
            </template>
          </a-table>

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
        </a-card>
      </a-col>
    </a-row>
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
  onMounted,
} from "vue";
import { message, Form, Modal } from "ant-design-vue";
import { STable } from "@/components/index";
import { getListPage, delData, updateStatus ,saveOrUpdate } from "@/api/common/tdemo";
import { findDataAuth } from "@/core/baseAction";
import { findDictionaryList } from "@/api/system/dictionary";

import Edit from "./Edit.vue";
import Info from "./Info.vue";
import Tree from "./Tree.vue";

interface QueryParam {}

const useForm = Form.useForm;
export default defineComponent({
  name: "TableList",
  components: {
    STable,
    Edit,
    Info,
    Tree,
  },
  setup() {
    const table = ref(null);
    const createModal = ref(null);
    const modal = ref(null);
    const dataSource = ref([]);
    const treeData = ref({});
    const columns = [
      {
        title: "字典名称",
        dataIndex: "name",
        ellipsis: true,
      },

      {
        title: "字典简称",
        dataIndex: "short_name",
        ellipsis: true,
        customRender: (record) => {
          if(record.value=='0'){
            return '北京';
          }
          if(record.value=='1'){
            return '上海';
          }
          if(record.value=='2'){
            return '深圳';
          }
        },
      },

      {
        title: "字典编码",
        dataIndex: "code_name",
        ellipsis: true,
      },

      {
        title: "分类",
        dataIndex: "classify_name",
        ellipsis: true,
      },

      {
        title: "排序",
        dataIndex: "order_by",
        ellipsis: true,
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
      total: 0,
      current: 1,
      pageSize: 10,
      pageSizeOptions: ["5", "10", "20", "30", "40", "100"],
      showTotal: (total) => `共` + total + `条数据`,
    });

    const handleTableChange = (pag: { pageSize: number; current: number }) => {
      pagination.value.pageSize = pag.pageSize;
      pagination.value.current = pag?.current;
      loadData({});
    };

    onMounted(() => {
      loadData({});
    });

    const formRef = reactive(<QueryParam>{});
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
      params.pageNum = pagination.value.current;
      if (treeData.parent_id != "") {
        params.parent_id = treeData.value.parent_id;
      }
      getListPage(params)
        .then((response) => {
          dataSource.value = response.data.data.rows;
          pagination.value.total = response.data.data.total;
        })
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
      loadData({});
    };

    //展示新增页面
    const handleAdd = () => {
      mdl.value = {
        ...treeData.value,
      };
      visible.value = true;
    };

    //展示编辑页面
    const handleEdit = (record) => {
      mdl.value = {
        ...treeData.value,
        ...record,
      };
      visible.value = true;
    };

    //执行保存
    const handleOk = () => {
      confirmLoading.value = true;
      // @ts-ignore
      createModal.value
        .validate()
        .then((result) => {
          if (isArray(result.order_by)) {
            result.order_by = result.order_by.join(",");
          }
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

            getTreeList();
            loadData({});
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

    const isArray = (obj) => {
      return typeof obj == "object" && obj.constructor == Array;
    };

    //取消弹框
    const handleCancel = () => {
      visible.value = false;
      visibleInfo.value = false;
      // 清理表单数据（可不做）
      resetQueryParam();
    };

    //执行删除
    const handleDel = (record) => {
      if (record.child_num > 0) {
        message.error("删除中存在含有下级菜单的数据，请重新选择。");
        return;
      }
      Modal.confirm({
        title: "确定要删除选择的数据吗?",
        content: "数据删除后不可恢复",
        onOk() {
          delData(record.id).then((response) => {
            message.info("删除成功。");
            loadData({});
            getTreeList();
          });
        },
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        onCancel() {},
      });
    };

    //修改状态
    const editStatus = (id, status) => {
      updateStatus(id, status).then((response) => {
        if (response.status == 200) {
          loadData({});
        } else {
          message.error("状态修改异常");
        }
      });
    };

    //详情
    const handleInfo = (record) => {
      mdl.value = {
        ...treeData.value,
        ...record,
      };
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
        if (selectedRows.value.length > 0) {
          let delBol = true;
          for (let i = 0; i < selectedRows.value.length; i++) {
            const record = selectedRows.value[i];
            if (record.child_num > 0) {
              delBol = false;
            }
          }
          if (!delBol) {
            message.error("批量删除中存在含有下级菜单的数据，请重新选择。");
            return;
          }
        }
        Modal.confirm({
          title: "确定要删除选择的数据吗?",
          content: "数据删除后不可恢复",
          onOk() {
            delData(selectedRowKeys.value.join(",")).then((response) => {
              message.info("删除成功。");
              loadData({});
              getTreeList();
            });
          },
          onCancel() {},
        });
      }
    };
    const selectTree = (id, name) => {
      treeData.value = {
        parent_id: id,
        parent_name: name,
      };
      loadData({});
    };

    let child = ref(null);
    const getTreeList = () => {
      child.value.getTreeList({ ...treeData.value });
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
      handleDelClick,
      selectTree,
      child,
      dataSource,
      handleTableChange,
    };
  },
});
</script>

<template>
  <a-modal
    :title="title"
    :width="1200"
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
     <template #footer>
        <a-button key="submit" type="primary" :loading="loading" @click="() => { $emit('cancel') }">确定</a-button>
    </template>
    <a-spin :spinning="loading">
      <a-form v-bind="formLayout">
        <a-card
          class="card"
          title="基础信息设置"
          :style="{ padding: '0 auto' }"
          :bordered="false"
        >
          <a-row>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.table_name" label="表名称">
                <a-input
                  v-model:value="formRef.table_name"
                  placeholder="表名称"
                  disabled
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.name" label="表描述">
                <a-input
                  v-model:value="formRef.table_comment"
                  placeholder="表描述"
                  disabled
                /> </a-form-item
            ></a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.temp_type" label="模板类型">
                <a-select
                  v-model:value="formRef.temp_type"
                  placeholder="模板类型"
                  disabled
                >
                  <a-select-option value="0"> 单表 </a-select-option>
                  <a-select-option value="1"> 树表 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.pack_path" label="生成包路径">
                <a-input
                  v-model:value="formRef.pack_path"
                  placeholder="生成包路径"
                  disabled
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.mod_name" label="生成模块名">
                <a-input
                  v-model:value="formRef.mod_name"
                  placeholder="生成模块名"
                  disabled
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.bus_name" label="生成业务名">
                <a-input
                  v-model:value="formRef.bus_name"
                  placeholder="生成业务名"
                  disabled
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.menu_name" label="功能名称">
                <a-input
                  v-model:value="formRef.menu_name"
                  placeholder="功能名称"
                  disabled
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.menu_id" label="上级菜单目录">
                <a-tree-select
                  v-model:value="formRef.menu_id"
                  show-search
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeData"
                  placeholder="菜单目录"
                  tree-default-expand-all
                  :tree-line="true"
                  :tree-icon="false"
                  disabled
                >
                </a-tree-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item v-bind="validateInfos.gen_type" label="生成方式">
                <a-select
                  v-model:value="formRef.gen_type"
                  placeholder="请选择生成方式"
                  disabled
                >
                  <a-select-option value="0"> zip包下载 </a-select-option>
                  <a-select-option value="1"> 生成到指定路径 </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="生成路径" v-bind="validateInfos.gen_path">
                <a-input
                  v-model:value="formRef.gen_path"
                  placeholder="生成路径"
                  disabled
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-card>
        <a-card
          class="card"
          title="字段信息设置"
          :style="{ padding: '0 auto' }"
          :bordered="false"
        >
          <a-table :data-source="formRef.fieldList" :pagination="false">
            <a-table-column
              key="field_name"
              title="字段名称"
              data-index="field_name"
            />
            <template #bodyCell="{ column, text, record }">
              <template v-if="column.dataIndex === 'field_comment'">
                {{ record.field_comment }}
              </template>
              <template v-if="column.dataIndex === 'field_operat'">
                <span>
                  <a-checkbox :checked="record.field_operat"></a-checkbox>
                </span>
              </template>
              <template v-if="column.dataIndex === 'field_list'">
                <span>
                  <a-checkbox :checked="record.field_list"></a-checkbox>
                </span>
              </template>
              <template v-if="column.dataIndex === 'field_query'">
                <span>
                  <a-checkbox :checked="record.field_query"></a-checkbox>
                </span>
              </template>
              <template v-if="column.dataIndex === 'query_type'">
                {{ record.query_type }}
              </template>
              <template v-if="column.dataIndex === 'verify_rule'">
                {{record.verify_rule}}
              </template>
              <template v-if="column.dataIndex === 'show_type'">
                <span v-if="record.show_type == '1'">文本框</span>
                <span v-if="record.show_type == '2'">文本域</span>
                <span v-if="record.show_type == '3'">下拉框</span>
                <span v-if="record.show_type == '4'">单选框</span>
                <span v-if="record.show_type == '5'">复选框</span>
                <span v-if="record.show_type == '6'">富文本</span>
                <span v-if="record.show_type == '7'">日期控件</span>
                <span v-if="record.show_type == '8'">上传控件</span>
              </template>
              <template v-if="column.dataIndex === 'option_content'">
                <span>
                  {{record.option_content}}
                </span>
              </template>
              <template v-if="column.dataIndex === 'default_value'">
                <span>
                  {{record.default_value}}
                </span>
              </template>
              <template v-if="column.dataIndex === 'order_by'">
                <span>
                  {{record.order_by}}
                </span>
              </template>
            </template>
            <a-table-column
              key="field_comment"
              title="字段描述"
              dataIndex="field_comment"
            >
            </a-table-column>
            <a-table-column
              key="field_operat"
              title="添加编辑"
              data-index="field_operat"
            >
            </a-table-column>
            <a-table-column
              key="field_list"
              title="列表展示"
              data-index="field_list"
            >
            </a-table-column>
            <a-table-column
              key="field_query"
              title="查询展示"
              data-index="field_query"
            >
            </a-table-column>
            <a-table-column
              key="query_type"
              title="查询方式"
              data-index="query_type"
            >
            </a-table-column>
            <a-table-column
              key="verify_rule"
              title="校验规则	"
              data-index="verify_rule"
            >
            </a-table-column>
            <a-table-column
              key="show_type"
              title="显示类型"
              data-index="show_type"
            >
            </a-table-column>
            <a-table-column
              key="option_content"
              title="选项内容"
              data-index="option_content"
            >
            </a-table-column>
            <a-table-column
              key="default_value"
              title="默认值"
              data-index="default_value"
            >
            </a-table-column>
            <a-table-column key="order_by" title="排序" data-index="order_by">
            </a-table-column>
          </a-table>
          <div>
            说明：字段【{{
              formRef.excludeField
            }}】属于系统保留字段，会默认存储使用，不可设置业务操作，如需操作可后续修改代码使用！！！
            <br />
            选项内容说明：
            模式一：取值方式：[值]/[显示文字];[值]/[显示文字];...。例：【0/北京;1/上海;2/深圳】。模式二：关联字典表，选项内容填写父节点字段code值，默认值填写对应code值。<br />
            关联字段说明：【选择关联字段】会重新进行设置，上面列表中对应字段的设置情况会失效。
          </div>
        </a-card>
        <a-card
          v-show="formRef.temp_type == '1'"
          class="card"
          title="树结构信息设置"
          :style="{ padding: '0 auto' }"
          :bordered="false"
        >
          <a-row class="form-row" :gutter="16">
            <a-col :lg="12" :md="12" :sm="24">
              <a-form-item ref="tree_pid" label="父节点字段" prop="tree_pid">
                <a-select
                  v-model:value="formRef.tree_pid"
                  placeholder="父节点字段"
                  disabled
                >
                  <a-select-option
                    v-for="(item, index) in formRef.fieldList"
                    :key="index"
                    :value="item.field_name"
                  >
                    {{ item.field_comment }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :lg="12" :md="12" :sm="24">
              <a-form-item
                ref="tree_name"
                label="节点名称字段"
                prop="tree_name"
              >
                <a-select
                  v-model:value="formRef.tree_name"
                  placeholder="节点名称字段"
                  disabled
                >
                  <a-select-option
                    v-for="(item, index) in formRef.fieldList"
                    :key="index"
                    :value="item.field_name"
                  >
                    {{ item.field_comment }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          说明：
          在树表中，【父节点字段】会重新进行设置，上面列表中对应字段的设置情况会失效。
        </a-card>
        <a-card
          class="card"
          title="其他信息设置"
          :style="{ padding: '0 auto' }"
          :bordered="false"
        >
          <a-row>
            <a-col :span="24">
              <a-form-item
                v-bind="validateInfos.order_by"
                label="排序"
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 18 }"
              >
                <a-input v-model:value="formRef.order_by" placeholder="排序" disabled />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="24">
              <a-form-item
                v-bind="validateInfos.remark"
                label="备注"
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 18 }"
              >
                <a-textarea
                  v-model:value="formRef.remark"
                  placeholder="备注"
                  :rows="4"
                  disabled
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-card>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form, message, TreeSelect } from "ant-design-vue";
import { findMenuList } from "@/api/system/menu";

interface Fields {
  id?: number;
  table_name?: string;
  table_comment?: string;
  temp_type?: string;
  pack_path?: string;
  mod_name?: string;
  bus_name?: string;
  menu_name?: string;
  menu_id?: string;
  gen_type?: string;
  gen_path?: string;
  fieldList?: [];
  excludeField?: string;
  order_by?: string;
  tree_pid?: string;
  tree_name?: string;
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
    ATreeSelect: TreeSelect,
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
        sm: { span: 16 },
      },
    };
    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      table_name: "",
      table_comment: "",
      temp_type: "",
      pack_path: "",
      mod_name: "",
      bus_name: "",
      menu_name: "",
      menu_id: "",
      gen_type: "",
      gen_path: "",
      fieldList: [],
      excludeField: "",
      order_by: "",
      tree_pid: "",
      tree_name: "",
      remark: "",
    });

    const title = ref("编辑代码生成规则");

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model != null && model != undefined) {
        formRef.id = model?.id;
        formRef.table_name = model?.table_name;
        formRef.table_comment = model?.table_comment;
        formRef.temp_type = model?.temp_type;
        formRef.pack_path = model?.pack_path;
        formRef.mod_name = model?.mod_name;
        formRef.bus_name = model?.bus_name;
        formRef.menu_name = model?.menu_name;
        formRef.menu_id = model?.menu_id;
        formRef.gen_type = model?.gen_type;
        formRef.gen_path = model?.gen_path;
        formRef.fieldList = model?.fieldList;
        formRef.excludeField = model?.excludeField;
        formRef.order_by = model?.order_by;
        formRef.tree_pid = model?.tree_pid;
        formRef.tree_name = model?.tree_name;
        formRef.remark = model?.remark;

        for (let i = 0; i < formRef.fieldList.length; i++) {
          //字段操作
          if (
            formRef.fieldList[i].field_operat == "Y" ||
            formRef.fieldList[i].field_operat == true
          ) {
            formRef.fieldList[i].field_operat = true;
          } else {
            formRef.fieldList[i].field_operat = false;
          }
          //字段列表
          if (
            formRef.fieldList[i].field_list == "Y" ||
            formRef.fieldList[i].field_list == true
          ) {
            formRef.fieldList[i].field_list = true;
          } else {
            formRef.fieldList[i].field_list = false;
          }
          //字段查询
          if (
            formRef.fieldList[i].field_query == "Y" ||
            formRef.fieldList[i].field_query == true
          ) {
            formRef.fieldList[i].field_query = true;
          } else {
            formRef.fieldList[i].field_query = false;
          }
        }
      }
      initMenuList();
    });

    const rulesRef = reactive({
    });

    const treeData = ref([]);
    const initMenuList = () => {
      findMenuList({ types: "0,1,3,4" }).then((response) => {
        const data = fommat({
          arrayList: response.data.data,
          pidStr: "parent_id",
        });
        treeData.value = data;
      });
    };

    const fommat = ({
      arrayList,
      pidStr = "parent_id",
      idStr = "id",
      childrenStr = "children",
    }) => {
      arrayList.push({
        title: "菜单信息",
        id: "1",
        parent_id: "0",
        value: "1",
      });
      let listOjb = {}; // 用来储存{key: obj}格式的对象
      let treeList = []; // 用来储存最终树形结构数据的数组
      // 将数据变换成{key: obj}格式，方便下面处理数据
      for (let i = 0; i < arrayList.length; i++) {
        var data = arrayList[i];
        data.key = data.id;
        data.value = data.id;
        if (data.child_num == 0) {
          data.isLeaf = true;
        }
        data.icon = "";
        listOjb[arrayList[i][idStr]] = data;
      }
      // 根据pid来将数据进行格式化
      for (let j = 0; j < arrayList.length; j++) {
        // 判断父级是否存在
        let haveParent = listOjb[arrayList[j][pidStr]];
        if (haveParent) {
          // 如果有没有父级children字段，就创建一个children字段
          !haveParent[childrenStr] && (haveParent[childrenStr] = []);
          // 在父级里插入子项
          haveParent[childrenStr].push(arrayList[j]);
        } else {
          // 如果没有父级直接插入到最外层
          treeList.push(arrayList[j]);
        }
      }
      return treeList;
    };

    const { validate, validateInfos } = useForm(formRef, rulesRef);

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
      treeData,
    };
  },
});
</script>
<style>
.avatar-uploader > .ant-upload {
  width: 128px;
  height: 128px;
}
</style>

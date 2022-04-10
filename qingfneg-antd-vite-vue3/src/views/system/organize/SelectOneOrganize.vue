<template>
  <a-modal
    :title="title"
    :width="1000"
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
    <div class="table-page-search-wrapper">
      <a-row :gutter="24">
        <a-col :md="24" :lg="6">
          <a-card :bordered="false">
            <organize-tree ref="child" @selectTree="selectTree"></organize-tree>
          </a-card>
        </a-col>
        <a-col :md="24" :lg="18">
          <a-card :bordered="false">
            <div class="table-page-search-wrapper">
              <a-form layout="inline">
                <a-row :gutter="48">
                  <a-col :md="8" :sm="24">
                    <a-form-item label="组织名称">
                      <a-input
                        v-model:value="queryParam.name"
                        placeholder="组织名称"
                        style="width: 160px"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="8" :sm="24">
                    <a-form-item label="组织简称">
                      <a-input
                        v-model:value="queryParam.short_name"
                        placeholder="组织简称"
                        style="width: 160px"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="8" :sm="24">
                    <a-button type="primary" @click="findData()">查询</a-button>
                    <a-button style="margin-left: 8px" @click="resetQueryParam"
                      >重置</a-button
                    >
                  </a-col>
                </a-row>
              </a-form>
            </div>

            <div class="table-operator">
              <div class="line-boder">
                <a-radio-group
                  v-model:value="sdata"
                  :style="{ padding: '10px' }"
                  @change="onChange"
                >
                  <a-radio
                    v-for="(item, i) in list"
                    :key="i"
                    :value="item.id + ':' + item.name"
                  >
                    {{ item.name }}
                  </a-radio>
                </a-radio-group>
              </div>
              <div id="selectValue" class="line-boder1">
                <blockquote v-for="(item, i) in selectList" :key="i">
                  <a-tag color="#2db7f5">
                    {{ item.name }}
                    <a @click="delData(item.id, item.name)">x</a>
                  </a-tag>
                </blockquote>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form, message } from "ant-design-vue";
import OrganizeTree from "./Tree.vue";
import { getOrganizeList } from "@/api/system/organize";

interface QueryParam {
  name?: string;
  short_name?: string;
  status?: string;
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
    OrganizeTree,
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

    const title = ref("单选用户");
    const organize_id = ref("");
    const organize_name = ref("");
    const sdata = ref("");

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model.organize_id != "" && model.organize_id != undefined) {
        selectList.value = [];
        const id = model.organize_id;
        const name = model.organize_name;
        selectList.value.push({ id, name });
        sdata.value = id + ":" + name;
      }
    });

    // 查询参数
    const queryParam = reactive(<QueryParam>{});

    //查询数据
    const list = ref([]);
    const selectList = ref([]);
    const findData = (params = {}) => {
      getOrganizeList({
        parent_id: organize_id.value,
        ...params,
        ...queryParam,
      }).then((response) => {
        // Read total count from server
        list.value = response.data.data;
      });
    };

    const onChange = (e) => {
      const value = e.target.value.split(":");
      const id = value[0];
      const name = value[1];
      selectList.value = [];
      selectList.value.push({ id, name });
    };

    const delData = (id, name) => {
      selectList.value = [];
      sdata.value = "";
    };

    const selectTree = (id, name, value) => {
      organize_id.value = id;
      organize_name.value = name;
      findData({});
    };

    //重置查询参数
    const resetQueryParam = () => {
      queryParam.name = "";
      queryParam.login_name = "";
      queryParam.status = "";
      findData({});
    };

    return {
      formLayout,
      title,
      queryParam,
      list,
      selectTree,
      selectList,
      onChange,
      delData,
      sdata,
      findData,
      resetQueryParam,
    };
  },
});
</script>
<style scoped>
.avatar-uploader > .ant-upload {
  width: 128px;
  height: 128px;
}
.ant-divider-horizontal {
  margin: 0 0 24px 0;
}

.line-boder {
  height: 180px;
  border: 1px solid #5fb878;
  margin-top: 2px;
  border-radius: 4px;
  overflow-y: scroll;
}

.line-boder1 {
  height: 88px;
  border: 1px solid #5fb878;
  margin-top: 2px;
  border-radius: 4px;
  overflow-y: scroll;
  padding: 4px;
}
</style>

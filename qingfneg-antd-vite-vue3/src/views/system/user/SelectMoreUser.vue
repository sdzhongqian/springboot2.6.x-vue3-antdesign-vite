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
                    <a-form-item label="登录账号">
                      <a-input
                        v-model:value="queryParam.login_name"
                        placeholder="登录账号"
                        style="width: 160px"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="8" :sm="24">
                    <a-form-item label="姓名">
                      <a-input
                        v-model:value="queryParam.name"
                        placeholder="姓名"
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
              <div :style="{ borderBottom: '1px solid #E9E9E9' }">
                <a-checkbox @change="onCheckAllChange"> 全选 </a-checkbox>
              </div>
              <div class="line-boder">
                <a-checkbox-group
                  v-model:value="sdata"
                  :style="{ padding: '10px' }"
                  @change="onChange"
                >
                  <a-checkbox
                    v-for="(item, i) in list"
                    :key="i"
                    :value="item.id + ':' + item.name"
                  >
                    {{ item.name }}
                  </a-checkbox>
                </a-checkbox-group>
              </div>
              <div id="selectValue" class="line-boder1">
                <a-tag
                  v-for="(item, i) in selectList"
                  :key="i"
                  color="#2db7f5"
                  :style="{ float: 'left' }"
                >
                  {{ item.name }}
                  <a @click="delData(item.id, item.name)">x</a>
                </a-tag>
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
import { getList } from "@/api/system/user";

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
    const sdata = ref([]);

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model.user_ids != "" && model.user_ids != undefined) {
        selectList.value = [];
        sdata.value = [];
        const ids = model.user_ids.split(",");
        const names = model.user_names.split(",");
        for (let i = 0; i < ids.length; i++) {
          const id = ids[i];
          const name = names[i];
          selectList.value.push({ id, name });
          sdata.value.push(id + ":" + name);
        }
      }
    });

    // 查询参数
    const queryParam = reactive(<QueryParam>{});

    //查询数据
    const list = ref([]);
    const selectList = ref([]);
    const findData = (params = {}) => {
      getList({
        organize_id: organize_id.value,
        ...params,
        ...queryParam,
      }).then((response) => {
        // Read total count from server
        list.value = response.data.data;
      });
    };

    const onChange = (e) => {
      selectList.value = [];
      e.map(function (item, key, ary) {
        const id = item.split(":")[0];
        const name = item.split(":")[1];
        selectList.value.push({ id, name });
      });
    };

    const delData = (id, name) => {
      removeObject(selectList.value, id);
      remove(sdata.value, id + ":" + name);
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

    const onCheckAllChange = (e) => {
      if (e.target.checked) {
        list.value.map(function (item, index) {
          if (sdata.value.indexOf(item.id + ":" + item.name) == -1) {
            sdata.value.push(item.id + ":" + item.name);
            if (item.id != "") {
              selectList.value.push({ id: item.id, name: item.name });
            }
          }
        });
      } else {
        list.value.map(function (item, index) {
          removeObject(selectList.value, item.id);
          remove(sdata.value, item.id + ":" + item.name);
        });
      }
    };

    const remove = (that, val) => {
      var index = that.indexOf(val);
      if (index > -1) {
        that.splice(index, 1);
      }
    };
    const removeObject = (that, val) => {
      that.map(function (item, key, ary) {
        if (item.id == val) {
          that.splice(key, 1);
        }
      });
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
      onCheckAllChange,
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

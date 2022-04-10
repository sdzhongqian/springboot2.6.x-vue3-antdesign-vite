<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="loading"
  >
    <a-card
      style="width:100%"
      :tab-list="tabListNoTitle"
      :active-tab-key="noTitleKey"
      @tabChange="key => onTabChange(key, 'noTitleKey')"
    >
        <block v-for="(item,index) of vodeList" :key="index">
             <p v-if="noTitleKey === item.name">
                <pre v-html="item.content"></pre>
            </p>
        </block>
        <template #tabBarExtraContent>
        <a @click="downloadCode()">下载</a>
        </template>
    </a-card>
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
  onMounted,
} from "vue";
import { message, Form } from "ant-design-vue";
import { getViewCode } from "@/api/system/gencode";
import { findDataAuth } from "@/core/baseAction";
import { formatDate } from "@/utils/util";

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
  setup(props) {
    const { model } = props;
    const tabListNoTitle = ref([]);
    const key = ref("tab1");
    const noTitleKey = ref("");
    const vodeList = ref([]);

    onMounted(() => {
      findViewCode();
    });

    const findViewCode = () => {
      getViewCode(model?.id).then((response) => {
        var data = response.data.data;
        vodeList.value = data;
        for (var i = 0; i < data.length; i++) {
          if (i == 0) {
            noTitleKey.value = data[i].name;
          }
          tabListNoTitle.value.push({ key: data[i].name, tab: data[i].name });
        }
      });
    };

    const onTabChange = (key, type) => {
      console.log(key, type);
      noTitleKey.value = key;
    };

    const downloadCode = () => {
      const baseURL = <string>import.meta.env.VITE_APP_BASE_API;
      window.location.href = baseURL+"/system/gencode/downloadCode?id=" + model?.id;
    };

    return {
      tabListNoTitle,
      key,
      noTitleKey,
      vodeList,
      onTabChange,
      downloadCode,
    };
  },
});
</script>

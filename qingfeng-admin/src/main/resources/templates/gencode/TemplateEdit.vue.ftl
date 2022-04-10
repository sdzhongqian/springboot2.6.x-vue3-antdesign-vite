<#assign isContainFile = 'false'>
<#list fieldList as obj>
  <#if obj.field_operat == 'Y'>
    <#if obj.show_type == '8'>
      <#assign isContainFile = 'true'>
    </#if>
  </#if>
</#list>
<template>
  <a-modal
    :title=title
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  > 
    <a-spin :spinning="loading">
      <a-form v-bind="formLayout">
        <!-- 检查是否有 id 并且大于0，大于0是修改。其他是新增，新增不显示主键ID -->
        <#if tablePd.temp_type == '1'>
          <a-form-item v-bind="validateInfos.parent_name" label="父级名称">
            <a-input disabled v-model:value="formRef.parent_name" placeholder="父级名称"/>
          </a-form-item>
        </#if>

    <#list fieldList as obj>
      <#if obj.field_operat == 'Y'>
        <#if obj.show_type == '1'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-input v-model:value="formRef.${obj.field_name}" placeholder="${obj.field_comment}" />
          </a-form-item>
        </#if>
        <#if obj.show_type == '2'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-textarea v-model:value="formRef.${obj.field_name}" placeholder="${obj.field_comment}" :rows="4" />
          </a-form-item>
        </#if>
        <#if obj.show_type == '3'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-select v-model:value="formRef.${obj.field_name}" placeholder="请选择${obj.field_comment}">
              <a-select-option v-for="(item, index) in ${obj.field_name}_data" :key = "index" :value="item.id"> {{item.name}} </a-select-option>
            </a-select>
          </a-form-item>
        </#if>
        <#if obj.show_type == '4'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-radio-group v-model:value="formRef.${obj.field_name}">
              <a-radio v-for="(item, index) in ${obj.field_name}_data" :value="item.id">{{item.name}} </a-radio>
            </a-radio-group>
          </a-form-item>
        </#if>
        <#if obj.show_type == '5'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-checkbox-group v-model:value="formRef.${obj.field_name}">
              <a-checkbox v-for="(item, index) in ${obj.field_name}_data" :value="item.id">{{item.name}}</a-checkbox>
            </a-checkbox-group>
          </a-form-item>
        </#if>
        <#if obj.show_type == '6'>
        </#if>
        <#if obj.show_type == '7'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-date-picker v-model:value="formRef.${obj.field_name}" placeholder="${obj.field_comment}" style="width:100%" />
          </a-form-item>
        </#if>
        <#if obj.show_type == '8'>
          <a-form-item v-bind="validateInfos.${obj.field_name}" label="${obj.field_comment}">
            <a-upload
              :action="baseURL + '/upload/uploadLocalFile?source=${tablePd.mod_name}_${obj.field_name}'"
              :headers="headers"
              :multiple="true"
              :file-list="file${obj.field_name?cap_first}List"
              @preview="handlePreview"
              @change="handle${obj.field_name?cap_first}Change"
            >
              <a-button>
                <upload-outlined></upload-outlined>
                上传
              </a-button>
            </a-upload>
          </a-form-item>
        </#if>
      </#if>
    </#list>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, reactive, onMounted,ref} from 'vue'
import { Form} from 'ant-design-vue'
import { findDictionaryList } from "@/api/system/dictionary";
<#if isContainFile == 'true'>
import storage from "store";
import { upload, infoFile } from "@/api/common/upload";
import { ACCESS_TOKEN } from "@/store/mutation-types";
</#if>
<#list fieldList as obj>
<#if obj.field_operat == 'Y'>
<#if obj.show_type == '6'>
</#if>
</#if>
</#list>

interface Fields{
  id?: string,
<#list fieldList as obj>
<#if obj.field_operat == 'Y'>
<#if obj.show_type == '5'>
${obj.field_name}:[],
</#if>
<#if obj.show_type != '5'>
${obj.field_name}:string,
</#if>
</#if>
</#list>
}
const useForm = Form.useForm;
export default defineComponent({
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    },
    model: {
      type: Object,
      default: () => null
    }
  },
  components: {
    <#list fieldList as obj>
    <#if obj.field_operat == 'Y'>
    <#if obj.show_type == '6'>
    </#if>
    </#if>
    </#list>
  },
  setup (props) {
    const {model} = props
    const formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
      }
    }

    const baseURL = <string>import.meta.env.VITE_APP_BASE_API;
    <#list fieldList as obj>
    <#if obj.show_type == '3' || obj.show_type == '4' || obj.show_type == '5'>
      const ${obj.field_name}_data = ref([]);
    </#if>
    <#if obj.show_type == '8'>
    const file${obj.field_name?cap_first}List = ref([]);
    </#if>
    </#list>

    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      <#list fieldList as obj>
      <#if obj.field_operat == 'Y'>
      <#if obj.show_type == '5'>
      ${obj.field_name}:[],
      </#if>
      <#if obj.show_type != '5'>
      ${obj.field_name}:"",
      </#if>
      </#if>
      </#list>
    });

    const title = ref("新增数据")

    // 自动请求并暴露内部方法
    onMounted(() => {
          formRef.parent_id = model?.parent_id;
          formRef.parent_name = model?.parent_name;
        if (model?.id != null && model?.id != undefined) {
        formRef.id = model?.id;
        <#list fieldList as obj>
        <#if obj.field_operat == 'Y'>
        <#if obj.show_type == '8'>
        formRef.${obj.field_name}=model?.${obj.field_name};
        if(formRef.${obj.field_name}.indexOf("#")!=-1){
          let ${obj.field_name}Files = formRef.${obj.field_name}.split("#");
          let ${obj.field_name}_names = ${obj.field_name}Files[0].split(",");
          let ${obj.field_name}_urls = ${obj.field_name}Files[1].split(",");
          for (let i = 0; i < ${obj.field_name}_names.length; i++) {
            file${obj.field_name?cap_first}List.value.push({
              name: ${obj.field_name}_names[i],
              status: "done",
              url: ${obj.field_name}_urls[i],
            });
          }
        }
        </#if>
        <#if obj.show_type != '8'>
        formRef.${obj.field_name}=model?.${obj.field_name};
        </#if>
        </#if>
        </#list>
        title.value = "编辑数据";
      }
        initData();
    })

    const rulesRef = reactive({
      id: [{required: false}],
      <#list fieldList as obj>
      <#if obj.field_operat == 'Y' && obj.verify_rule=='required'>
        ${obj.field_name}: [{required: true, max: 50, message: '请输入${obj.field_comment}且不多余50个字符'}],
      </#if>
      <#if obj.field_operat == 'Y' && obj.verify_rule!='required'>
        ${obj.field_name}: [{required: false}],
      </#if>
      </#list>
      <#if tablePd.temp_type == '1'>
      parent_id: [{required: false}],
      parent_name: [{required: false}],
      </#if>
    })

    const initData = () => {
      <#list fieldList as obj>
      <#if obj.show_type == '3' || obj.show_type == '4' || obj.show_type == '5'>
      <#if obj.option_content?contains(";")>
      const options_${obj.field_name} = [
      <#list obj.option_content?split(";") as name>
      <#assign param = name?split("/")>
      { id: '${param[0]}', name: '${param[1]}' },
      </#list>
      ];
      ${obj.field_name}_data.value = options_${obj.field_name};
      </#if>
      <#if !obj.option_content?contains(";")>
      findDictionaryList({parent_code:'${obj.option_content}'}).then((response) => {
        ${obj.field_name}_data.value = response.data.data;
      })
      </#if>

      </#if>
      <#if obj.show_type != '5'>
      <#if obj.default_value??>
        formRef.${obj.field_name} = '${obj.default_value!''}';
      </#if>
      </#if>
      <#if obj.show_type == '5'>
      <#if obj.default_value??>
      if (model == null || model == "") {
        formRef.${obj.field_name} = '${obj.default_value!''}';
      }
      </#if>
      </#if>
      </#list>
    }
    const { validate, validateInfos } = useForm(formRef, rulesRef);

    <#if isContainFile == 'true'>
    const accessToken = storage.get(ACCESS_TOKEN);
    const headers = ref();
    headers.value = {
      Authorization: "bearer " + accessToken,
    };
    const handlePreview = (file) => {
      window.location.href =
        baseURL +
        "/upload/downloadFile?name=" +
        file.name +
        "&file_path=" +
        file.url;
    };
    </#if>

    <#list fieldList as obj>
    <#if obj.field_operat == 'Y'>
    <#if obj.show_type == '8'>
    const handle${obj.field_name?cap_first}Change = (info: UploadChangeParam) => {
      let resFileList = [...info.fileList];
      resFileList = resFileList.map((file) => {
        if (file.response) {
          file.url = file.response.data.file_path;
        }
        return file;
      });
      file${obj.field_name?cap_first}List.value = resFileList;
      let names = [];
      let urls = [];
      file${obj.field_name?cap_first}List.value.map((file) => {
        names.push(file.name);
        urls.push(file.url);
      });
      formRef.${obj.field_name} = names.join(",") + "#" + urls.join(",");
    };
    </#if>
    </#if>
    </#list>


    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
      baseURL,
      <#if isContainFile == 'true'>
      headers,
      handlePreview,
      </#if>
      <#list fieldList as obj>
      <#if obj.show_type == '3' || obj.show_type == '4' || obj.show_type == '5'>
      ${obj.field_name}_data,
      </#if>
      <#if obj.show_type == '8'>
      handle${obj.field_name?cap_first}Change,
      file${obj.field_name?cap_first}List,
      </#if>
      </#list>
    }
  }
})
</script>

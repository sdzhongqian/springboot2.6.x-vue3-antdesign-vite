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
                    <a-form-item v-bind="validateInfos.parent_name" label="父级名称">
                        <a-input disabled v-model:value="formRef.parent_name" placeholder="父级名称"/>
                    </a-form-item>

                            <a-form-item v-bind="validateInfos.name" label="字典名称">
                                <a-input disabled v-model:value="formRef.name" placeholder="字典名称" />
                            </a-form-item>
                            <a-form-item v-bind="validateInfos.short_name" label="字典简称">
                                <a-select disabled v-model:value="formRef.short_name" placeholder="请选择字典简称">
                                    <a-select-option v-for="(item, index) in short_name_data" :key = "index" :value="item.id"> {{item.name}} </a-select-option>
                                </a-select>
                            </a-form-item>
                            <a-form-item v-bind="validateInfos.code" label="字典编码">
                                <a-select disabled v-model:value="formRef.code" placeholder="请选择字典编码">
                                    <a-select-option v-for="(item, index) in code_data" :key = "index" :value="item.id"> {{item.name}} </a-select-option>
                                </a-select>
                            </a-form-item>
                            <a-form-item v-bind="validateInfos.classify" label="分类">
                                <a-radio-group disabled v-model:value="formRef.classify">
                                    <a-radio v-for="(item, index) in classify_data" :value="item.id">{{item.name}} </a-radio>
                                </a-radio-group>
                            </a-form-item>
                            <a-form-item v-bind="validateInfos.order_by" label="排序">
                                <a-checkbox-group disabled v-model:value="formRef.order_by">
                                    <a-checkbox disabled v-for="(item, index) in order_by_data" :value="item.id">{{item.name}}</a-checkbox>
                                </a-checkbox-group>
                            </a-form-item>
                            <a-form-item v-bind="validateInfos.remark" label="备注">
                                <a-upload
                                    :action="baseURL + '/upload/uploadLocalFile?source=common_remark'"
                                    :headers="headers"
                                    :multiple="true"
                                    :file-list="fileRemarkList"
                                    @preview="handlePreview"
                                    @change="handleRemarkChange"
                                >
                                </a-upload>
                            </a-form-item>
            </a-form>
        </a-spin>
    </a-modal>
</template>

<script lang="ts">
    import {defineComponent, reactive, onMounted,ref} from 'vue'
    import { Form} from 'ant-design-vue'
    import { findDictionaryList } from "@/api/system/dictionary";
    import storage from "store";
    import { upload, infoFile } from "@/api/common/upload";
    import { ACCESS_TOKEN } from "@/store/mutation-types";

    interface Fields{
        id?: string,
            name:string,
            short_name:string,
            code:string,
            classify:string,
            order_by:[],
            remark:string,
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
                const short_name_data = ref([]);
                const code_data = ref([]);
                const classify_data = ref([]);
                const order_by_data = ref([]);
                const fileRemarkList = ref([]);

                // 表单字段
                const formRef = reactive(<Fields>{
                    id: "",
                    name:"",
                    short_name:"",
                    code:"",
                    classify:"",
                    order_by:[],
                    remark:"",
                });

                    const title = ref("新增数据")

                    // 自动请求并暴露内部方法
                    onMounted(() => {
                        formRef.parent_id = model?.parent_id;
                        formRef.parent_name = model?.parent_name;
                        if (model?.id != null && model?.id != undefined) {
                        formRef.id = model?.id;
                        formRef.name=model?.name;
                        formRef.short_name=model?.short_name;
                        formRef.code=model?.code;
                        formRef.classify=model?.classify;
                        formRef.order_by=model?.order_by;
                        formRef.remark=model?.remark;
                        if(formRef.remark.indexOf("#")!=-1){
                        let remarkFiles = formRef.remark.split("#");
                        let remark_names = remarkFiles[0].split(",");
                        let remark_urls = remarkFiles[1].split(",");
                        for (let i = 0; i < remark_names.length; i++) {
                        fileRemarkList.value.push({
                        name: remark_names[i],
                        status: "done",
                        url: remark_urls[i],
                    });
                    }
                    }
                        title.value = "编辑数据";
                    }
                        initData();
                    })

                    const rulesRef = reactive({
                        id: [{required: false}],
                        name: [{required: true, max: 50, message: '请输入字典名称且不多余50个字符'}],
                        short_name: [{required: false}],
                        code: [{required: false}],
                        classify: [{required: false}],
                        order_by: [{required: false}],
                        remark: [{required: false}],
                        parent_id: [{required: false}],
                        parent_name: [{required: false}],
                    })

                    const initData = () => {
                        const options_short_name = [
                    { id: '0', name: '北京' },
                    { id: '1', name: '上海' },
                    { id: '2', name: '深圳' },
                        ];
                        short_name_data.value = options_short_name;

                        findDictionaryList({parent_code:'flxx'}).then((response) => {
                        code_data.value = response.data.data;
                    })

                        findDictionaryList({parent_code:'flxx'}).then((response) => {
                        classify_data.value = response.data.data;
                    })

                        const options_order_by = [
                    { id: '0', name: '北京' },
                    { id: '1', name: '上海' },
                    { id: '2', name: '深圳' },
                        ];
                        order_by_data.value = options_order_by;

                        formRef.remark = '1';
                    }
                    const { validate, validateInfos } = useForm(formRef, rulesRef);

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

                    const handleRemarkChange = (info: UploadChangeParam) => {
                        let resFileList = [...info.fileList];
                        resFileList = resFileList.map((file) => {
                        if (file.response) {
                        file.url = file.response.data.file_path;
                    }
                        return file;
                    });
                        fileRemarkList.value = resFileList;
                        let names = [];
                        let urls = [];
                        fileRemarkList.value.map((file) => {
                        names.push(file.name);
                        urls.push(file.url);
                    });
                        formRef.remark = names.join(",") + "#" + urls.join(",");
                    };


                    return {
                        formLayout,
                        formRef,
                        validate,
                        validateInfos,
                        title,
                        baseURL,
                        headers,
                        handlePreview,
                        short_name_data,
                        code_data,
                        classify_data,
                        order_by_data,
                        handleRemarkChange,
                        fileRemarkList,
                    }
                    }
                    })
</script>

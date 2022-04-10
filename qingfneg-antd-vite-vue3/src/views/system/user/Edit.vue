<template>
  <a-modal
    :title="title"
    :width="900"
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
    <a-spin :spinning="loading">
      <a-form v-bind="formLayout">
        <a-row>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.login_name" label="登录账号">
              <a-input
                v-model:value="formRef.login_name"
                placeholder="登录账号"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.name" label="姓名">
              <a-input
                v-model:value="formRef.name"
                placeholder="姓名"
              /> </a-form-item
          ></a-col>
        </a-row>
        <a-row v-if="formRef.id == ''">
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.login_password" label="登录密码">
              <a-input-password
                v-model:value="formRef.login_password"
                placeholder="登录密码"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              v-bind="validateInfos.confirm_password"
              label="确认密码"
            >
              <a-input-password
                v-model:value="formRef.confirm_password"
                placeholder="确认密码"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-item label="性别" v-bind="validateInfos.region">
              <a-select v-model:value="formRef.sex" placeholder="请选择性别">
                <a-select-option value="1"> 男 </a-select-option>
                <a-select-option value="2"> 女 </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.phone" label="手机号">
              <a-input v-model:value="formRef.phone" placeholder="手机号" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.email" label="邮箱">
              <a-input v-model:value="formRef.email" placeholder="邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.birth_date" label="出生日期">
               <a-input v-model:value="formRef.birth_date" placeholder="出生日期" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.motto" label="座右铭">
              <a-input v-model:value="formRef.motto" placeholder="座右铭" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="排序">
              <a-input v-model:value="formRef.order_by" placeholder="排序" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.live_address" label="居住地址">
              <a-input
                v-model:value="formRef.live_address"
                placeholder="居住地址"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.birth_address" label="出生地址">
              <a-input
                v-model:value="formRef.birth_address"
                placeholder="出生地址"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row v-if="formRef.id == ''">
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.organize_name" label="组织名称">
              <a-input
                disabled
                v-model:value="formRef.organize_name"
                placeholder="组织名称"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item v-bind="validateInfos.position" label="职位">
              <a-input v-model:value="formRef.position" placeholder="职位" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-item
              v-bind="validateInfos.live_address"
              label="头像地址"
              :label-col="{ span: 4 }"
              :wrapper-col="{ span: 20 }"
            >
              <a-upload
                v-model:file-list="fileList"
                name="file"
                list-type="picture-card"
                class="avatar-uploader"
                :show-upload-list="false"
                :customRequest="uploadImage"
                :before-upload="beforeUpload"
                :headers="headers"
                @change="handleChange"
              >
                <img v-if="imageUrl" width="120" :src="imageUrl" alt="avatar" />
                <div v-else>
                  <loading-outlined v-if="loading"></loading-outlined>
                  <plus-outlined v-else></plus-outlined>
                  <div class="ant-upload-text">上传</div>
                </div>
              </a-upload>
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
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form, message, UploadChangeParam, UploadProps } from "ant-design-vue";
import { upload, infoFile } from "@/api/common/upload";
// import pick from 'lodash.pick'

interface Fields {
  id?: number;
  name?: string;
  short_name?: string;
  order_by?: string;
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
      login_name: "",
      name: "",
      login_password: "",
      confirm_password: "",
      sex: "",
      phone: "",
      email: "",
      birth_date: "",
      motto: "",
      live_address: "",
      birth_address: "",
      organize_id: "",
      organize_name: "",
      position: "",
      order_by: "",
      remark: "",
      fileIds: "",
      old_login_name: "",
    });

    const title = ref("新增数据");

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model != null && model != undefined) {
        if (model?.id != "" && model?.id != undefined) {
          formRef.id = model?.id;
          formRef.name = model?.name;
          formRef.login_name = model?.login_name;
          // formRef.login_password = model?.login_password;
          // formRef.confirm_password = model?.confirm_password;
          formRef.sex = model?.sex;
          formRef.phone = model?.phone;
          formRef.email = model?.email;
          formRef.birth_date = model?.birth_date;
          formRef.motto = model?.motto;
          formRef.live_address = model?.live_address;
          formRef.birth_address = model?.birth_address;
          formRef.organize_id = model?.organize_id;
          formRef.organize_name = model?.organize_name;
          formRef.position = model?.position;
          formRef.order_by = model?.order_by;
          formRef.remark = model?.remark;
          formRef.old_login_name = model?.login_name;
          title.value = "编辑数据";
          findFileInfo(model?.id);
        } else {
          formRef.organize_id = model?.organize_id;
          formRef.organize_name = model?.organize_name;
          rulesRef.login_password = [
            { required: true, message: "必填项不能为空" },
            { min: 0, max: 50, message: "长度不得大于50个字符" },
          ];
          rulesRef.confirm_password = [
            { required: true, validator: confirm_password, trigger: "change" },
            { min: 0, max: 50, message: "长度不得大于50个字符" },
          ];
        }
      }
    });

    let confirm_password = async (_rule: RuleObject, value: string) => {
      if (value === "") {
        return Promise.reject("必填项不能为空");
      } else if (value !== formRef.login_password) {
        return Promise.reject("两次密码输入不一致!");
      } else {
        return Promise.resolve();
      }
    };

    const rulesRef = reactive({
      id: [{ required: false }],
      login_name: [
        { required: true, max: 50, message: "请输入登录名称且不多余50个字符" },
      ],
      name: [
        { required: true, max: 50, message: "请输入用户名称且不多余50个字符" },
      ],
      position: [{ min: 0, max: 50, message: "长度不得大于50个字符" }],
      short_name: [{ max: 50, message: "组织简称不能超过50个字符" }],
      code: [{ max: 50, message: "组织简称不能超过50个字符" }],
      order_by: [{ max: 50, message: "不能超过50个字符" }],
      remark: [
        {
          min: 0,
          max: 500,
          message: "长度不得大于500个字符",
        },
      ],
      organize_id: [{ required: false }],
      organize_name: [{ required: false }],
      old_login_name: [{ required: false }],
      fileIds: [{ required: false }],
      sex: [{ max: 50, message: "性别不能超过50个字符" }],
      phone: [{ max: 11, message: "手机号不能超过11个字符" }],
      email: [{ max: 50, message: "邮箱不能超过50个字符" }],
      birth_date:  [{ required: false }],
      motto: [{ max: 50, message: "座右铭不能超过50个字符" }],
      live_address: [{ max: 50, message: "居住地址不能超过50个字符" }],
      birth_address: [{ max: 50, message: "出生地址不能超过50个字符" }],
      position: [{ max: 50, message: "职位不能超过50个字符" }],
    });

    const headers = ref({});
    //附件上传
    const loading = ref<boolean>(false);
    const imageUrl = ref<string>("");
    const beforeUpload = (file: UploadProps["fileList"][number]) => {
      const isJpgOrPng =
        file.type === "image/jpeg" || file.type === "image/png";
      if (!isJpgOrPng) {
        message.error("请上传正确的图片格式【jepg/png】");
      }
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isLt2M) {
        message.error("上传图片大小不能大于2MB");
      }
      return isJpgOrPng && isLt2M;
    };
    // 上传头像
    const uploadImage = (file: UploadChangeParam) => {
      if (file.file.status === "uploading") {
        loading.value = true;
        return;
      }
      const formData = new FormData();
      formData.append("file", file.file);
      formData.append("source", "userAvatar");
      upload(formData).then(
        (res) => {
          if (res.data.success) {
            loading.value = false;
            imageUrl.value = res.data.data.show_file_path;
            formRef.fileIds = res.data.data.id;
          }
        },
        (err) => {
          this.avatarLoading = false;
        }
      );
    };

    const handleChange = (info) => {
      if (info.file.status === "uploading") {
        loading.value = true;
        return;
      }
      if (info.file.status === "done") {
        // Get this url from response in real world.
        getBase64(info.file.originFileObj, (imageUrl) => {
          loading.value = false;
          imageUrl.value = res.data.show_file_path;
        });
      }
    };

    function getBase64(img: Blob, callback: (base64Url: string) => void) {
      const reader = new FileReader();
      reader.addEventListener("load", () => callback(reader.result as string));
      reader.readAsDataURL(img);
    }

    let findFileInfo = (id) => {
      if (id != "") {
        let params = {
          id:id,
          source:"userAvatar"
        }
        infoFile(params).then((response) => {
          if(response.data.success){
            imageUrl.value = response.data.data.show_file_path;
          }
        });
      }
    };

    const { validate, validateInfos } = useForm(formRef, rulesRef);

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
      beforeUpload,
      uploadImage,
      handleChange,
      headers,
      loading,
      imageUrl,
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

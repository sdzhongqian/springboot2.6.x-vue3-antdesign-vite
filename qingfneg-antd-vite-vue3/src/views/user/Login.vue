<template>
  <div class="main">
    <a-form
      id="formLogin"
      class="user-layout-login"
      ref="formLogin"
      :model="formRef"
      @submit="handleSubmit"
    >
        <!-- 账户密码登录 -->
        <a-alert
          type="error"
          showIcon
          style="margin-bottom: 24px"
          :message="$t('user.login.message-invalid-credentials')"
        />
        <a-form-item v-bind="validateInfos.username">
          <a-input
            size="large"
            type="text"
            :placeholder="$t('user.login.username.placeholder')"
            v-model:value="formRef.username"
          >
            <template #prefix>
              <UserOutlined :style="{ color: 'rgba(0,0,0,.25)' }" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item v-bind="validateInfos.password">
          <a-input-password
            size="large"
            :placeholder="$t('user.login.password.placeholder')"
            v-model:value="formRef.password"
          >
            <template #prefix>
              <LockOutlined :style="{ color: 'rgba(0,0,0,.25)' }" />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item v-bind="validateInfos.code">
          <a-row type="flex">
            <a-col :flex="3">
              <a-input
                size="large"
                type="text"
                placeholder="验证码"
                v-model:value="formRef.code"
              />
            </a-col>
            <a-col :flex="2">
              <img
                :src="imageCode"
                alt="codeImage"
                class="code-image"
                @click="getCodeImage"
              />
            </a-col>
          </a-row>
        </a-form-item>
        
      <a-form-item v-bind="validateInfos.rememberMe">
        <a-checkbox v-model:checked="formRef.rememberMe" style="float:left">
          {{ $t("user.login.remember-me") }}
        </a-checkbox>
      </a-form-item>

      <a-form-item style="margin-top: 24px">
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="login-button"
          :loading="state.loginBtn"
          :disabled="state.loginBtn"
        >{{$t("user.login.login")}}</a-button>
      </a-form-item>
    </a-form>

  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, ref, UnwrapRef } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { FormState } from './types'
import { useStore } from 'vuex'
import { timeFix , randomNum } from '@/utils/util'
import { notification, message } from 'ant-design-vue'
import { useForm } from 'ant-design-vue/lib/form'
import storage from "@/utils/storage"
import axios from 'axios';

export default defineComponent({
  setup() {
    let imageCode = ref("");
    let randomId = ref("");
    const router = useRouter()
    const store = useStore()
    const { t } = useI18n()
    // const Login = (userInfo) => store.dispatch('Login', userInfo)
    const Logout = () => store.dispatch('Logout')
    const codeUrl =<string>import.meta.env.VITE_APP_BASE_API + `/auth/captcha`

    onMounted(() => {
      getCodeImage();
      
      if(storage.get("username")!=''&&storage.get("username")!=null){
        formRef.username = storage.get("username");
        formRef.rememberMe = true;
      }
    })

    const state = reactive({
      loginBtn: false,
      // login type: 0 email, 1 username, 2 telephone
      loginType: 0
    })
    // #region 表单相关

    const formRef: UnwrapRef<FormState> = reactive({
      rememberMe: false,
      username: '',
      password: '',
      code: ''
    })

    const rulesRef = reactive({
      rememberMe: [{ trigger: 'checked' }],
      username: [
        {
          required: true,
          message: t('user.username.required')
        }
      ],
      password: [{ required: true, message: t('user.password.required') }, {}],
      code: [
        {
          required: true,
          message: t('user.verification-code.required')
        }
      ]
    })


    const { validate, validateInfos } = useForm(formRef, rulesRef)

    const handleSubmit = (e: Event) => {
      e.preventDefault()
      state.loginBtn = true
      const validateFieldsKey = ['username', 'password','code']
      validate(validateFieldsKey)
        .then((result) => {
          // console.log(res, toRaw(formRef));
          const loginParams = { ...result,rememberMe:formRef.rememberMe,key: randomId }
          // delete loginParams.username
          store.dispatch('Login', loginParams)
            .then((res) => loginSuccess(res))
            .catch(err => requestFailed(err))
            .finally(() => {
              state.loginBtn = false
            })
        }).catch(err => {
          setTimeout(() => {
            state.loginBtn = false
          }, 600)
      })
    }

    const loginSuccess = (res) => {
      if (res.status==200) {
        //保存账号密码
        if(formRef.rememberMe){
          storage.set("username",formRef.username)
        }else{
          storage.remove("username")
        }
       
        //执行跳转
        router.push({ path: '/' })
       // 延迟 1 秒显示欢迎信息
        setTimeout(() => {
          notification.success({
            message: `欢迎`+store.getters.nickname,
            description: `${timeFix()}，欢迎回来`
          })
        }, 1000)
      } else {
        // 延迟 1 秒显示欢迎信息
        setTimeout(() => {
          this.$notification.warning({
            message: "登录失败",
            description: res.msg,
          });
        }, 1000);
      }
    }

    const requestFailed = (err) => {
      notification['error']({
        message: '错误',
        description: (err || err.data || {}).message || '请求出现错误，请稍后再试',
        duration: 4,
      });
    }

    const getCodeImage =()=> {
      randomId = randomNum(24, 16);
      axios({
        method: "GET",
        url: codeUrl+`?key=`+randomId,
        responseType: "arraybuffer",
      })
        .then((res) => {
          return (
            "data:image/png;base64," +
            btoa(
              new Uint8Array(res.data).reduce(
                (data, byte) => data + String.fromCharCode(byte),
                ""
              )
            )
          );
        })
        .then((res) => {
          imageCode.value = res;
        })
        .catch((e) => {
          console.log(e)
        });
    }

    return{
      formRef,
      rulesRef,
      state,
      handleSubmit,
      validateInfos,
      imageCode,
      getCodeImage
    }
  }
})
</script>

<style lang="less" scoped>
@import '../../style/index.less';

.pro-layout-login-main {
  width: 360px;
  position: absolute;
  left: 50%;
  top: 30%;
  transform: translate(-50%, -50%);
}

.user-layout-login {
  label {
    font-size: 14px;
  }

  .forget-password {
    font-size: 14px;
  }

  button.login-button {
    padding: 0 15px;
    font-size: 16px;
    height: 40px;
    width: 100%;
  }

  .user-login-other {
    text-align: left;
    margin-top: 24px;
    line-height: 22px;

    .anticon {
      font-size: 24px;
      color: rgba(0, 0, 0, 0.2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: @primary-color;
      }
    }

    .register {
      float: right;
    }
  }
}
</style>

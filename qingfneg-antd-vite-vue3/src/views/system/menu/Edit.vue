<template>
  <a-modal
    :title="title"
    :width="640"
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
        <!-- 检查是否有 id 并且大于0，大于0是修改。其他是新增，新增不显示主键ID -->
        <a-form-item v-bind="validateInfos.parent_name" label="父级名称">
          <a-input
            disabled
            v-model:value="formRef.parent_name"
            placeholder="字典父级名称"
          />
        </a-form-item>
        <a-form-item label="菜单类型" v-bind="validateInfos.region">
          <a-select v-model:value="formRef.type" placeholder="请选择菜单类型">
            <a-select-option value="0" v-if="formRef.parent_type == '0'"
              >目录</a-select-option
            >
            <a-select-option value="1" v-if="formRef.parent_type == '0'"
              >菜单</a-select-option
            >
            <a-select-option value="2" v-if="formRef.parent_type == '1'"
              >按钮</a-select-option
            >
            <a-select-option value="3" v-if="formRef.parent_type == '0'"
              >外链</a-select-option
            >
            <a-select-option value="4" v-if="formRef.parent_type == '0'"
              >iframe</a-select-option
            >
          </a-select>
        </a-form-item>

        <a-form-item v-bind="validateInfos.name" label="菜单名称">
          <a-input v-model:value="formRef.name" placeholder="菜单名称" />
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.path"
          label="路由地址"
          v-show="
            formRef.type == '0' ||
            formRef.type == '1' ||
            formRef.type == '3' ||
            formRef.type == '4'
          "
        >
          <a-input v-model:value="formRef.path" placeholder="路由地址" />
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.redirect"
          label="重定向地址"
          v-show="formRef.type == '0'"
        >
          <a-input v-model:value="formRef.redirect" placeholder="重定向地址" />
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.component"
          label="组件路径"
          v-show="formRef.type == '1'"
        >
          <a-input v-model:value="formRef.component" placeholder="组件路径" />
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.permission"
          label="权限标识"
          v-show="formRef.type == '1' || formRef.type == '2'"
        >
          <a-input v-model:value="formRef.permission" placeholder="权限标识" />
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.icon"
          label="菜单图标"
          v-show="
            formRef.type == '0' ||
            formRef.type == '1' ||
            formRef.type == '3' ||
            formRef.type == '4'
          "
        >
          <!-- <a-input v-model:value="formRef.icon" placeholder="菜单图标" /> -->
          <a-select
            ref="select"
            v-model:value="formRef.icon"
            @change="handleChange"
            show-search
          >
            <a-select-option v-for="item in iconList" :key="item" :value="item">
              <Icon :icon="item"></Icon>
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          v-bind="validateInfos.keepAlive"
          label="是否缓存"
          v-show="
            formRef.type == '0' ||
            formRef.type == '1' ||
            formRef.type == '3' ||
            formRef.type == '4'
          "
        >
          <a-radio-group v-model:value="formRef.keepAlive" name="keepAlive">
            <a-radio value="true"> 缓存 </a-radio>
            <a-radio value="false"> 不缓存 </a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="排序">
          <a-input v-model:value="formRef.order_by" placeholder="排序" />
        </a-form-item>
        <a-form-item v-bind="validateInfos.remark" label="备注">
          <a-textarea
            v-model:value="formRef.remark"
            placeholder="备注"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, ref } from "vue";
import { Form } from "ant-design-vue";
// import pick from 'lodash.pick'
import {Icon} from '@/utils/icon'


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
  components:{
    Icon
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
        sm: { span: 14 },
      },
    };
    // 表单字段
    const formRef = reactive(<Fields>{
      id: "",
      name: "",
      short_name: "",
      code: "",
      order_by: "",
      remark: "",
      type: "",
      path: "",
      redirect: "",
      component: "",
      permission: "",
      icon: "",
      keepAlive: "",
      parent_id: "",
      parent_name: "",
      menu_cascade: "",
      level_num: "",
      parent_type: "",
    });

    const title = ref("新增数据");

    const iconList = ref([]);
    iconList.value = [
      "AccountBookOutlined",
      "AimOutlined",
      "AlertOutlined",
      "ApartmentOutlined",
      "ApiOutlined",
      "AppstoreAddOutlined",
      "AppstoreOutlined",
      "AudioOutlined",
      "AudioMutedOutlined",
      "AuditOutlined",
      "BankOutlined",
      "BarcodeOutlined",
      "BarsOutlined",
      "BellOutlined",
      "BlockOutlined",
      "BookOutlined",
      "BorderOutlined",
      "BorderlessTableOutlined",
      "BranchesOutlined",
      "BugOutlined",
      "BuildOutlined",
      "BulbOutlined",
      "CalculatorOutlined",
      "CalendarOutlined",
      "CameraOutlined",
      "CarOutlined",
      "CarryOutOutlined",
      "CiCircleOutlined",
      "CiOutlined",
      "ClearOutlined",
      "CloudDownloadOutlined",
      "CloudOutlined",
      "CloudServerOutlined",
      "CloudSyncOutlined",
      "CloudUploadOutlined",
      "ClusterOutlined",
      "CodeOutlined",
      "CoffeeOutlined",
      "CommentOutlined",
      "CompassOutlined",
      "CompressOutlined",
      "ConsoleSqlOutlined",
      "ContactsOutlined",
      "ContainerOutlined",
      "ControlOutlined",
      "CopyrightCircleOutlined",
      "CopyrightOutlined",
      "CreditCardOutlined",
      "CrownOutlined",
      "CustomerServiceOutlined",
      "DashboardOutlined",
      "DatabaseOutlined",
      "DeleteColumnOutlined",
      "DeleteRowOutlined",
      "DeliveredProcedureOutlined",
      "DeploymentUnitOutlined",
      "DesktopOutlined",
      "DingtalkOutlined",
      "DisconnectOutlined",
      "DislikeOutlined",
      "DollarCircleOutlined",
      "DollarOutlined",
      "DownloadOutlined",
      "EllipsisOutlined",
      "EnvironmentOutlined",
      "EuroCircleOutlined",
      "EuroOutlined",
      "ExceptionOutlined",
      "ExpandAltOutlined",
      "ExpandOutlined",
      "ExperimentOutlined",
      "ExportOutlined",
      "EyeOutlined",
      "EyeInvisibleOutlined",
      "FieldBinaryOutlined",
      "FieldNumberOutlined",
      "FieldStringOutlined",
      "FieldTimeOutlined",
      "FileAddOutlined",
      "FileDoneOutlined",
      "FileExcelOutlined",
      "FileExclamationOutlined",
      "FileOutlined",
      "FileGifOutlined",
      "FileImageOutlined",
      "FileJpgOutlined",
      "FileMarkdownOutlined",
      "FilePdfOutlined",
      "FilePptOutlined",
      "FileProtectOutlined",
      "FileSearchOutlined",
      "FileSyncOutlined",
      "FileTextOutlined",
      "FileUnknownOutlined",
      "FileWordOutlined",
      "FileZipOutlined",
      "FilterOutlined",
      "FireOutlined",
      "FlagOutlined",
      "FolderAddOutlined",
      "FolderOutlined",
      "FolderOpenOutlined",
      "FolderViewOutlined",
      "ForkOutlined",
      "FormatPainterOutlined",
      "FrownOutlined",
      "FunctionOutlined",
      "FundProjectionScreenOutlined",
      "FundViewOutlined",
      "FunnelPlotOutlined",
      "GatewayOutlined",
      "GifOutlined",
      "GiftOutlined",
      "GlobalOutlined",
      "GoldOutlined",
      "GroupOutlined",
      "HddOutlined",
      "HeartOutlined",
      "HistoryOutlined",
      "HomeOutlined",
      "HourglassOutlined",
      "IdcardOutlined",
      "ImportOutlined",
      "InboxOutlined",
      "InsertRowAboveOutlined",
      "InsertRowBelowOutlined",
      "InsertRowLeftOutlined",
      "InsertRowRightOutlined",
      "InsuranceOutlined",
      "InteractionOutlined",
      "KeyOutlined",
      "LaptopOutlined",
      "LayoutOutlined",
      "LikeOutlined",
      "LineOutlined",
      "LinkOutlined",
      "Loading3QuartersOutlined",
      "LoadingOutlined",
      "LockOutlined",
      "MacCommandOutlined",
      "MailOutlined",
      "ManOutlined",
      "MedicineBoxOutlined",
      "MehOutlined",
      "MenuOutlined",
      "MergeCellsOutlined",
      "MessageOutlined",
      "MobileOutlined",
      "MoneyCollectOutlined",
      "MonitorOutlined",
      "MoreOutlined",
      "NodeCollapseOutlined",
      "NodeExpandOutlined",
      "NodeIndexOutlined",
      "NotificationOutlined",
      "NumberOutlined",
      "OneToOneOutlined",
      "PaperClipOutlined",
      "PartitionOutlined",
      "PayCircleOutlined",
      "PercentageOutlined",
      "PhoneOutlined",
      "PictureOutlined",
      "PlaySquareOutlined",
      "PoundCircleOutlined",
      "PoundOutlined",
      "PoweroffOutlined",
      "PrinterOutlined",
      "ProfileOutlined",
      "ProjectOutlined",
      "PropertySafetyOutlined",
      "PullRequestOutlined",
      "PushpinOutlined",
      "QrcodeOutlined",
      "ReadOutlined",
      "ReconciliationOutlined",
      "RedEnvelopeOutlined",
      "ReloadOutlined",
      "RestOutlined",
      "RobotOutlined",
      "RocketOutlined",
      "RotateLeftOutlined",
      "RotateRightOutlined",
      "SafetyCertificateOutlined",
      "SafetyOutlined",
      "SaveOutlined",
      "ScanOutlined",
      "ScheduleOutlined",
      "SearchOutlined",
      "SecurityScanOutlined",
      "SelectOutlined",
      "SendOutlined",
      "SettingOutlined",
      "ShakeOutlined",
      "ShareAltOutlined",
      "ShopOutlined",
      "ShoppingCartOutlined",
      "ShoppingOutlined",
      "SisternodeOutlined",
      "SkinOutlined",
      "SmileOutlined",
      "SolutionOutlined",
      "SoundOutlined",
      "SplitCellsOutlined",
      "StarOutlined",
      "SubnodeOutlined",
      "SwitcherOutlined",
      "SyncOutlined",
      "TableOutlined",
      "TabletOutlined",
      "TagOutlined",
      "TagsOutlined",
      "TeamOutlined",
      "ThunderboltOutlined",
      "ToTopOutlined",
      "ToolOutlined",
      "TrademarkCircleOutlined",
      "TrademarkOutlined",
      "TransactionOutlined",
      "TranslationOutlined",
      "TrophyOutlined",
      "UngroupOutlined",
      "UnlockOutlined",
      "UploadOutlined",
      "UsbOutlined",
      "UserAddOutlined",
      "UserDeleteOutlined",
      "UserOutlined",
      "UserSwitchOutlined",
      "UsergroupAddOutlined",
      "UsergroupDeleteOutlined",
      "VerifiedOutlined",
      "VideoCameraAddOutlined",
      "VideoCameraOutlined",
      "WalletOutlined",
      "WhatsAppOutlined",
      "WifiOutlined",
      "WomanOutlined",
    ]
    // iconList.value = [
    //   "account-book-outlined",
    //   "aim-outlined",
    //   "alert-outlined",
    //   "apartment-outlined",
    //   "api-outlined",
    //   "appstore-add-outlined",
    //   "appstore-outlined",
    //   "audio-outlined",
    //   "audio-muted-outlined",
    //   "audit-outlined",
    //   "bank-outlined",
    //   "barcode-outlined",
    //   "bars-outlined",
    //   "bell-outlined",
    //   "block-outlined",
    //   "book-outlined",
    //   "border-outlined",
    //   "borderless-table-outlined",
    //   "branches-outlined",
    //   "bug-outlined",
    //   "build-outlined",
    //   "bulb-outlined",
    //   "calculator-outlined",
    //   "calendar-outlined",
    //   "camera-outlined",
    //   "car-outlined",
    //   "carry-out-outlined",
    //   "ci-circle-outlined",
    //   "ci-outlined",
    //   "clear-outlined",
    //   "cloud-download-outlined",
    //   "cloud-outlined",
    //   "cloud-server-outlined",
    //   "cloud-sync-outlined",
    //   "cloud-upload-outlined",
    //   "cluster-outlined",
    //   "code-outlined",
    //   "coffee-outlined",
    //   "comment-outlined",
    //   "compass-outlined",
    //   "compress-outlined",
    //   "console-sql-outlined",
    //   "contacts-outlined",
    //   "container-outlined",
    //   "control-outlined",
    //   "copyright-circle-outlined",
    //   "copyright-outlined",
    //   "credit-card-outlined",
    //   "crown-outlined",
    //   "customer-service-outlined",
    //   "dashboard-outlined",
    //   "database-outlined",
    //   "delete-column-outlined",
    //   "delete-row-outlined",
    //   "delivered-procedure-outlined",
    //   "deployment-unit-outlined",
    //   "desktop-outlined",
    //   "dingtalk-outlined",
    //   "disconnect-outlined",
    //   "dislike-outlined",
    //   "dollar-circle-outlined",
    //   "dollar-outlined",
    //   "download-outlined",
    //   "ellipsis-outlined",
    //   "environment-outlined",
    //   "euro-circle-outlined",
    //   "euro-outlined",
    //   "exception-outlined",
    //   "expand-alt-outlined",
    //   "expand-outlined",
    //   "experiment-outlined",
    //   "export-outlined",
    //   "eye-outlined",
    //   "eye-invisible-outlined",
    //   "field-binary-outlined",
    //   "field-number-outlined",
    //   "field-string-outlined",
    //   "field-time-outlined",
    //   "file-add-outlined",
    //   "file-done-outlined",
    //   "file-excel-outlined",
    //   "file-exclamation-outlined",
    //   "file-outlined",
    //   "file-gif-outlined",
    //   "file-image-outlined",
    //   "file-jpg-outlined",
    //   "file-markdown-outlined",
    //   "file-pdf-outlined",
    //   "file-ppt-outlined",
    //   "file-protect-outlined",
    //   "file-search-outlined",
    //   "file-sync-outlined",
    //   "file-text-outlined",
    //   "file-unknown-outlined",
    //   "file-word-outlined",
    //   "file-zip-outlined",
    //   "filter-outlined",
    //   "fire-outlined",
    //   "flag-outlined",
    //   "folder-add-outlined",
    //   "folder-outlined",
    //   "folder-open-outlined",
    //   "folder-view-outlined",
    //   "fork-outlined",
    //   "format-painter-outlined",
    //   "frown-outlined",
    //   "function-outlined",
    //   "fund-projection-screen-outlined",
    //   "fund-view-outlined",
    //   "funnel-plot-outlined",
    //   "gateway-outlined",
    //   "gif-outlined",
    //   "gift-outlined",
    //   "global-outlined",
    //   "gold-outlined",
    //   "group-outlined",
    //   "hdd-outlined",
    //   "heart-outlined",
    //   "history-outlined",
    //   "holder-outlined",
    //   "home-outlined",
    //   "hourglass-outlined",
    //   "idcard-outlined",
    //   "import-outlined",
    //   "inbox-outlined",
    //   "insert-row-above-outlined",
    //   "insert-row-below-outlined",
    //   "insert-row-left-outlined",
    //   "insert-row-right-outlined",
    //   "insurance-outlined",
    //   "interaction-outlined",
    //   "key-outlined",
    //   "laptop-outlined",
    //   "layout-outlined",
    //   "like-outlined",
    //   "line-outlined",
    //   "link-outlined",
    //   "loading3-quarters-outlined",
    //   "loading-outlined",
    //   "lock-outlined",
    //   "mac-command-outlined",
    //   "mail-outlined",
    //   "man-outlined",
    //   "medicine-box-outlined",
    //   "meh-outlined",
    //   "menu-outlined",
    //   "merge-cells-outlined",
    //   "message-outlined",
    //   "mobile-outlined",
    //   "money-collect-outlined",
    //   "monitor-outlined",
    //   "more-outlined",
    //   "node-collapse-outlined",
    //   "node-expand-outlined",
    //   "node-index-outlined",
    //   "notification-outlined",
    //   "number-outlined",
    //   "one-to-one-outlined",
    //   "paper-clip-outlined",
    //   "partition-outlined",
    //   "pay-circle-outlined",
    //   "percentage-outlined",
    //   "phone-outlined",
    //   "picture-outlined",
    //   "play-square-outlined",
    //   "pound-circle-outlined",
    //   "pound-outlined",
    //   "poweroff-outlined",
    //   "printer-outlined",
    //   "profile-outlined",
    //   "project-outlined",
    //   "property-safety-outlined",
    //   "pull-request-outlined",
    //   "pushpin-outlined",
    //   "qrcode-outlined",
    //   "read-outlined",
    //   "reconciliation-outlined",
    //   "red-envelope-outlined ",
    //   "reload-outlined",
    //   "rest-outlined",
    //   "robot-outlined",
    //   "rocket-outlined",
    //   "rotate-left-outlined",
    //   "rotate-right-outlined",
    //   "safety-certificate-outlined",
    //   "safety-outlined",
    //   "save-outlined",
    //   "scan-outlined",
    //   "schedule-outlined",
    //   "search-outlined",
    //   "security-scan-outlined",
    //   "select-outlined",
    //   "send-outlined",
    //   "setting-outlined",
    //   "shake-outlined",
    //   "share-alt-outlined",
    //   "shop-outlined",
    //   "shopping-cart-outlined",
    //   "shopping-outlined",
    //   "sisternode-outlined",
    //   "skin-outlined",
    //   "smile-outlined",
    //   "solution-outlined",
    //   "sound-outlined",
    //   "split-cells-outlined",
    //   "star-outlined",
    //   "subnode-outlined",
    //   "switcher-outlined",
    //   "sync-outlined",
    //   "table-outlined",
    //   "tablet-outlined",
    //   "tag-outlined",
    //   "tags-outlined",
    //   "team-outlined",
    //   "thunderbolt-outlined",
    //   "to-top-outlined",
    //   "tool-outlined",
    //   "trademark-circle-outlined",
    //   "trademark-outlined",
    //   "transaction-outlined",
    //   "translation-outlined",
    //   "trophy-outlined",
    //   "ungroup-outlined",
    //   "unlock-outlined",
    //   "upload-outlined",
    //   "usb-outlined",
    //   "user-add-outlined",
    //   "user-delete-outlined",
    //   "user-outlined",
    //   "user-switch-outlined",
    //   "usergroup-add-outlined",
    //   "usergroup-delete-outlined",
    //   "verified-outlined",
    //   "video-camera-add-outlined",
    //   "video-camera-outlined",
    //   "wallet-outlined",
    //   "whats-app-outlined",
    //   "wifi-outlined",
    //   "woman-outlined",
    // ];

    // 自动请求并暴露内部方法
    onMounted(() => {
      if (model != null && model != undefined) {
        if (model.id == "" || model.id == undefined) {
          if (model.parent_type == "0") {
            model.type = "0";
          } else if (model.parent_type == "1") {
            model.type = "2";
            model.path = "/";
          }
        }
        formRef.id = model?.id;
        formRef.name = model?.name;
        formRef.short_name = model?.short_name;
        formRef.code = model?.code;
        formRef.order_by = model?.order_by;
        formRef.remark = model?.remark;
        formRef.type = model?.type;
        formRef.path = model?.path;
        formRef.redirect = model?.redirect;
        formRef.component = model?.component;
        formRef.permission = model?.permission;
        formRef.icon = model?.icon;
        formRef.keepAlive = model?.keepAlive;
        formRef.parent_id = model?.parent_id;
        formRef.parent_name = model?.parent_name;
        formRef.menu_cascade = model?.menu_cascade;
        formRef.level_num = model?.level_num;
        formRef.parent_type = model?.parent_type;
        title.value = "编辑数据";
      }
    });

    const rulesRef = reactive({
      id: [{ required: false }],
      name: [
        { required: true, max: 50, message: "请输入字典名称且不多余50个字符" },
      ],
      short_name: [{ max: 50, message: "字典简称不能超过50个字符" }],
      code: [{ max: 50, message: "字典简称不能超过50个字符" }],
      order_by: [{ max: 50, message: "不能超过50个字符" }],
      remark: [
        {
          min: 0,
          max: 500,
          message: "长度不得大于500个字符",
        },
      ],
      type: [{ required: true, message: "请选择菜单类型" }],
      path: [{ required: false }],
      redirect: [{ required: false }],
      component: [{ required: false }],
      permission: [{ required: false }],
      icon: [{ required: false }],
      keepAlive: [{ required: false }],
      parent_id: [{ required: false }],
      parent_name: [{ required: false }],
      menu_cascade: [{ required: false }],
      level_num: [{ required: false }],
      parent_type: [{ required: false }],
    });
    const { validate, validateInfos } = useForm(formRef, rulesRef);

    return {
      formLayout,
      formRef,
      validate,
      validateInfos,
      title,
      iconList,
    };
  },
});
</script>

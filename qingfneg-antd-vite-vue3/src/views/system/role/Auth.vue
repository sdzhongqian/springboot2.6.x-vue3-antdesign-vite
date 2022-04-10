<template>
  <a-modal
    :title="title"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
  >
    <a-tree
      v-model:expandedKeys="expandedKeys"
      v-model:selectedKeys="selectedKeys"
      v-model:checkedKeys="checkedKeys"
      checkable
      :tree-data="treeData"
    >
    </a-tree>
  </a-modal>
</template>
<script lang="ts">
import { defineComponent, ref, watch, onMounted } from "vue";
import type { TreeProps } from "ant-design-vue";

import { findMenuList } from "@/api/system/menu";
import { findRoleMenuList } from "@/api/system/role";

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
    const title = ref("权限分配");

    const expandedKeys = ref<string[]>([]);
    const selectedKeys = ref<string[]>([]);
    const checkedKeys = ref<string[]>([]);

    // 自动请求并暴露内部方法
    onMounted(() => {
      getTreeList({});
      getRoleMenuList({role_id: model?.id});
    });

    const treeData: TreeProps["treeData"] = ref([]);
    watch(expandedKeys, () => {
    //   console.log("expandedKeys", expandedKeys);
    });
    watch(selectedKeys, () => {
    //   console.log("selectedKeys", selectedKeys);
    });
    watch(checkedKeys, () => {
    //   console.log("checkedKeys", checkedKeys);
    });

    const getRoleMenuList = (params) => {
      findRoleMenuList(params).then((response) => {
        checkedKeys.value = response.data.data.map((item) => {
          if (item.child_num == "0") {
            return item.menu_id;
          }
        });
      });
    };

    const getTreeList = (params) => {
      findMenuList(params).then((response) => {
        treeData.value = fommat({
          arrayList: response.data.data,
          pidStr: "parent_id",
        });
        expandedKeys.value.push(treeData.value[0].id);
        treeData.value[0].children.forEach((item) => {
          expandedKeys.value.push(item.id);
        });
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
        menu_cascade: "menu_1_",
        level_num: "0",
        type: "0",
      });
      let listOjb = {}; // 用来储存{key: obj}格式的对象
      let treeList = []; // 用来储存最终树形结构数据的数组
      // 将数据变换成{key: obj}格式，方便下面处理数据
      for (let i = 0; i < arrayList.length; i++) {
        var data = arrayList[i];
        data.key = data.id;
        data.value = data.menu_cascade + ":" + data.level_num + ":" + data.type;
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

    return {
      expandedKeys,
      selectedKeys,
      checkedKeys,
      treeData,
      title,
    };
  },
});
</script>


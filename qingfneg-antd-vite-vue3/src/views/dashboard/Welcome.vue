<template>
  <page-header-wrapper>
    <template #content>
      <div class="page-header-content">
        <div class="avatar">
          <a-avatar size="large" :src="currentUser.avatar" />
        </div>
        <div class="content">
          <div class="content-title">
            {{ tf }}，{{ user.name }}
          </div>
          <div>前端工程师 | 蚂蚁金服 - 某某某事业群 - VUE平台</div>
        </div>
      </div>
    </template>
    <template #extraContent>
      <div class="extra-content">
        <div class="stat-item">
          <a-statistic title="项目数" :value="56" />
        </div>
        <div class="stat-item">
          <a-statistic title="团队内排名" :value="8" suffix="/ 24" />
        </div>
        <div class="stat-item">
          <a-statistic title="项目访问" :value="2223" />
        </div>
      </div>
    </template>
    <div :style="{ minHeight: '400px' }">

    </div>
  </page-header-wrapper>
</template>

<script lang="ts">
import {defineComponent, computed, reactive, ref, onMounted} from 'vue'
import DataSet from '@antv/data-set'
import store from '@/store';
import { timeFix } from '@/utils/util'

import { getRoleList, getServiceList } from '@/api/manage'
import {requestGet} from '@/api/service';

interface Project {
  id: Number,
  cover: String,
  title: String,
  description: String,
  status: Number,
  updatedAt: String
}
interface Activity {
  id: Number,
  user: {
    nickname: String,
    avatar: String
},
  project: {
    name: String,
    action: String,
    event: String
  },
  time: String
}

interface team {
  id: Number,
  name: String,
  avatar: String
}

export default defineComponent({
  name: 'Workplace',
  components: {
  },
  setup(){
  const tf = timeFix()
  const userInfo = computed(() => {
    return store.getters.userInfo
  })

  const avatar = store.state.user.info.avatar
  const user = store.state.user.info

  const projects = reactive<Project[]>([])
  const loading = ref(true)
  const activities = reactive<Activity[]>([])
  const teams = ref(<team[]>[])

  const nickname = computed(() => store.state.user.nickname)

  const currentUser = () => {
    return {
      name: nickname,
      avatar: '/avatar.jpg'
    }
  }


  onMounted(
    () =>{
    },
  )

  return{
    tf,
    avatar,
    user,
    projects,
    loading,
    activities,
    nickname,
    currentUser,
    userInfo
  }
}
})


</script>

<style lang="less" scoped>
@import './Welcome.less';

.project-list {
  .card-title {
    font-size: 0;

    a {
      color: rgba(0, 0, 0, 0.85);
      margin-left: 12px;
      line-height: 24px;
      height: 24px;
      display: inline-block;
      vertical-align: top;
      font-size: 14px;

      &:hover {
        color: #1890ff;
      }
    }
  }

  .card-description {
    color: rgba(0, 0, 0, 0.45);
    height: 44px;
    line-height: 22px;
    overflow: hidden;
  }

  .project-item {
    display: flex;
    margin-top: 8px;
    overflow: hidden;
    font-size: 12px;
    height: 20px;
    line-height: 20px;

    a {
      color: rgba(0, 0, 0, 0.45);
      display: inline-block;
      flex: 1 1 0;

      &:hover {
        color: #1890ff;
      }
    }

    .datetime {
      color: rgba(0, 0, 0, 0.25);
      flex: 0 0 auto;
      float: right;
    }
  }

  .ant-card-meta-description {
    color: rgba(0, 0, 0, 0.45);
    height: 44px;
    line-height: 22px;
    overflow: hidden;
  }
}

.item-group {
  padding: 20px 0 8px 24px;
  font-size: 0;

  a {
    color: rgba(0, 0, 0, 0.65);
    display: inline-block;
    font-size: 14px;
    margin-bottom: 13px;
    width: 25%;
  }
}

.members {
  a {
    display: block;
    margin: 12px 0;
    line-height: 24px;
    height: 24px;

    .member {
      font-size: 14px;
      color: rgba(0, 0, 0, 0.65);
      line-height: 24px;
      max-width: 100px;
      vertical-align: top;
      margin-left: 12px;
      transition: all 0.3s;
      display: inline-block;
    }

    &:hover {
      span {
        color: #1890ff;
      }
    }
  }
}

.mobile {
  .project-list {
    .project-card-grid {
      width: 100%;
    }
  }

  .more-info {
    border: 0;
    padding-top: 16px;
    margin: 16px 0 16px;
  }

  .headerContent .title .welcome-text {
    display: none;
  }
}
</style>

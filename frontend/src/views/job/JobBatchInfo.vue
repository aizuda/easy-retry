<template>
  <div>
    <page-header-wrapper @back="() => $router.replace('/job/batch/list')" style="margin: -24px -1px 0"v-if="showHeader">
      <div></div>
    </page-header-wrapper>
    <a-card :bordered="false" v-if="jobBatchInfo !==null ">
      <a-descriptions title="" :column="column" bordered>
        <a-descriptions-item label="组名称">
          {{ jobBatchInfo.groupName }}
        </a-descriptions-item>
        <a-descriptions-item label="任务名称">
          {{ jobBatchInfo.jobName }}
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="taskBatchStatus[jobBatchInfo.taskBatchStatus].color">
            {{ taskBatchStatus[jobBatchInfo.taskBatchStatus].name }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="执行器类型">
          <a-tag :color="executorType[jobBatchInfo.executorType].color">
            {{ executorType[jobBatchInfo.executorType].name }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="操作原因">
          <a-tag :color="operationReason[jobBatchInfo.operationReason].color">
            {{ operationReason[jobBatchInfo.operationReason].name }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="开始执行时间">
          {{ jobBatchInfo.executionAt }}
        </a-descriptions-item>
        <a-descriptions-item label="执行器名称" span="4">
          {{ jobBatchInfo.executorInfo }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ jobBatchInfo.createDt }}
        </a-descriptions-item>
      </a-descriptions>
    </a-card>
    <div style="margin: 20px 0; border-left: #f5222d 5px solid; font-size: medium; font-weight: bold">
      <span style="padding-left: 18px">任务项列表</span>
      <span style="padding-left: 18px"><a-icon type="sync" @click="()=> this.$refs.JobTaskListRef.refreshTable(this.queryParam)"/></span>
    </div>
    <JobTaskList ref="JobTaskListRef" />
  </div>
</template>

<script>
import { jobBatchDetail } from '@/api/jobApi'
import moment from 'moment'
import enums from '@/utils/jobEnum'
import JobTaskList from './JobTaskList'

export default {
  name: 'JobInfo',
  components: {
    JobTaskList

  },
  props: {
    showHeader: {
      type: Boolean,
      default: true
    },
    column: {
      type: Number,
      default: 3
    }
  },
  data () {
    return {
      jobBatchInfo: null,
      taskBatchStatus: enums.taskBatchStatus,
      operationReason: enums.operationReason,
      taskType: enums.taskType,
      triggerType: enums.triggerType,
      blockStrategy: enums.blockStrategy,
      executorType: enums.executorType,
      queryParam: {}
    }
  },
  created () {
    const id = this.$route.query.id
    const groupName = this.$route.query.groupName
    if (id && groupName) {
      this.jobBatchDetail(id)
    } else {
      if (this.showHeader) {
        this.$router.push({ path: '/404' })
      }
    }
  },
  methods: {
    parseDate (date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss')
    },
    jobBatchDetail (id) {
      jobBatchDetail(id).then(res => {
        this.jobBatchInfo = res.data
        this.queryParam = {
          groupName: this.jobBatchInfo.groupName,
          taskBatchId: id,
          startId: 0,
          fromIndex: 0
        }
        this.$refs.JobTaskListRef.refreshTable(this.queryParam)
      })
    }
  }
}
</script>

<style scoped>

</style>

<template>
  <div class="antd-pro-pages-dashboard-analysis-twoColLayout" :class="!isMobile && 'desktop'">
    <a-row>
      <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
        <g2-retry-line ref="viewChart" name="RetryLine" />
      </a-col>
      <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
        <rank-list :title="$t('dashboard.analysis.sales-ranking')" :list="rankList" />
      </a-col>
    </a-row>
    <a-row :gutter="24" type="flex" :style="{ marginTop: '24px' }">
      <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
        <a-card :loading="loading" :bordered="false" title="场景列表" :style="{ height: '100%' }">
          <s-table
            ref="table"
            size="default"
            :rowKey="(record,index) => index"
            :columns="columns"
            :data="loadData"
            :scroll="{ x: 200 }"
          >
          </s-table>
        </a-card>
      </a-col>
      <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
        <a-card class="antd-pro-pages-dashboard-analysis-salesCard" :loading="loading" :bordered="false" :title="$t('dashboard.analysis.the-proportion-of-sales')" :style="{ height: '100%' }">
          <h4>{{ $t('dashboard.analysis.sales') }}</h4>
          <div>
            <div>
              <v-chart :force-fit="true" :height="405" :data="pieData" :scale="pieScale" >
                <v-tooltip :showTitle="false" dataKey="value*percent" />
                <v-axis />
                <v-legend dataKey="value" />
                <v-pie position="percent" color="value" :vStyle="pieStyle" />
                <v-coord type="theta" :radius="0.95" :innerRadius="0.7" />
              </v-chart>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import {
  RankList,
  STable
} from '@/components'
import { getAllGroupNameList, getDashboardRetryLine } from '@/api/manage'
import { baseMixin } from '@/store/app-mixin'
import G2RetryLine from '@/components/Charts/RetryLine.vue'
import moment from 'moment'

export default {
  name: 'RetryAnalysis',
  mixins: [ baseMixin ],
  components: {
    G2RetryLine,
    RankList,
    STable
  },
  data () {
    return {
      loading: true,
      rankList: [],
      taskList: [],
      type: 'WEEK',
      groupName: '',
      startTime: [],
      endTime: [],
      groupNameList: [],
      pieScale: [{
        dataKey: 'percent',
        min: 0,
        formatter: '.0%'
      }],
      pieData: [],
      pieStyle: {
        stroke: '#fff',
        lineWidth: 1
      },
      columns: [
        {
          title: '组名称',
          dataIndex: 'groupName'
        },
        {
          title: '启动中',
          dataIndex: 'run'
        },
        {
          title: '总场景',
          dataIndex: 'total'
        }
      ],
      loadData: (parameter) => {
        return getDashboardRetryLine(Object.assign(parameter)).then((res) => {
          this.rankList = res.data.rankList
          return res.data.taskList
        })
      }
    }
  },
  mounted () {
    this.$bus.$on('retry', (res) => {
      this.total = 0
      this.successNum = 0
      this.runningNum = 0
      this.maxCountNum = 0
      this.suspendNum = 0
      this.rankList = res.data.rankList
      this.taskList = res.data.taskList
      res.data.dashboardLineResponseDOList.forEach(res => {
        this.successNum += res.successNum
        this.runningNum += res.runningNum
        this.maxCountNum += res.maxCountNum
        this.suspendNum += res.suspendNum
      })
      this.total = this.successNum + this.runningNum + this.maxCountNum + this.suspendNum
      this.pieData = [
        { value: 'SUCCESS', name: this.successNum, percent: this.successNum / this.total },
        { value: 'RUNNING', name: this.runningNum, percent: this.runningNum / this.total },
        { value: 'MAXCOUNT', name: this.maxCountNum, percent: this.maxCountNum / this.total },
        { value: 'SUSPEND', name: this.suspendNum, percent: this.suspendNum / this.total }
      ]
    })
  },
  methods: {
    moment,
    dataHandler (type) {
      this.type = type
      this.$refs.viewChart.getDashboardRetryLine(this.groupName, this.type, this.startTime, this.endTime)
    },
    handleChange (value) {
      this.groupName = value
      this.$refs.viewChart.getDashboardRetryLine(this.groupName, this.type, this.startTime, this.endTime)
    },
    dateChange (date, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
      this.type = this.startTime === '' ? 'WEEK' : 'OTHERS'
      this.$refs.viewChart.getDashboardRetryLine(this.groupName, this.type, this.startTime, this.endTime)
    }
  },
  created () {
    getAllGroupNameList().then(res => {
      this.groupNameList = res.data
    })

    setTimeout(() => {
      this.loading = !this.loading
    }, 1000)
  }
}
</script>

<style lang='less' scoped>
@import 'Analysis.less';
</style>

<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="组名称">
              <a-select
                v-model="queryParam.groupName"
                placeholder="请输入组名称"
                @change="(value) => handleChange(value)"
              >
                <a-select-option v-for="item in groupNameList" :value="item" :key="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="任务名称">
              <a-input v-model="queryParam.workflowName" placeholder="请输入任务名称" allowClear />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="状态">
              <a-select v-model="queryParam.workflowStatus" placeholder="请选择状态" allowClear>
                <a-select-option v-for="(index, value) in workflowStatus" :value="value" :key="value">
                  {{ index.name }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <template v-if="advanced">
          </template>
          <a-col :md="(!advanced && 8) || 24" :sm="24">
            <span
              class="table-page-search-submitButtons"
              :style="(advanced && { float: 'right', overflow: 'hidden' }) || {}"
            >
              <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => (queryParam = {})">重置</a-button>
              <a @click="toggleAdvanced" style="margin-left: 8px">
                {{ advanced ? '收起' : '展开' }}
                <a-icon :type="advanced ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleNew()">新增</a-button>
      <!--      <a-button type="primary" icon="plus" @click="handleBatchNew()">批量</a-button>-->
      <a-dropdown v-action:edit v-if="selectedRowKeys.length > 0">
        <!--          <a-menu-item key="1"><a-icon type="delete" />删除</a-menu-item>-->
        <!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /> </a-button>-->
      </a-dropdown>
    </div>

    <s-table
      ref="table"
      size="default"
      :rowKey="(record) => record.id"
      :columns="columns"
      :data="loadData"
      :scroll="{ x: 1600 }"
    >
      <span slot="serial" slot-scope="text, record">
        {{ record.id }}
      </span>
      <span slot="workflowName" slot-scope="text">
        {{ text }}
      </span>
      <span slot="workflowStatus" slot-scope="text">
        <a-tag :color="workflowStatus[text].color">
          {{ workflowStatus[text].name }}
        </a-tag>
      </span>
      <span slot="triggerType" slot-scope="text">
        <a-tag :color="triggerType[text].color">
          {{ triggerType[text].name }}
        </a-tag>
      </span>
      <span slot="triggerInterval" slot-scope="text">
        <span>{{ text }}</span>
      </span>
      <span slot="executorTimeout" slot-scope="text">
        <span>{{ text }}(秒)</span>
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a-popconfirm
            title="是否运行?"
            ok-text="运行"
            cancel-text="取消"
            @confirm="handleTrigger(record)"
          >
            <a href="javascript:;">运行</a>
          </a-popconfirm>
          <a-divider type="vertical" />
          <a @click="handleInfo(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="goJobBatchList(record)">批次</a>
          <a-divider type="vertical" />
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm
            title="是否关闭?"
            ok-text="关闭"
            cancel-text="取消"
            @confirm="handleOpenOrClose(record)"
          >
            <a href="javascript:;" v-if="record.workflowStatus === 1">关闭</a>
          </a-popconfirm>
          <a-divider type="vertical" v-if="record.workflowStatus === 1" />
          <a-popconfirm
            title="是否开启?"
            ok-text="开启"
            cancel-text="取消"
            @confirm="handleOpenOrClose(record)"
          >
            <a href="javascript:;" v-if="record.workflowStatus === 0">开启</a>
          </a-popconfirm>
          <a-divider type="vertical" v-if="record.workflowStatus === 0 && $auth('job.del')" />
          <a-popconfirm
            title="是否删除工作流?"
            ok-text="删除"
            cancel-text="取消"
            @confirm="handleDel(record)"
            v-if="$auth('job.del')"
          >
            <a href="javascript:;" v-if="record.workflowStatus === 0">删除</a>
          </a-popconfirm>
          <a-divider type="vertical" v-if="record.workflowStatus === 0"/>
          <a-popconfirm
            title="是否复制此工作流?"
            ok-text="复制"
            cancel-text="取消"
            @confirm="handleCopy(record)"
          >
            <a href="javascript:;">复制</a>
          </a-popconfirm>

        </template>
      </span>
    </s-table>

  </a-card>
</template>

<script>
import ATextarea from 'ant-design-vue/es/input/TextArea'
import AInput from 'ant-design-vue/es/input/Input'
import { STable, Drawer } from '@/components'
import { workflowListPage, updateWorkflowStatus, delWorkflow, triggerWorkflow } from '@/api/jobApi'
import { getAllGroupNameList } from '@/api/manage'
import enums from '@/utils/jobEnum'
import JobInfo from '@/views/job/JobInfo'

export default {
  name: 'WorkflowList',
  components: {
    AInput,
    ATextarea,
    STable,
    JobInfo,
    Drawer
  },
  data () {
    return {
      currentComponet: 'List',
      record: '',
      mdl: {},
      visible: false,
      // 高级搜索 展开/关闭
      advanced: false,
      // 查询参数
      queryParam: {},
      workflowStatus: enums.workflowStatus,
      triggerType: enums.triggerType,
      blockStrategy: enums.blockStrategy,
      executorType: enums.executorType,
      // 表头
      columns: [
        {
          title: 'ID',
          scopedSlots: { customRender: 'serial' },
          fixed: 'left'
        },
        {
          title: '工作流名称',
          dataIndex: 'workflowName',
          scopedSlots: { customRender: 'workflowName' }
        },
        {
          title: '组名称',
          dataIndex: 'groupName',
          width: '15%'
        },
        {
          title: '触发时间',
          dataIndex: 'nextTriggerAt',
          ellipsis: true,
          width: '10%'
        },
        {
          title: '状态',
          dataIndex: 'workflowStatus',
          scopedSlots: { customRender: 'workflowStatus' }
        },
        {
          title: '触发类型',
          dataIndex: 'triggerType',
          scopedSlots: { customRender: 'triggerType' }
        },
        {
          title: '间隔时长',
          dataIndex: 'triggerInterval',
          scopedSlots: { customRender: 'triggerInterval' },
          ellipsis: true
        },
        {
          title: '超时时间',
          dataIndex: 'executorTimeout',
          scopedSlots: { customRender: 'executorTimeout' }
        },
        {
          title: '更新时间',
          dataIndex: 'updateDt',
          sorter: true
        },
        {
          title: '操作',
          fixed: 'right',
          dataIndex: 'action',
          width: '180px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      // 加载数据方法 必须为 Promise 对象
      loadData: (parameter) => {
        return workflowListPage(Object.assign(parameter, this.queryParam)).then((res) => {
          return res
        })
      },
      selectedRowKeys: [],
      selectedRows: [],

      // custom table alert & rowSelection
      options: {
        alert: {
          show: true,
          clear: () => {
            this.selectedRowKeys = []
          }
        },
        rowSelection: {
          selectedRowKeys: this.selectedRowKeys,
          onChange: this.onSelectChange
        }
      },
      optionAlertShow: false,
      groupNameList: [],
      sceneList: [],
      openDrawer: false,
      currentShowRecord: null
    }
  },
  created () {
    getAllGroupNameList().then((res) => {
      this.groupNameList = res.data
    })
  },
  methods: {
    handleEdit (record) {
      this.$router.push({ path: '/job/workflow/edit', query: { id: record.id } })
    },
    goJobBatchList (record) {
      this.$router.push({ path: '/job/workflow/batch/list', query: { workflowId: record.id } })
    },
    handleNew () {
      this.$router.push({ path: '/job/workflow/edit' })
    },
    handleChange (value) {
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    handleInfo (record) {
      record = record || this.currentShowRecord
      this.$router.push({ path: '/job/workflow/detail', query: { id: record.id } })
    },
    handleOk (record) {},
    handleTrigger (record) {
      triggerWorkflow(record.id).then((res) => {
        const { status } = res
        if (status === 0) {
          this.$message.error('执行失败')
        } else {
          // this.$refs.table.refresh(true)
          this.$message.success('执行成功')
        }
      })
    },
    handleOpenOrClose (record) {
      updateWorkflowStatus(record.id).then((res) => {
        const { status } = res
        if (status === 0) {
          this.$message.error('执行失败')
        } else {
          this.$refs.table.refresh(true)
          this.$message.success('执行成功')
        }
      })
    },
    handleCopy (record) {
      this.$router.push({ path: '/job/workflow/copy', query: { id: record.id } })
    },
    handleDel (record) {
      delWorkflow(record.id).then((res) => {
        const { status } = res
        if (status === 0) {
          this.$message.error('删除失败')
        } else {
          this.$refs.table.refresh(true)
          this.$message.success('删除成功')
        }
      })
    },
    refreshTable (v) {
      this.$refs.table.refresh(true)
    },
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    handlerOpenDrawer (record) {
      this.currentShowRecord = record
      this.openDrawer = true
      setTimeout(() => {
        this.$refs.jobInfoRef.jobDetail(record.id)
      }, 200)
    },
    onClose () {
      this.openDrawer = false
      this.currentShowRecord = null
    }
  }
}
</script>

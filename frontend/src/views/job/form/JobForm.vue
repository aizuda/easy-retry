<template>
  <div>
    <page-header-wrapper content="定时任务配置" @back="() => $router.go(-1)" style="margin: -24px -1px 0">
      <div></div>
    </page-header-wrapper>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false" :loading="loading">
      <a-form @submit="handleSubmit" :form="form" class="form-row" layout="vertical" style="width: 40%;margin: auto;">
        <a-row class="form-row" :gutter="16">
          <a-col :lg="24" :md="24" :sm="24">
            <a-form-item>
              <a-input
                hidden
                v-decorator="['id']" />
            </a-form-item>
            <a-form-item label="任务名称" >
              <a-input
                placeholder="请输入任务名称"
                :maxLength="64"
                v-decorator="[
                  'jobName',
                  {rules: [{ required: true, message: '请输入任务名称', whitespace: true},{required: true, max: 64, message: '最多支持64个字符！'}]}
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="18" :md="18" :sm="24">
            <a-form-item label="组">
              <a-select
                placeholder="请选择组"
                v-decorator="['groupName', { rules: [{ required: true, message: '请选择组' }] }]"
              >
                <a-select-option v-for="item in groupNameList" :value="item" :key="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="状态">
              <a-select
                placeholder="请选择状态"
                v-decorator="[
                  'jobStatus',
                  {
                    initialValue: '1',
                    rules: [{ required: true, message: '请选择状态'}]
                  }
                ]" >
                <a-select-option :value="index" v-for="(item, index) in jobStatusEnum" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="12" :md="12" :sm="24">
            <a-form-item label="路由策略">
              <a-select
                placeholder="请选择路由策略"
                v-decorator="[
                  'routeKey',
                  {
                    initialValue: '4',
                    rules: [{ required: true, message: '请选择路由策略'}]
                  }
                ]" >
                <a-select-option :value="index" v-for="(item, index) in routeKey" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="12" :md="12" :sm="24">
            <a-form-item label="阻塞策略">
              <a-select
                placeholder="请选择阻塞策略"
                v-decorator="[
                  'blockStrategy',
                  {
                    initialValue: '1',
                    rules: [{ required: true, message: '请选择阻塞策略'}]
                  }
                ]" >
                <a-select-option :value="index" v-for="(item, index) in blockStrategy" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="8" :md="12" :sm="12">
            <a-form-item label="触发类型">
              <a-select
                placeholder="请选择触发类型"
                @change="handleChange"
                v-decorator="[
                  'triggerType',
                  {
                    initialValue: '2',
                    rules: [{ required: true, message: '请选择触发类型'}]
                  }
                ]" >
                <a-select-option :value="index" v-for="(item, index) in triggerType" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="16" :md="12" :sm="12">
            <a-form-item label="间隔时长">
              <a-input-number
                v-if="triggerTypeValue === '2'"
                style="width: -webkit-fill-available"
                placeholder="请输入间隔时长(秒)"
                :min="1"
                v-decorator="[
                  'triggerInterval',
                  {initialValue: '60',
                   rules: [ { required: true, message: '请输入间隔时长'}]}
                ]" />

              <a-input
                v-if="triggerTypeValue === '3'"
                @click="handlerCron"
                placeholder="请输入间隔时长"
                v-decorator="[
                  'triggerInterval',
                  {rules: [{ required: true, message: '请输入间隔时长', whitespace: true}]}
                ]" />

              <a-input
                v-if="triggerTypeValue === '99'"
                disabled
                placeholder=""
                v-decorator="[
                  'triggerInterval'
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="8" :md="6" :sm="12">
            <a-form-item label="执行器类型">
              <a-select
                placeholder="请选择执行器类型"
                v-decorator="[
                  'executorType',
                  {
                    initialValue: '1',
                    rules: [{ required: true, message: '请选择执行器类型'}]
                  }
                ]" >
                <a-select-option :value="index" v-for="(item, index) in executorType" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="16" :md="24" :sm="24">
            <a-form-item label="执行器名称">
              <a-input
                placeholder="请输入执行器名称"
                type="textarea"
                :rows="1"
                v-decorator="[
                  'executorInfo',
                  {rules: [{ required: true, message: '请输入执行器名称', whitespace: true}]}
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="8" :md="12" :sm="24">
            <a-form-item label="任务类型">
              <a-select
                placeholder="请选择任务类型"
                @change="handleTaskTypeChange"
                v-decorator="[
                  'taskType',
                  {
                    initialValue: taskTypeValue,
                    rules: [{ required: true, message: '请选择任务类型'}]
                  }
                ]" >
                <a-select-option :value="index" v-for="(item, index) in taskType" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :lg="16" :md="24" :sm="24">
            <a-form-item label="方法参数">
              <a-input
                placeholder="请输入方法参数"
                type="textarea"
                :rows="1"
                @click="handleBlur"
                v-decorator="[
                  'argsStr',
                  {rules: [{ required: this.taskTypeValue === '3', message: '请输入方法参数', whitespace: true}]}
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="超时时间(秒)">
              <a-input-number
                id="inputNumber"
                :min="1"
                :max="36000"
                v-decorator="[
                  'executorTimeout',
                  {
                    initialValue: '60',
                    rules: [{ required: true, message: '请输入超时时间'}]
                  }
                ]" />
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="最大重试次数">
              <a-input-number
                :min="0"
                v-decorator="[
                  'maxRetryTimes',
                  {
                    initialValue: '3',
                    rules: [{ required: true, message: '请输入最大重试次数'}]
                  }
                ]" />
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="重试间隔(秒)">
              <a-input-number
                :min="1"
                v-decorator="[
                  'retryInterval',
                  {
                    initialValue: '1',
                    rules: [{ required: true, message: '请输入重试间隔'
                    }]
                  }
                ]" />
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="并行数">
              <a-input-number
                :min="1"
                v-decorator="[
                  'parallelNum',
                  {
                    initialValue: '1',
                    rules: [{ required: true, message: '请输入并行数'}]
                  }
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col :lg="24" :md="24" :sm="24">
            <a-form-item label="描述">
              <a-input
                placeholder="请输入描述"
                type="textarea"
                v-decorator="[
                  'description',
                  {rules: [{required: false, max: 256, message: '最多支持256个字符！'}]}
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item
          :wrapperCol="{ span: 24 }"
          style="text-align: center"
        >
          <a-button htmlType="submit" type="primary">提交</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-modal :visible="visible" title="分片参数" @ok="submitForm()" @cancel="handlerCancel" width="500px">
      <a-form-model ref="dynamicValidateForm" :model="dynamicValidateForm" style="margin-left: 15%" layout="vertical" >
        <a-form-model-item
          v-for="(domain, index) in dynamicValidateForm.domains"
          :key="domain.key"
          :label="'分片' + (index)"
          :prop="'domains.' + index + '.value'"
          :rules="{
            required: true,
            message: '分区参数不能为空',
            trigger: 'blur',
          }"
        >
          <a-input
            v-model="domain.value"
            placeholder="请输入分区的参数"
            style="width: 80%; margin-right: 8px"
          />
          <a-icon
            v-if="dynamicValidateForm.domains.length > 1"
            class="dynamic-delete-button"
            type="minus-circle-o"
            :disabled="dynamicValidateForm.domains.length === 1"
            @click="removeDomain(domain)"
          />
        </a-form-model-item>
        <a-form-model-item v-bind="formItemLayoutWithOutLabel">
          <a-button type="dashed" style="width: 60%" @click="add">
            <a-icon type="plus" /> 添加分片
          </a-button>
        </a-form-model-item>
        <a-form-model-item
          :wrapper-col="{
            xs: { span: 24, offset: 0 },
            sm: { span: 16, offset: 8 },
            lg: { span: 7 }
          }">
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <cron-modal ref="cronModalRef" @getCron="getCron"/>
  </div>
</template>

<script>
import { getAllGroupNameList } from '@/api/manage'
import { getJobDetail, saveJob, updateJob } from '@/api/jobApi'
import pick from 'lodash.pick'
import CronModal from '@/views/job/form/CronModal'

import AFormModel from 'ant-design-vue/es/form-model/Form'
import AFormModelItem from 'ant-design-vue/es/form-model/FormItem'

const enums = require('@/utils/jobEnum')

export default {
  name: 'JobFrom',
  components: {
    CronModal,
    AFormModel,
    AFormModelItem
  },
  props: {},
  data () {
    return {
      form: this.$form.createForm(this),
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 4 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 20 }
        }
      },
      formItemLayoutWithOutLabel: {
        wrapperCol: {
          xs: { span: 24, offset: 0 },
          sm: { span: 20, offset: 4 }
        }
      },
      formType: 'create',
      groupNameList: [],
      jobStatusEnum: enums.jobStatusEnum,
      taskType: enums.taskType,
      triggerType: enums.triggerType,
      blockStrategy: enums.blockStrategy,
      executorType: enums.executorType,
      routeKey: enums.routeKey,
      loading: false,
      visible: false,
      count: 0,
      triggerTypeValue: '2',
      taskTypeValue: '1',
      argsStrValue: [],
      dynamicValidateForm: {
        domains: []
      }
    }
  },
  beforeCreate () {
    // this.dynamicForm = this.$form.createForm(this, { name: 'dynamic_form_item' })
    // this.dynamicForm.getFieldDecorator('keys', { initialValue: [], preserve: true })
  },
  mounted () {
    getAllGroupNameList().then((res) => {
      this.groupNameList = res.data
    })

    this.$nextTick(() => {
      const id = this.$route.query.id
      if (id) {
        this.loading = true
        getJobDetail(id).then(res => {
          this.loadEditInfo(res.data)
          this.loading = false
        })
      }
    })
  },
  methods: {
    handleChange (value) {
      this.triggerTypeValue = value
      this.form.setFieldsValue({
        triggerInterval: null
      })
    },
    handleTaskTypeChange (value) {
      this.taskTypeValue = value
    },
    handlerCron () {
      const triggerType = this.form.getFieldValue('triggerType')
      if (triggerType === '3') {
        let triggerInterval = this.form.getFieldValue('triggerInterval')
        if (triggerInterval === null || triggerInterval === '') {
          triggerInterval = '* * * * * ?'
        }
        this.$refs.cronModalRef.isShow(triggerInterval)
      }
    },
    removeDomain (item) {
      const index = this.dynamicValidateForm.domains.indexOf(item)
      if (index !== -1) {
        this.dynamicValidateForm.domains.splice(index, 1)
      }
    },
    add () {
      this.dynamicValidateForm.domains.push({
        value: '',
        key: Date.now()
      })
    },
    handleBlur () {
      const taskType = this.form.getFieldValue('taskType')
      if (taskType === '3') {
        this.visible = !this.visible
      }
    },
    getCron (cron) {
      this.form.setFieldsValue({
        triggerInterval: cron
      })
    },
    submitForm () {
      const { form } = this
      this.$refs['dynamicValidateForm'].validate(valid => {
        if (valid) {
          this.argsStrValue = this.dynamicValidateForm.domains.map((item, index) => item.value)
          form.setFieldsValue({
            argsStr: this.dynamicValidateForm.domains.map((item, index) => `分区:${index}=>${item.value}`).join('; ')
          })
          this.visible = !this.visible
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleOk (e) {
      const { form } = this
      e.preventDefault()
      this.dynamicForm.validateFields((err, values) => {
        if (!err) {
          this.argsStrValue = values['sharding']
          console.log(this.argsStrValue)
          form.setFieldsValue({
            argsStr: this.argsStrValue.filter(item => item).map((item, index) => `分区:${index}=>${item}`).join('; ')
          })
          this.visible = false
        }
      })
    },
    handlerCancel () {
      this.visible = false
    },
    handleSubmit (e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.taskTypeValue === '3') {
            values['argsStr'] = JSON.stringify(this.argsStrValue)
          }

          if (this.triggerTypeValue === '99') {
            values['triggerInterval'] = '0'
          }

          if (this.formType === 'create') {
            saveJob(values).then(res => {
              this.$message.success('任务新增完成')
              this.form.resetFields()
              this.$router.go(-1)
            })
          } else {
            updateJob(values).then(res => {
              this.$message.success('任务更新完成')
              this.form.resetFields()
              this.$router.go(-1)
            })
          }
        }
      })
    },
    loadEditInfo (data) {
      this.formType = 'edit'
      const { form } = this
      // ajax
      new Promise((resolve) => {
        setTimeout(resolve, 100)
      }).then(() => {
        const formData = pick(data, [
          'id', 'jobName', 'groupName', 'jobStatus', 'executorInfo', 'argsStr', 'executorTimeout', 'description',
          'maxRetryTimes', 'parallelNum', 'retryInterval', 'triggerType', 'blockStrategy', 'executorType', 'taskType', 'triggerInterval'])
        formData.jobStatus = formData.jobStatus.toString()
        formData.taskType = formData.taskType.toString()
        formData.executorType = formData.executorType.toString()
        formData.blockStrategy = formData.blockStrategy.toString()
        formData.triggerType = formData.triggerType.toString()
        this.triggerTypeValue = formData.triggerType
        this.taskTypeValue = formData.taskType

        if (this.triggerTypeValue === '99') {
          formData.triggerInterval = null
        }

        if (this.taskTypeValue === '3') {
          this.argsStrValue = JSON.parse(formData.argsStr)
          formData.argsStr = this.argsStrValue.map((item, index) => `分区:${index}=>${item}`).join(';')

          this.argsStrValue.forEach((item, index) => {
            this.dynamicValidateForm.domains.push({
              value: item,
              key: Date.now() + index
            })
          })
        }

        form.setFieldsValue(formData)
      })
    },
    reset () {
      this.form.resetFields()
      this.dynamicForm.resetFields()
    }

  }
}
</script>

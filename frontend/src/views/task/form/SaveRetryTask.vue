<template>
  <div>
    <a-modal :visible="visible" title="新增任务" @ok="handleOk" @cancel="visible = false" width="800px">
      <a-form @submit="handleOk" :form="form" v-bind="formItemLayout">
        <a-form-item label="组">
          <a-select
            placeholder="请选择组"
            v-decorator="['groupName', { rules: [{ required: true, message: '请选择组' }] }]"
            @change="(value) => handleChange(value)"
          >
            <a-select-option v-for="item in groupNameList" :value="item" :key="item">{{ item }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="场景名称">
          <a-select
            placeholder="请选择场景名称"
            v-decorator="['sceneName', { rules: [{ required: true, message: '请选择场景名称' }] }]" >
            <a-select-option v-for="item in sceneList" :value="item.sceneName" :key="item.sceneName">
              {{ item.sceneName }}</a-select-option
            >
          </a-select>
        </a-form-item>
        <a-form-item label="执行器名称">
          <a-input
            v-decorator="['executorName', { rules: [{ required: true, message: '请输入执行器名称' }] }]"
            name="executorName"
            placeholder="请输入执行器名称"
          />
        </a-form-item>
        <a-form-item label="幂等ID">
          <a-input
            v-decorator="['idempotentId', { rules: [{ required: true, message: '请输入幂等ID' }] }]"
            name="idempotentId"
            placeholder="请输入幂等ID"
          >
            <a-tooltip slot="suffix">
              <a :href="officialWebsite + '/pages/b74542/#幂等id(idempotentId)'" target="_blank">
                <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
              </a>
            </a-tooltip>
          </a-input>
          <a-button type="primary" style="position: absolute; margin: 3px 10px" @click="idempotentIdGenerate"> 通过客户端生成 </a-button>
        </a-form-item>
        <a-form-item label="业务编号">
          <a-input
            v-decorator="['bizNo', { rules: [{ required: false, message: '请输入业务编号' }] }]"
            name="bizNo"
            placeholder="请输入业务编号"
          >
            <a-tooltip slot="suffix">
              <a :href="officialWebsite + '/pages/b74542/#业务编号(bizNo)'" target="_blank">
                <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
              </a>
            </a-tooltip>
          </a-input>
        </a-form-item>
        <a-form-item label="重试状态">
          <a-select
            placeholder="请选择重试状态"
            v-decorator="['retryStatus', { rules: [{ required: true, message: '请选择重试状态' }] }]"
          >
            <a-select-option v-for="(value, key) in retryStatus" :value="key" :key="key"> {{ value }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="参数">
          <a-textarea
            rows="4"
            placeholder="请输入参数"
            v-decorator="['argsStr', { rules: [{ required: true, message: '请输入参数' }] }]"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { getAllGroupNameList, getSceneList, saveRetryTask, idempotentIdGenerate } from '@/api/manage'
import { officialWebsite } from '@/utils/util'

export default {
  name: 'SavRetryTask',
  props: {},
  data () {
    return {
      visible: false,
      form: this.$form.createForm(this),
      formItemLayout: {
        labelCol: { lg: { span: 6 }, sm: { span: 7 } },
        wrapperCol: { lg: { span: 14 }, sm: { span: 17 } }
      },
      groupNameList: [],
      sceneList: [],
      retryStatus: {
        0: '重试中',
        1: '重试完成',
        2: '最大次数',
        3: '暂停'
      },
      officialWebsite: officialWebsite()
    }
  },
  methods: {
    handleOk (e) {
      console.log(e)
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log(values)
          saveRetryTask(values).then((res) => {
            this.form.resetFields()
            this.$message.success('新增任务成功')
            this.visible = false
            this.$emit('refreshTable', 1)
          })
        }
      })
    },
    handleChange (value) {
      getSceneList({ groupName: value }).then((res) => {
        this.sceneList = res.data
      })
    },
    isShow (visible, data) {
      this.visible = visible
      getAllGroupNameList().then((res) => {
        this.groupNameList = res.data
      })
    },
    idempotentIdGenerate () {
      const groupName = this.form.getFieldValue('groupName')
      const sceneName = this.form.getFieldValue('sceneName')
      const executorName = this.form.getFieldValue('executorName')
      const argsStr = this.form.getFieldValue('argsStr')

      idempotentIdGenerate({ groupName, sceneName, executorName, argsStr }).then(res => {
        this.form.setFieldsValue({
          'idempotentId': res.data
        })
      })
    }
  }
}
</script>

<style scoped>
</style>

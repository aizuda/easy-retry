(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-77908973","chunk-821810a0","chunk-7e5ff423"],{"08f3":function(t,e){var a={jobStatusEnum:{0:{name:"关闭",color:"#9c1f1f"},1:{name:"开启",color:"#f5a22d"}},taskType:{1:{name:"集群模式",color:"#d06892"},2:{name:"广播模式",color:"#f5a22d"},3:{name:"分片模式",color:"#e1f52d"}},triggerType:{1:{name:"CRON表达式",color:"#d06892"},2:{name:"固定时间",color:"#f5a22d"}},blockStrategy:{1:{name:"丢弃策略",color:"#d06892"},2:{name:"覆盖",color:"#f5a22d"},3:{name:"并行",color:"#e1f52d"}},executorType:{1:{name:"Java",color:"#d06892"}},routeKey:{4:{name:"轮询",color:"#8f68d2"},1:{name:"一致性Hash",color:"#d06892"},2:{name:"随机",color:"#f5a22d"},3:{name:"LRU",color:"#e1f52d"}},taskBatchStatus:{1:{name:"待处理",color:"#64a6ea"},2:{name:"运行中",color:"#1b7ee5"},3:{name:"成功",color:"#087da1"},4:{name:"失败",color:"#f52d80"},5:{name:"停止",color:"#ac2df5"},6:{name:"取消",color:"#f5732d"}},operationReason:{0:{name:""},1:{name:"执行超时",color:"#64a6ea"},2:{name:"无客户端节点",color:"#1b7ee5"},3:{name:"任务已关闭",color:"#087da1"}},taskStatus:{2:{name:"运行中",color:"#1b7ee5"},3:{name:"成功",color:"#087da1"},4:{name:"失败",color:"#f52d80"},5:{name:"停止",color:"#ac2df5"}}};t.exports=a},"119c":function(t,e,a){"use strict";a.r(e);a("b0c0");var o=function(){var t=this,e=t._self._c;return e("div",[e("page-header-wrapper",{staticStyle:{margin:"-24px -1px 0"},on:{back:function(){return t.$router.go(-1)}}},[e("div")]),null!==t.jobBatchInfo?e("a-card",{attrs:{bordered:!1}},[e("a-descriptions",{attrs:{title:"",column:3,bordered:""}},[e("a-descriptions-item",{attrs:{label:"组名称"}},[t._v(" "+t._s(t.jobBatchInfo.groupName)+" ")]),e("a-descriptions-item",{attrs:{label:"任务名称"}},[t._v(" "+t._s(t.jobBatchInfo.jobName)+" ")]),e("a-descriptions-item",{attrs:{label:"状态"}},[e("a-tag",{attrs:{color:t.taskBatchStatus[t.jobBatchInfo.taskBatchStatus].color}},[t._v(" "+t._s(t.taskBatchStatus[t.jobBatchInfo.taskBatchStatus].name)+" ")])],1),e("a-descriptions-item",{attrs:{label:"执行器类型"}},[e("a-tag",{attrs:{color:t.executorType[t.jobBatchInfo.executorType].color}},[t._v(" "+t._s(t.executorType[t.jobBatchInfo.executorType].name)+" ")])],1),e("a-descriptions-item",{attrs:{label:"操作原因"}},[e("a-tag",{attrs:{color:t.operationReason[t.jobBatchInfo.operationReason].color}},[t._v(" "+t._s(t.operationReason[t.jobBatchInfo.operationReason].name)+" ")])],1),e("a-descriptions-item",{attrs:{label:"开始执行时间"}},[t._v(" "+t._s(t.jobBatchInfo.executionAt)+" ")]),e("a-descriptions-item",{attrs:{label:"执行器名称",span:"4"}},[t._v(" "+t._s(t.jobBatchInfo.executorInfo)+" ")]),e("a-descriptions-item",{attrs:{label:"创建时间"}},[t._v(" "+t._s(t.jobBatchInfo.createDt)+" ")])],1)],1):t._e(),e("div",{staticStyle:{margin:"20px 0","border-left":"#f5222d 5px solid","font-size":"medium","font-weight":"bold"}},[t._v("    任务项列表 ")]),e("JobTaskList",{ref:"JobTaskListRef"})],1)},n=[],r=a("3b7a"),s=a("c1df"),c=a.n(s),i=a("08f3"),u=a.n(i),l=a("36e8"),d={name:"JobInfo",components:{JobTaskList:l["default"]},data:function(){return{jobBatchInfo:null,taskBatchStatus:u.a.taskBatchStatus,operationReason:u.a.operationReason,taskType:u.a.taskType,triggerType:u.a.triggerType,blockStrategy:u.a.blockStrategy,executorType:u.a.executorType}},created:function(){var t=this,e=this.$route.query.id,a=this.$route.query.groupName;e&&a?Object(r["d"])(e).then((function(a){t.jobBatchInfo=a.data,t.queryParam={groupName:t.jobBatchInfo.groupName,taskBatchId:e},t.$refs.JobTaskListRef.refreshTable(t.queryParam)})):this.$router.push({path:"/404"})},methods:{jobTaskList:r["h"],parseDate:function(t){return c()(t).format("YYYY-MM-DD HH:mm:ss")}}},f=d,b=a("2877"),h=Object(b["a"])(f,o,n,!1,null,"f40bfb86",null);e["default"]=h.exports},"36e8":function(t,e,a){"use strict";a.r(e);a("b0c0");var o=function(){var t=this,e=t._self._c;return e("a-card",{attrs:{bordered:!1}},[e("div",{staticClass:"table-page-search-wrapper"},[e("a-form",{attrs:{layout:"inline"}},[e("a-row",{attrs:{gutter:48}})],1)],1),e("div",{staticClass:"table-operator"},[t.selectedRowKeys.length>0?e("a-dropdown",{directives:[{name:"action",rawName:"v-action:edit",arg:"edit"}]},[e("a-menu",{attrs:{slot:"overlay"},on:{click:t.onClick},slot:"overlay"},[e("a-menu-item",{key:"1"},[e("a-icon",{attrs:{type:"delete"}}),t._v("删除")],1),e("a-menu-item",{key:"2"},[e("a-icon",{attrs:{type:"edit"}}),t._v("更新")],1)],1),e("a-button",{staticStyle:{"margin-left":"8px"}},[t._v(" 批量操作 "),e("a-icon",{attrs:{type:"down"}})],1)],1):t._e()],1),e("s-table",{ref:"table",attrs:{size:"default",rowKey:function(t){return t.id},columns:t.columns,data:t.loadData,alert:t.options.alert,rowSelection:t.options.rowSelection,scroll:{x:2e3}},scopedSlots:t._u([{key:"serial",fn:function(a,o){return e("span",{},[t._v(" "+t._s(o.id)+" ")])}},{key:"taskStatus",fn:function(a){return e("span",{},[e("a-tag",{attrs:{color:t.taskStatus[a].color}},[t._v(" "+t._s(t.taskStatus[a].name)+" ")])],1)}},{key:"clientInfo",fn:function(a){return e("span",{},[t._v(" "+t._s(""!==a?a.split("@")[1]:"")+" ")])}},{key:"action",fn:function(a,o){return e("span",{},[[e("a",{on:{click:function(e){return t.handleLog(o)}}},[t._v("日志")])]],2)}}])})],1)},n=[],r=a("261e"),s=a("27e3"),c=a("2af9"),i=a("3b7a"),u=a("0fea"),l=a("08f3"),d=a.n(l),f=a("a03c"),b={name:"JobTaskList",components:{AInput:s["a"],ATextarea:r["a"],STable:c["j"],JobLogMessageList:f["default"]},data:function(){var t=this;return{currentComponet:"List",record:"",mdl:{},visible:!1,advanced:!1,queryParam:{},taskStatus:d.a.taskStatus,columns:[{title:"ID",scopedSlots:{customRender:"serial"},width:"8%"},{title:"组名称",dataIndex:"groupName"},{title:"地址",dataIndex:"clientInfo",scopedSlots:{customRender:"clientInfo"}},{title:"参数",dataIndex:"argsStr",ellipsis:!0},{title:"结果",dataIndex:"resultMessage",ellipsis:!0},{title:"状态",dataIndex:"taskStatus",scopedSlots:{customRender:"taskStatus"}},{title:"重试次数",dataIndex:"retryCount"},{title:"开始执行时间",dataIndex:"createDt",sorter:!0,width:"10%"},{title:"操作",fixed:"right",dataIndex:"action",width:"180px",scopedSlots:{customRender:"action"}}],loadData:function(e){return null===t.queryParam.taskBatchId||void 0===t.queryParam.taskBatchId?[]:Object(i["h"])(Object.assign(e,t.queryParam)).then((function(t){return t}))},selectedRowKeys:[],selectedRows:[],options:{alert:{show:!0,clear:function(){t.selectedRowKeys=[]}},rowSelection:{selectedRowKeys:this.selectedRowKeys,onChange:this.onSelectChange}},optionAlertShow:!1,groupNameList:[],sceneList:[]}},created:function(){var t=this;Object(u["i"])().then((function(e){t.groupNameList=e.data,null!==t.groupNameList&&t.groupNameList.length>0&&(t.queryParam["groupName"]=t.groupNameList[0],t.$refs.table.refresh(!0),t.handleChange(t.groupNameList[0]))}))},methods:{handleNew:function(){this.$refs.saveRetryTask.isShow(!0,null)},handleBatchNew:function(){this.$refs.batchSaveRetryTask.isShow(!0,null)},handleChange:function(t){},toggleAdvanced:function(){this.advanced=!this.advanced},handleLog:function(t){this.$router.push({path:"/job/log/list",query:{taskBatchId:t.taskBatchId,jobId:t.jobId}})},handleOk:function(t){},handleSuspend:function(t){},handleRecovery:function(t){},handleFinish:function(t){},handleTrigger:function(t){},refreshTable:function(t){this.queryParam=t,this.$refs.table.refresh(!0)},onSelectChange:function(t,e){this.selectedRowKeys=t,this.selectedRows=e},handlerDel:function(){},onClick:function(t){var e=t.key;"2"!==e?"1"===e&&this.handlerDel():this.$refs.batchUpdateRetryTaskInfo.isShow(!0,this.selectedRows,this.selectedRowKeys)}}},h=b,m=a("2877"),p=Object(m["a"])(h,o,n,!1,null,null,null);e["default"]=p.exports},"3b7a":function(t,e,a){"use strict";a.d(e,"g",(function(){return r})),a.d(e,"j",(function(){return s})),a.d(e,"a",(function(){return c})),a.d(e,"l",(function(){return i})),a.d(e,"f",(function(){return u})),a.d(e,"h",(function(){return l})),a.d(e,"e",(function(){return d})),a.d(e,"d",(function(){return f})),a.d(e,"c",(function(){return b})),a.d(e,"b",(function(){return h})),a.d(e,"i",(function(){return m})),a.d(e,"k",(function(){return p}));var o=a("b775"),n={jobList:"/job/list",jobDetail:"/job/",saveJob:"/job/",updateJob:"/job/",updateJobStatus:"/job/status",delJob:"/job/",timeByCron:"/job/cron",jobNameList:"/job/job-name/list",jobBatchList:"/job/batch/list",jobBatchDetail:"/job/batch/",jobTaskList:"/job/task/list",jobLogList:"/job/log/list"};function r(t){return Object(o["b"])({url:n.jobNameList,method:"get",params:t})}function s(t){return Object(o["b"])({url:n.timeByCron,method:"get",params:t})}function c(t){return Object(o["b"])({url:n.delJob+t,method:"delete"})}function i(t){return Object(o["b"])({url:n.updateJobStatus,method:"put",data:t})}function u(t){return Object(o["b"])({url:n.jobLogList,method:"get",params:t})}function l(t){return Object(o["b"])({url:n.jobTaskList,method:"get",params:t})}function d(t){return Object(o["b"])({url:n.jobBatchList,method:"get",params:t})}function f(t){return Object(o["b"])({url:n.jobBatchDetail+t,method:"get"})}function b(t){return Object(o["b"])({url:n.jobList,method:"get",params:t})}function h(t){return Object(o["b"])({url:n.jobDetail+t,method:"get"})}function m(t){return Object(o["b"])({url:n.saveJob,method:"post",data:t})}function p(t){return Object(o["b"])({url:n.updateJob,method:"put",data:t})}},a03c:function(t,e,a){"use strict";a.r(e);var o=function(){var t=this,e=t._self._c;return e("div",[e("a-card",[e("s-table",{ref:"table",attrs:{size:"default",rowKey:"key",columns:t.columns,data:t.loadData},scopedSlots:t._u([{key:"serial",fn:function(a,o){return e("span",{},[t._v(" "+t._s(o.id)+" ")])}}])})],1)],1)},n=[],r=a("c1df"),s=a.n(r),c=a("2af9"),i=a("3b7a"),u={name:"JobLogList",components:{STable:c["j"]},data:function(){var t=this;return{columns:[{title:"#",scopedSlots:{customRender:"serial"},width:"5%"},{title:"信息",dataIndex:"message",width:"50%"},{title:"触发时间",dataIndex:"createDt",sorter:!0,customRender:function(t){return s()(t).format("YYYY-MM-DD HH:mm:ss")},width:"10%"}],queryParam:{},loadData:function(e){return Object(i["f"])(Object.assign(e,t.queryParam)).then((function(e){return t.total=e.total,e}))},total:0}},created:function(){var t=this.$route.query.taskBatchId,e=this.$route.query.jobId;t&&e?(this.queryParam={taskBatchId:t,jobId:e},this.$refs.table.refresh(!0)):this.$router.push({path:"/404"})},methods:{refreshTable:function(t){this.queryParam=t,this.$refs.table.refresh(!0)}}},l=u,d=a("2877"),f=Object(d["a"])(l,o,n,!1,null,"35165b21",null);e["default"]=f.exports}}]);
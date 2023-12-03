(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-5e846c48"],{"2f3a":function(t,a,e){"use strict";e.r(a);var s=function(){var t=this,a=t._self._c;return a("div",[a("a-row",{attrs:{gutter:24}},[a("a-col",{style:{marginBottom:"24px"},attrs:{sm:24,md:12,xl:8}},[a("chart-card",{attrs:{loading:t.loading,title:t.$t("dashboard.analysis.total-sales"),total:t.retryTask.totalNum}},[a("a-tooltip",{attrs:{slot:"action",title:"总任务量: 重试/回调任务量"},slot:"action"},[a("a-icon",{attrs:{type:"info-circle-o"}})],1),a("div",{staticClass:"antv-chart-mini"},[a("div",{staticClass:"chart-wrapper",style:{height:46}},[a("v-chart",{attrs:{"force-fit":!0,height:t.height,data:t.retryTaskBarList,padding:[36,5,18,5]}},[a("v-tooltip"),a("v-bar",{attrs:{position:"x*y"}})],1)],1)]),a("template",{slot:"footer"},[a("div",[a("span",{attrs:{slot:"term"},slot:"term"},[t._v("完成")]),t._v(" "+t._s(t.retryTask.finishNum)+" "),a("a-divider",{attrs:{type:"vertical"}}),a("span",{attrs:{slot:"term"},slot:"term"},[t._v("运行中")]),t._v(" "+t._s(t.retryTask.runningNum)+" "),a("a-divider",{attrs:{type:"vertical"}}),a("span",{attrs:{slot:"term"},slot:"term"},[t._v("最大次数")]),t._v(" "+t._s(t.retryTask.maxCountNum)+" "),a("a-divider",{attrs:{type:"vertical"}}),a("span",{attrs:{slot:"term"},slot:"term"},[t._v("暂停重试")]),t._v(" "+t._s(t.retryTask.suspendNum)+" ")],1)])],2)],1),a("a-col",{style:{marginBottom:"24px"},attrs:{sm:24,md:12,xl:8}},[a("chart-card",{attrs:{loading:t.loading,title:"定时任务",total:t.jobTask.totalNum}},[a("a-tooltip",{attrs:{slot:"action",title:"成功率:总完成/总调度量;"},slot:"action"},[a("a-icon",{attrs:{type:"info-circle-o"}})],1),a("div",[a("a-tooltip",{attrs:{title:"成功率"}},[a("a-progress",{attrs:{"stroke-linecap":"square",percent:t.jobTask.successRate}})],1)],1),a("template",{slot:"footer"},[t._v(" "+t._s(t.$t("dashboard.analysis.job_success"))+" "),a("span",[t._v(t._s(t.jobTask.successNum))]),a("a-divider",{attrs:{type:"vertical"}}),t._v(" "+t._s(t.$t("dashboard.analysis.job_fail"))+" "),a("span",[t._v(t._s(t.jobTask.failNum))])],1)],2)],1),a("a-col",{style:{marginBottom:"24px"},attrs:{sm:24,md:12,xl:8}},[a("a",{attrs:{href:"#"},on:{click:t.jumpPosList}},[a("chart-card",{attrs:{loading:t.loading,title:"总在线机器",total:t.onLineService.total}},[a("a-tooltip",{attrs:{slot:"action",title:"总在线机器:注册到系统的客户端和服务端之和"},slot:"action"},[a("a-icon",{attrs:{type:"info-circle-o"}})],1),a("template",{slot:"footer"},[a("div",[a("span",{attrs:{slot:"term"},slot:"term"},[t._v("客户端")]),t._v(" "+t._s(t.onLineService.clientTotal)+" "),a("a-divider",{attrs:{type:"vertical"}}),a("span",{attrs:{slot:"term"},slot:"term"},[t._v("服务端")]),t._v(" "+t._s(t.onLineService.serverTotal)+" ")],1)])],2)],1)])],1),a("a-card",{attrs:{loading:t.loading,bordered:!0,"body-style":{padding:"0"}}},[a("div",{staticClass:"salesCard"},[a("a-tabs",[t.$auth("RetryAnalysis.retry")?a("a-tab-pane",{key:"1",attrs:{loading:"true",tab:t.$t("dashboard.analysis.sales")}},[a("div",[a("retry-analysis")],1)]):t._e(),t.$auth("JobAnalysis.job")?a("a-tab-pane",{key:"2",attrs:{tab:t.$t("dashboard.analysis.visits")}},[a("div",[a("job-analysis")],1)]):t._e()],1)],1)])],1)},i=[],n=e("2af9"),r=e("0fea"),o=function(){var t=this,a=t._self._c;return a("div",{staticClass:"antd-pro-pages-dashboard-analysis-twoColLayout",class:!t.isMobile&&"desktop"},[a("div",{staticClass:"extra-wrapper",attrs:{slot:"tabBarExtraContent"},slot:"tabBarExtraContent"},[a("div",{staticClass:"extra-item"},[a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("DAY")}}},[a("a-tag",{class:"DAY"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-day")))])],1),a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("WEEK")}}},[a("a-tag",{class:"WEEK"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-week")))])],1),a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("MONTH")}}},[a("a-tag",{class:"MONTH"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-month")))])],1),a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("YEAR")}}},[a("a-tag",{class:"YEAR"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-year")))])],1)]),a("div",{staticClass:"extra-item"},[a("a-range-picker",{attrs:{"show-time":{format:"HH:mm:ss",defaultValue:[t.moment("00:00:00","HH:mm:ss"),t.moment("23:59:59","HH:mm:ss")]},format:"YYYY-MM-DD HH:mm:ss",placeholder:["Start Time","End Time"]},on:{change:t.dateChange}})],1),a("a-select",{style:{width:"256px"},attrs:{placeholder:"请输入组名称"},on:{change:function(a){return t.handleChange(a)}}},t._l(t.groupNameList,(function(e){return a("a-select-option",{key:e,attrs:{value:e}},[t._v(t._s(e))])})),1)],1),a("a-row",[a("a-col",{attrs:{xl:16,lg:12,md:12,sm:24,xs:24}},[a("g2-retry-line",{ref:"viewChart",attrs:{name:"RetryLine"}})],1),a("a-col",{attrs:{xl:8,lg:12,md:12,sm:24,xs:24}},[a("rank-list",{attrs:{title:t.$t("dashboard.analysis.sales-ranking"),list:t.rankList}})],1)],1),a("a-row",{style:{marginTop:"24px"},attrs:{gutter:24,type:"flex"}},[a("a-col",{attrs:{xl:12,lg:24,md:24,sm:24,xs:24}},[a("a-card",{style:{height:"100%"},attrs:{loading:t.loading,bordered:!1,title:t.$t("dashboard.analysis.online-top-search")}},[a("s-table",{ref:"table",attrs:{size:"default",rowKey:function(t,a){return a},columns:t.columns,data:t.loadData,scroll:{x:200}}})],1)],1),a("a-col",{attrs:{xl:12,lg:24,md:24,sm:24,xs:24}},[a("a-card",{staticClass:"antd-pro-pages-dashboard-analysis-salesCard",style:{height:"100%"},attrs:{loading:t.loading,bordered:!1,title:t.$t("dashboard.analysis.the-proportion-of-sales")}},[a("div",{staticStyle:{height:"inherit"},attrs:{slot:"extra"},slot:"extra"},[a("div",{staticClass:"analysis-salesTypeRadio"},[a("a-radio-group",{attrs:{defaultValue:"a"}},[a("a-radio-button",{attrs:{value:"retry"}},[t._v(t._s(t.$t("dashboard.analysis.channel.online")))])],1)],1)]),a("h4",[t._v(t._s(t.$t("dashboard.analysis.sales")))]),a("div",[a("div",[a("v-chart",{attrs:{"force-fit":!0,height:405,data:t.pieData,scale:t.pieScale}},[a("v-tooltip",{attrs:{showTitle:!0,dataKey:"value*percent"}}),a("v-axis"),a("v-legend",{attrs:{dataKey:"value"}}),a("v-pie",{attrs:{position:"percent",color:"value",vStyle:t.pieStyle}}),a("v-coord",{attrs:{type:"theta",radius:.95,innerRadius:.7}})],1)],1)])])],1)],1)],1)},l=[],d=(e("d3b7"),e("159b"),e("432b")),c=e("d01d"),u=e("c1df"),m=e.n(u),h={name:"RetryAnalysis",mixins:[d["a"]],components:{G2RetryLine:c["a"],RankList:n["h"],STable:n["i"]},data:function(){var t=this;return{loading:!0,rankList:[],taskList:[],dateType:"WEEK",type:"WEEK",groupName:"",startTime:[],endTime:[],groupNameList:[],pieScale:[{dataKey:"percent",min:0,formatter:".0%"}],pieData:[],pieStyle:{stroke:"#fff",lineWidth:1},columns:[{title:"组名称",dataIndex:"groupName"},{title:"启动中",dataIndex:"run"},{title:"总场景",dataIndex:"total"}],loadData:function(a){return Object(r["m"])(Object.assign(a)).then((function(a){return t.rankList=a.data.rankList,a.data.taskList}))}}},mounted:function(){var t=this;this.$bus.$on("retry",(function(a){t.total=0,t.successNum=0,t.runningNum=0,t.maxCountNum=0,t.suspendNum=0,t.rankList=a.data.rankList,t.taskList=a.data.taskList,a.data.retryLinkeResponseVOList.forEach((function(a){t.successNum+=a.successNum,t.runningNum+=a.runningNum,t.maxCountNum+=a.maxCountNum,t.suspendNum+=a.suspendNum})),t.total=t.successNum+t.runningNum+t.maxCountNum+t.suspendNum,t.pieData=[{value:"SUCCESS",name:t.successNum,percent:t.successNum/t.total},{value:"RUNNING",name:t.runningNum,percent:t.runningNum/t.total},{value:"MAXCOUNT",name:t.maxCountNum,percent:t.maxCountNum/t.total},{value:"SUSPEND",name:t.suspendNum,percent:t.suspendNum/t.total}]}))},methods:{moment:m.a,dataHandler:function(t){this.dateType=t,this.type=t,this.$refs.viewChart.getDashboardRetryLine(this.groupName,this.type,this.startTime,this.endTime)},handleChange:function(t){this.groupName=t,this.$refs.viewChart.getDashboardRetryLine(this.groupName,this.type,this.startTime,this.endTime)},dateChange:function(t,a){this.startTime=a[0],this.endTime=a[1],this.type=""===this.startTime?"WEEK":"OTHERS",this.$refs.viewChart.getDashboardRetryLine(this.groupName,this.type,this.startTime,this.endTime)}},created:function(){var t=this;Object(r["j"])().then((function(a){t.groupNameList=a.data})),setTimeout((function(){t.loading=!t.loading}),1e3)}},p=h,f=(e("57d4"),e("2877")),y=Object(f["a"])(p,o,l,!1,null,"1cb0bb97",null),v=y.exports,b=function(){var t=this,a=t._self._c;return a("div",{staticClass:"antd-pro-pages-dashboard-analysis-twoColLayout",class:!t.isMobile&&"desktop"},[a("div",{staticClass:"extra-wrapper",attrs:{slot:"tabBarExtraContent"},slot:"tabBarExtraContent"},[a("div",{staticClass:"extra-item"},[a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("DAY")}}},[a("a-tag",{class:"DAY"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-day")))])],1),a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("WEEK")}}},[a("a-tag",{class:"WEEK"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-week")))])],1),a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("MONTH")}}},[a("a-tag",{class:"MONTH"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-month")))])],1),a("a",{attrs:{href:"#"},on:{click:function(a){return t.dataHandler("YEAR")}}},[a("a-tag",{class:"YEAR"==t.dateType?"in":"on"},[t._v(t._s(t.$t("dashboard.analysis.all-year")))])],1)]),a("div",{staticClass:"extra-item"},[a("a-range-picker",{attrs:{"show-time":{format:"HH:mm:ss",defaultValue:[t.moment("00:00:00","HH:mm:ss"),t.moment("23:59:59","HH:mm:ss")]},format:"YYYY-MM-DD HH:mm:ss",placeholder:["Start Time","End Time"]},on:{change:t.dateChange}})],1),a("a-select",{style:{width:"256px"},attrs:{placeholder:"请输入组名称"},on:{change:function(a){return t.handleChange(a)}}},t._l(t.groupNameList,(function(e){return a("a-select-option",{key:e,attrs:{value:e}},[t._v(t._s(e))])})),1)],1),a("a-row",[a("a-col",{attrs:{xl:16,lg:12,md:12,sm:24,xs:24}},[a("g2-job-line",{ref:"jobViewChart",attrs:{name:"G2JobLine"}})],1),a("a-col",{attrs:{xl:8,lg:12,md:12,sm:24,xs:24}},[a("rank-list",{attrs:{title:t.$t("dashboard.analysis.sales-ranking"),list:t.rankList}})],1)],1),a("a-row",{style:{marginTop:"24px"},attrs:{gutter:24,type:"flex"}},[a("a-col",{attrs:{xl:12,lg:24,md:24,sm:24,xs:24}},[a("a-card",{style:{height:"100%"},attrs:{loading:t.loading,bordered:!1,title:t.$t("dashboard.analysis.online-top-search")}},[a("s-table",{ref:"table",attrs:{size:"default",rowKey:function(t,a){return a},columns:t.columns,data:t.loadData,scroll:{x:200}}})],1)],1),a("a-col",{attrs:{xl:12,lg:24,md:24,sm:24,xs:24}},[a("a-card",{staticClass:"antd-pro-pages-dashboard-analysis-salesCard",style:{height:"100%"},attrs:{loading:t.loading,bordered:!1,title:t.$t("dashboard.analysis.the-proportion-of-sales")}},[a("div",{staticStyle:{height:"inherit"},attrs:{slot:"extra"},slot:"extra"},[a("div",{staticClass:"analysis-salesTypeRadio"},[a("a-radio-group",{attrs:{defaultValue:"a"}},[a("a-radio-button",{attrs:{value:"timing"}},[t._v(t._s(t.$t("dashboard.analysis.channel.stores")))])],1)],1)]),a("h4",[t._v(t._s(t.$t("dashboard.analysis.job.sales")))]),a("div",[a("div",[a("v-chart",{attrs:{"force-fit":!0,height:405,data:t.pieData,scale:t.pieScale}},[a("v-tooltip",{attrs:{showTitle:!0,dataKey:"value*percent"}}),a("v-axis"),a("v-legend",{attrs:{dataKey:"value"}}),a("v-pie",{attrs:{position:"percent",color:"value",vStyle:t.pieStyle}}),a("v-coord",{attrs:{type:"theta",radius:.95,innerRadius:.7}})],1)],1)])])],1)],1)],1)},g=[],N=function(){var t=this;t._self._c;return t._m(0)},T=[function(){var t=this,a=t._self._c;return a("div",[a("div",{attrs:{id:"jobViewData"}})])}],k=e("7f1a"),x=e("7104"),_={name:"JobLine",data:function(){return{viewRecords:[],chart:null}},mounted:function(){this.getDashboardJobLine(),this.createView()},methods:{getDashboardJobLine:function(t){var a=this,e=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"WEEK",s=arguments.length>2?arguments[2]:void 0,i=arguments.length>3?arguments[3]:void 0;Object(r["l"])({groupName:t,type:e,startTime:s,endTime:i}).then((function(t){a.$bus.$emit("job",t),a.viewCharts(t.data.dispatchQuantityResponseVOList)}))},viewCharts:function(t){var a=new x;if(void 0!==t&&null!==t){var e=a.createView().source(t);e.transform({type:"fold",fields:["success","fail"],key:"name",value:"viewTotal",retains:["total","createDt"]}),this.chart.source(e,{date:{type:"cat"}}),this.chart.axis("viewTotal",{label:{textStyle:{fill:"#aaaaaa"}}}),this.chart.tooltip({crosshairs:{type:"line"}}),this.chart.line().position("createDt*viewTotal").color("name",["#1890ff","#c28c62"]).shape("smooth"),this.chart.point().position("createDt*viewTotal").color("name",["#1890ff","#c28c62"]).size(4).shape("circle").style({stroke:"#fff",lineWidth:1}),this.chart.render()}},createView:function(){this.chart=new k["Chart"]({container:"jobViewData",forceFit:!0,height:410,padding:[20,90,60,50]})}}},C=_,L=Object(f["a"])(C,N,T,!1,null,null,null),w=L.exports,j={name:"JobAnalysis",mixins:[d["a"]],components:{G2JobLine:w,RankList:n["h"],STable:n["i"]},data:function(){var t=this;return{loading:!0,rankList:[],taskList:[],dispatchQuantityResponseVOList:[],dateType:"WEEK",type:"WEEK",groupName:"",startTime:"",endTime:"",successNum:0,failNum:0,stopNum:0,cancelNum:0,total:0,groupNameList:[],pieScale:[{dataKey:"percent",min:0,formatter:".0%"}],pieData:[],pieStyle:{stroke:"#fff",lineWidth:1},columns:[{title:"组名称",dataIndex:"groupName"},{title:"运行中任务数",dataIndex:"run"},{title:"总任务数",dataIndex:"total"}],loadData:function(a){return Object(r["l"])(Object.assign(a)).then((function(a){return t.rankList=a.data.rankList,a.data.taskList}))}}},mounted:function(){var t=this;this.$bus.$on("job",(function(a){t.total=0,t.successNum=0,t.failNum=0,t.stopNum=0,t.cancelNum=0,t.rankList=a.data.rankList,t.taskList=a.data.taskList,a.data.dispatchQuantityResponseVOList.forEach((function(a){t.successNum+=a.successNum,t.failNum+=a.failNum,t.stopNum+=a.stopNum,t.cancelNum+=a.cancelNum})),t.total=t.successNum+t.failNum+t.stopNum+t.cancelNum,t.pieData=[{value:"SUCCESS",name:t.successNum,percent:t.successNum/t.total},{value:"FAIL",name:t.failNum,percent:t.failNum/t.total},{value:"STOP",name:t.stopNum,percent:t.stopNum/t.total},{value:"CANCEL",name:t.cancelNum,percent:t.cancelNum/t.total}]}))},methods:{moment:m.a,dataHandler:function(t){this.dateType=t,this.type=t,this.$refs.jobViewChart.getDashboardJobLine(this.groupName,this.type,this.startTime,this.endTime)},handleChange:function(t){this.groupName=t,this.$refs.jobViewChart.getDashboardJobLine(this.groupName,this.type,this.startTime,this.endTime)},dateChange:function(t,a){this.startTime=a[0],this.endTime=a[1],this.type=""===this.startTime?"WEEK":"OTHERS",this.$refs.jobViewChart.getDashboardJobLine(this.groupName,this.type,this.startTime,this.endTime)}},created:function(){var t=this;Object(r["j"])().then((function(a){t.groupNameList=a.data})),setTimeout((function(){t.loading=!t.loading}),1e3)}},E=j,S=(e("558a"),Object(f["a"])(E,b,g,!1,null,"9f5509c6",null)),H=S.exports,$={name:"Analysis",components:{RetryAnalysis:v,JobAnalysis:H,ChartCard:n["b"],MiniArea:n["d"],MiniProgress:n["e"],Bar:n["a"],Trend:n["j"],NumberInfo:n["g"],MiniSmoothArea:n["f"]},data:function(){return{loading:!0,height:100,retryTaskBarList:[],retryTask:{totalNum:0,runningNum:0,finishNum:0,maxCountNum:0,suspendNum:0},jobTask:{successRate:0,successNum:0,failNum:0,totalNum:0},onLineService:{clientTotal:0,serverTotal:0,total:0}}},computed:{},methods:{jumpPosList:function(){this.$router.push({path:"/dashboard/pods"})}},created:function(){var t=this;Object(r["n"])().then((function(a){t.retryTask=a.data.retryTask,t.jobTask=a.data.jobTask,t.onLineService=a.data.onLineService,t.retryTaskBarList=a.data.retryTaskBarList})),setTimeout((function(){t.loading=!t.loading}),1e3)}},D=$,R=(e("ba39"),Object(f["a"])(D,s,i,!1,null,"5b067982",null));a["default"]=R.exports},"432b":function(t,a,e){"use strict";e.d(a,"a",(function(){return n}));var s=e("5530"),i=e("5880"),n={computed:Object(s["a"])(Object(s["a"])({},Object(i["mapState"])({layout:function(t){return t.app.layout},navTheme:function(t){return t.app.theme},primaryColor:function(t){return t.app.color},colorWeak:function(t){return t.app.weak},fixedHeader:function(t){return t.app.fixedHeader},fixedSidebar:function(t){return t.app.fixedSidebar},contentWidth:function(t){return t.app.contentWidth},autoHideHeader:function(t){return t.app.autoHideHeader},isMobile:function(t){return t.app.isMobile},sideCollapsed:function(t){return t.app.sideCollapsed},multiTab:function(t){return t.app.multiTab}})),{},{isTopMenu:function(){return"topmenu"===this.layout}}),methods:{isSideMenu:function(){return!this.isTopMenu}}}},"558a":function(t,a,e){"use strict";e("bf88")},"57d4":function(t,a,e){"use strict";e("ac44")},ac44:function(t,a,e){},b001:function(t,a,e){},ba39:function(t,a,e){"use strict";e("b001")},bf88:function(t,a,e){}}]);
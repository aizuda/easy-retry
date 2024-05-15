import{_ as j}from"./table-header-operation.vue_vue_type_script_setup_true_lang-CAikEgod.js";import{bj as D,d as $,U as R,V as C,a as A,$ as a,r as z,W as E,o as U,c as F,w as r,f as n,g as V,t as B,h as e,n as M,B as O,s as P,b as G,a9 as I,I as K,aa as W}from"./index-DydImCNJ.js";import{_ as H,u as L,a as J}from"./table-BB81mEh2.js";import{u as Q,a as X,_ as Y,b as Z}from"./form-CXP3EjI2.js";import{_ as ee}from"./operate-drawer-B2Yn2ELy.js";import{_ as ae}from"./Space-HTlydpS2.js";import{_ as te}from"./search-form.vue_vue_type_script_setup_true_lang-G2vaTHhL.js";import{b as ne}from"./Grid-C0ryvMLX.js";import"./close-fullscreen-rounded-Cdf1DUar.js";import"./round-search-BN-yNV5a.js";function oe(o){return D({url:"/namespace/list",method:"get",params:o})}function se(o){return D({url:"/namespace",method:"post",data:o})}function le(o){return D({url:"/namespace",method:"put",data:o})}const ie=$({name:"NamespaceOperateDrawer",__name:"namespace-operate-drawer",props:R({operateType:{},rowData:{}},{visible:{type:Boolean,default:!1},visibleModifiers:{}}),emits:R(["submitted"],["update:visible"]),setup(o,{emit:y}){const i=o,p=y,m=C(o,"visible"),{formRef:u,validate:k,restoreValidation:h}=Q(),{defaultRequiredRule:f}=X(),x=A(()=>({add:a("page.namespace.addNamespace"),edit:a("page.namespace.editNamespace")})[i.operateType]),s=z(w());function w(){return{name:"",uniqueId:""}}const T={name:f};function q(){if(i.operateType==="add"){Object.assign(s,w());return}i.operateType==="edit"&&i.rowData&&Object.assign(s,i.rowData)}function S(){m.value=!1}async function _(){var b,l;if(await k(),i.operateType==="add"){const{name:t,uniqueId:c}=s,{error:g}=await se({name:t,uniqueId:c});if(g)return;(b=window.$message)==null||b.success(a("common.addSuccess"))}if(i.operateType==="edit"){const{id:t,name:c,uniqueId:g}=s,{error:v}=await le({id:t,name:c,uniqueId:g});if(v)return;(l=window.$message)==null||l.success(a("common.updateSuccess"))}S(),p("submitted")}return E(m,()=>{m.value&&(q(),h())}),(b,l)=>{const t=M,c=Y,g=Z,v=O,d=ae;return U(),F(ee,{modelValue:m.value,"onUpdate:modelValue":l[2]||(l[2]=N=>m.value=N),title:x.value,onSubmitted:_},{footer:r(()=>[n(d,{size:16},{default:r(()=>[n(v,{onClick:S},{default:r(()=>[V(B(e(a)("common.cancel")),1)]),_:1}),n(v,{type:"primary",onClick:_},{default:r(()=>[V(B(e(a)("common.save")),1)]),_:1})]),_:1})]),default:r(()=>[n(g,{ref_key:"formRef",ref:u,model:s,rules:T},{default:r(()=>[n(c,{label:e(a)("page.namespace.uniqueId"),path:"uniqueId"},{default:r(()=>[n(t,{value:s.uniqueId,"onUpdate:value":l[0]||(l[0]=N=>s.uniqueId=N),disabled:i.operateType==="edit",placeholder:e(a)("page.namespace.form.uniqueId")},null,8,["value","disabled","placeholder"])]),_:1},8,["label"]),n(c,{label:e(a)("page.namespace.name"),path:"name"},{default:r(()=>[n(t,{value:s.name,"onUpdate:value":l[1]||(l[1]=N=>s.name=N),placeholder:e(a)("page.namespace.form.name")},null,8,["value","placeholder"])]),_:1},8,["label"])]),_:1},8,["model"])]),_:1},8,["modelValue","title"])}}}),re=$({name:"NamespaceSearch",__name:"namespace-search",props:{model:{required:!0},modelModifiers:{}},emits:R(["reset","search"],["update:model"]),setup(o,{emit:y}){const i=y,p=C(o,"model");function m(){i("reset")}function u(){i("search")}return(k,h)=>{const f=M,x=H,s=te;return U(),F(s,{model:p.value,onSearch:u,onReset:m},{default:r(()=>[n(x,{span:"24 s:12 m:6",label:e(a)("page.namespace.keyword"),path:"keyword",class:"pr-24px"},{default:r(()=>[n(f,{value:p.value.keyword,"onUpdate:value":h[0]||(h[0]=w=>p.value.keyword=w),placeholder:e(a)("page.namespace.form.keyword")},null,8,["value","placeholder"])]),_:1},8,["label"])]),_:1},8,["model"])}}}),de={class:"min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto"};function me(o){return typeof o=="function"||Object.prototype.toString.call(o)==="[object Object]"&&!W(o)}const ye=$({name:"namespace",__name:"index",setup(o){const y=P(),{columns:i,columnChecks:p,data:m,getData:u,loading:k,mobilePagination:h,searchParams:f,resetSearchParams:x}=L({apiFn:oe,apiParams:{page:1,size:10,keyword:void 0},columns:()=>[{key:"index",title:a("common.index"),align:"center",width:64},{key:"name",title:a("page.namespace.name"),align:"left",width:120},{key:"uniqueId",title:a("page.namespace.uniqueId"),align:"left",width:180},{key:"createDt",title:a("page.common.createTime"),align:"left",width:130},{key:"updateDt",title:a("page.common.upadteTime"),align:"left",width:130},{key:"operate",title:a("common.operate"),align:"center",width:64,render:l=>{let t;return n("div",{class:"flex-center gap-8px"},[n(O,{type:"primary",ghost:!0,size:"small",onClick:()=>b(l.id)},me(t=a("common.edit"))?t:{default:()=>[t]})])}}]}),{drawerVisible:s,operateType:w,editingData:T,handleAdd:q,handleEdit:S,checkedRowKeys:_}=J(m,u);function b(l){S(l)}return(l,t)=>{const c=j,g=ne,v=K;return U(),G("div",de,[n(re,{model:e(f),"onUpdate:model":t[0]||(t[0]=d=>I(f)?f.value=d:null),onReset:e(x),onSearch:e(u)},null,8,["model","onReset","onSearch"]),n(v,{title:e(a)("page.namespace.title"),bordered:!1,size:"small",class:"sm:flex-1-hidden card-wrapper","header-class":"view-card-header"},{"header-extra":r(()=>[n(c,{columns:e(p),"onUpdate:columns":t[1]||(t[1]=d=>I(p)?p.value=d:null),"disabled-delete":e(_).length===0,loading:e(k),"show-delete":!1,onAdd:e(q),onRefresh:e(u)},null,8,["columns","disabled-delete","loading","onAdd","onRefresh"])]),default:r(()=>[n(g,{"checked-row-keys":e(_),"onUpdate:checkedRowKeys":t[2]||(t[2]=d=>I(_)?_.value=d:null),columns:e(i),data:e(m),"flex-height":!e(y).isMobile,"scroll-x":962,loading:e(k),remote:"","row-key":d=>d.id,pagination:e(h),class:"sm:h-full"},null,8,["checked-row-keys","columns","data","flex-height","loading","row-key","pagination"]),n(ie,{visible:e(s),"onUpdate:visible":t[3]||(t[3]=d=>I(s)?s.value=d:null),"operate-type":e(w),"row-data":e(T),onSubmitted:e(u)},null,8,["visible","operate-type","row-data","onSubmitted"])]),_:1},8,["title"])])}}});export{ye as default};

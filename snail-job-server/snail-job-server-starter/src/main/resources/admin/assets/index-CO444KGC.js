import{d as E,s as v,a as I,p as h,q as T,o as k,c as S,w as s,f as o,e as n,h as e,$ as t,t as i,g as c,I as V,N as y}from"./index-C0jHFrT7.js";import{_ as O,a as j}from"./DescriptionsItem-7KqcbMhz.js";import{_ as R}from"./Space-DUmk9ljR.js";const C="snail-job",w="module",A="1.0.0",D="A flexible, reliable, and fast platform for distributed task retry and distributed task scheduling.",L="Apache-2.0",N="https://gitee.com/aizuda/snail-job",P={githubUrl:"https://github.com/aizuda/snail-job.git",giteeUrl:"https://gitee.com/aizuda/snail-job.git"},U={url:"https://gitee.com/aizuda/snail-job/issues"},x=["Job","Retry","Snail Job","Vue3 admin ","vue-admin-template","Vite5","TypeScript","naive-ui","naive-ui-admin","ant-design-vue v4","UnoCSS"],M={node:">=18.12.0",pnpm:">=8.7.0"},B={build:"vite build --mode prod","build:test":"vite build --mode test",cleanup:"sa cleanup",commit:"sa git-commit",dev:"vite --mode test","dev:prod":"vite --mode prod","gen-route":"sa gen-route",lint:"eslint . --fix",prepare:"simple-git-hooks",preview:"vite preview",release:"sa release",typecheck:"vue-tsc --noEmit --skipLibCheck","update-pkg":"sa update-pkg"},z={"@better-scroll/core":"2.5.1","@codemirror/lang-javascript":"^6.2.2","@codemirror/lang-json":"^6.0.1","@codemirror/theme-one-dark":"^6.1.2","@iconify/vue":"4.1.2","@sa/axios":"workspace:*","@sa/color":"workspace:*","@sa/cron-input":"workspace:*","@sa/hooks":"workspace:*","@sa/materials":"workspace:*","@sa/utils":"workspace:*","@vueuse/core":"10.9.0",clipboard:"2.0.11",dayjs:"1.11.11",echarts:"5.5.0","lodash-es":"4.17.21","naive-ui":"2.38.2",nprogress:"0.2.0",pinia:"2.1.7","ts-md5":"1.3.1",vue:"3.4.26","vue-codemirror6":"^1.3.0","vue-draggable-plus":"0.4.0","vue-i18n":"9.13.1","vue-router":"4.3.2"},H={"@elegant-router/vue":"0.3.6","@iconify/json":"2.2.207","@sa/scripts":"workspace:*","@sa/uno-preset":"workspace:*","@soybeanjs/eslint-config":"1.3.4","@types/lodash-es":"4.17.12","@types/node":"20.12.10","@types/nprogress":"0.2.3","@unocss/eslint-config":"0.59.4","@unocss/preset-icons":"0.59.4","@unocss/preset-uno":"0.59.4","@unocss/transformer-directives":"0.59.4","@unocss/transformer-variant-group":"0.59.4","@unocss/vite":"0.59.4","@vitejs/plugin-vue":"5.0.4","@vitejs/plugin-vue-jsx":"3.1.0",eslint:"9.2.0","eslint-plugin-vue":"9.25.0","lint-staged":"15.2.2",sass:"1.76.0","simple-git-hooks":"2.11.1",tsx:"4.9.3",typescript:"5.4.5","unplugin-icons":"0.19.0","unplugin-vue-components":"0.27.0",vite:"5.2.11","vite-plugin-progress":"0.0.7","vite-plugin-svg-icons":"2.0.1","vite-plugin-vue-devtools":"7.1.3","vue-eslint-parser":"9.4.2","vue-tsc":"2.0.16"},W="https://snailjob.opensnail.com",X="https://snailjob.opensnail.com/docs/preview.html",r={name:C,type:w,version:A,description:D,license:L,homepage:N,repository:P,bugs:U,keywords:x,engines:M,scripts:B,dependencies:z,devDependencies:H,"simple-git-hooks":{"commit-msg":"pnpm sa git-commit-verify","pre-commit":"pnpm typecheck && pnpm lint-staged"},"lint-staged":{"*":"eslint --fix"},officialWebsite:W,website:X};var F={VITE_APP_TITLE:"Snail Job",VITE_APP_DESC:"A flexible, reliable, and fast platform for distributed task retry and distributed task scheduling.",VITE_APP_VERSION:"1.0.0-beta2",VITE_APP_DEFAULT_TOKEN:"SJ_Wyz3dmsdbDOkDujOTSSoBjGQP1BMsVnj",VITE_ICON_PREFIX:"icon",VITE_ICON_LOCAL_PREFIX:"icon-local",VITE_AUTH_ROUTE_MODE:"static",VITE_ROUTE_HOME:"home",VITE_MENU_ICON:"mdi:menu",VITE_HTTP_PROXY:"Y",VITE_ROUTER_HISTORY_MODE:"hash",VITE_SERVICE_SUCCESS_CODE:"1",VITE_SERVICE_LOGOUT_CODES:"8888,8889",VITE_SERVICE_MODAL_LOGOUT_CODES:"5001",VITE_SERVICE_EXPIRED_TOKEN_CODES:"9999,9998",VITE_STATIC_SUPER_ROLE:"R_ADMIN",VITE_SOURCE_MAP:"N",VITE_STORAGE_PREFIX:"",VITE_BASE_URL:"/snail-job",VITE_SERVICE_BASE_URL:"/snail-job",VITE_OTHER_SERVICE_BASE_URL:`{
  "demo": "http://localhost:9529"
}`,BASE_URL:"/snail-job",MODE:"prod",DEV:!1,PROD:!0,SSR:!1};const G=["innerHTML"],J=["href"],$=["href"],Y=["href"],K=["href"],oe=E({name:"about",__name:"index",setup(q){const u=v(),_=I(()=>u.isMobile?1:2),{VITE_APP_VERSION:d}=F,m=h(`${T.get("version")||d}`),g="2024-05-23 22:48:55";return(Q,Z)=>{const l=V,a=O,p=y,b=j,f=R;return k(),S(f,{vertical:"",size:16},{default:s(()=>[o(l,{title:e(t)("page.about.title"),bordered:!1,size:"small",segmented:"",class:"card-wrapper"},{default:s(()=>[n("p",{innerHTML:e(t)("page.about.introduction")},null,8,G)]),_:1},8,["title"]),o(l,{title:e(t)("page.about.projectInfo.title"),bordered:!1,size:"small",segmented:"",class:"card-wrapper"},{default:s(()=>[o(b,{"label-placement":"left",bordered:"",size:"small",column:_.value},{default:s(()=>[o(a,{label:e(t)("page.about.projectInfo.officialWebsite")},{default:s(()=>[n("a",{class:"text-primary",href:e(r).officialWebsite,target:"_blank",rel:"noopener noreferrer"},i(e(t)("page.about.projectInfo.officialWebsite")),9,J)]),_:1},8,["label"]),o(a,{label:e(t)("page.about.projectInfo.version")},{default:s(()=>[o(p,{type:"primary"},{default:s(()=>[c(i(m.value),1)]),_:1})]),_:1},8,["label"]),o(a,{label:e(t)("page.about.projectInfo.githubLink")},{default:s(()=>[n("a",{class:"text-primary",href:e(r).repository.githubUrl,target:"_blank",rel:"noopener noreferrer"},i(e(t)("page.about.projectInfo.githubLink")),9,$)]),_:1},8,["label"]),o(a,{label:e(t)("page.about.projectInfo.giteeLink")},{default:s(()=>[n("a",{class:"text-primary",href:e(r).repository.giteeUrl,target:"_blank",rel:"noopener noreferrer"},i(e(t)("page.about.projectInfo.giteeLink")),9,Y)]),_:1},8,["label"]),o(a,{label:e(t)("page.about.projectInfo.previewLink")},{default:s(()=>[n("a",{class:"text-primary",href:e(r).website,target:"_blank",rel:"noopener noreferrer"},i(e(t)("page.about.projectInfo.previewLink")),9,K)]),_:1},8,["label"]),o(a,{label:e(t)("page.about.projectInfo.latestBuildTime")},{default:s(()=>[o(p,{type:"primary"},{default:s(()=>[c(i(e(g)),1)]),_:1})]),_:1},8,["label"])]),_:1},8,["column"])]),_:1},8,["title"])]),_:1})}}});export{oe as default};

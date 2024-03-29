// eslint-disable-next-line
import { UserLayout, BasicLayout, BlankLayout } from '@/layouts'

const RouteView = {
  name: 'RouteView',
  render: (h) => h('router-view')
}

export const asyncRouterMap = [
  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: 'menu.home' },
    redirect: '/dashboard/analysis',
    children: [
      // dashboard
      {
        path: '/dashboard',
        name: 'dashboard',
        redirect: '/dashboard/analysis',
        hideChildrenInMenu: true,
        component: RouteView,
        meta: { title: 'menu.dashboard', keepAlive: true, icon: 'dashboard', permission: ['dashboard'] },
        children: [
          {
            path: '/dashboard/analysis',
            name: 'Analysis',
            component: () => import('@/views/dashboard/Analysis.vue'),
            meta: { title: 'menu.dashboard.analysis', keepAlive: true, permission: ['dashboard'] }
          },
          {
            path: '/dashboard/pods',
            name: 'PodList',
            component: () => import('@/views/dashboard/PodList'),
            meta: { title: 'menu.dashboard.analysis', keepAlive: true, permission: ['dashboard'] }
          }
        ]
      },
      // profile
      {
        path: '/namespace',
        name: 'namespace',
        component: () => import('@/views/namespace/NamespaceList'),
        meta: { title: '命名空间', icon: 'team', permission: ['namespace'] }
      },
      // profile
      {
        path: '/group',
        name: 'GroupConfigList',
        component: RouteView,
        hideChildrenInMenu: true,
        redirect: '/group/list',
        meta: { title: '组管理', hideChildrenInMenu: true, icon: 'team', permission: ['group'] },
        children: [
          {
            path: '/group/list',
            name: 'basicConfigList',
            component: () => import('@/views/config/GroupList'),
            meta: { title: '组配置列表', hidden: true, icon: 'team', permission: ['group'] }
          },
          {
            path: '/group/config',
            name: 'GroupConfig',
            hidden: true,
            component: () => import('@/views/config/basicConfigForm/BasicConfigForm'),
            meta: {
              title: '组信息配置',
              hidden: true,
              hideChildrenInMenu: true,
              icon: 'profile',
              permission: ['group']
            }
          },
          {
            path: '/group/info',
            name: 'GroupInfo',
            hidden: true,
            component: () => import('@/views/config/GroupInfo'),
            meta: {
              title: '组配置详情',
              hidden: true,
              hideChildrenInMenu: true,
              icon: 'profile',
              permission: ['group']
            }
          }
        ]
      },

      {
        path: '/retry',
        name: 'RetryTask',
        component: RouteView,
        redirect: '/retry/list',
        meta: { title: '重试任务管理', icon: 'schedule', permission: ['retryTask'] },
        children: [
          {
            path: '/retry/list',
            name: 'RetryTaskList',
            component: () => import('@/views/task/RetryTaskList'),
            meta: { title: '重试任务', icon: 'profile', keepAlive: true, permission: ['retryTask'] }
          },
          {
            path: '/retry/info',
            name: 'RetryTaskInfo',
            hidden: true,
            component: () => import('@/views/task/RetryTaskInfo'),
            meta: { title: '重试任务详情', icon: 'profile', keepAlive: true, permission: ['retryTask'] }
          },
          {
            path: '/retry/dead-letter/list',
            name: 'RetryDeadLetterList',
            component: () => import('@/views/task/RetryDeadLetterList'),
            meta: { title: '死信任务', icon: 'profile', permission: ['retryDeadLetter'] }
          },
          {
            path: '/retry/dead-letter/info',
            name: 'RetryDeadLetterInfo',
            hidden: true,
            component: () => import('@/views/task/RetryDeadLetterInfo'),
            meta: { title: '死信任务详情', icon: 'profile', permission: ['retryDeadLetter'] }
          },
          {
            path: '/retry/log/list',
            name: 'RetryLogList',
            component: () => import('@/views/task/RetryLogList'),
            meta: { title: '重试日志', icon: 'profile', permission: ['retryLog'] }
          },
          {
            path: '/retry/log/info',
            name: 'RetryLogInfo',
            hidden: true,
            component: () => import('@/views/task/RetryLogInfo'),
            meta: { title: '重试日志详情', icon: 'profile', permission: ['retryLog'] }
          },
          {
            path: '/retry/scene/list',
            name: 'SceneList',
            component: () => import('@/views/task/SceneList'),
            meta: { title: '场景列表', icon: 'profile', keepAlive: true, permission: ['retryTask'] }
          },
          {
            path: '/retry/scene/config',
            name: 'SceneForm',
            hidden: true,
            component: () => import('@/views/task/form/SceneForm.vue'),
            meta: { title: '场景配置', icon: 'profile', keepAlive: true, permission: ['retryTask'] }
          },
          {
            path: '/retry/notify/list',
            name: 'NotifyList',
            component: () => import('@/views/task/NotifyList'),
            meta: { title: '通知列表', icon: 'profile', keepAlive: true, permission: ['retryTask'] }
          },
          {
            path: '/retry/notify/config',
            name: 'NotifyForm',
            hidden: true,
            component: () => import('@/views/task/form/NotifyForm.vue'),
            meta: { title: '通知配置', icon: 'profile', keepAlive: true, permission: ['retryTask'] }
          }
        ]
      },
      {
        path: '/job',
        name: 'Job',
        component: RouteView,
        redirect: '/job/list',
        meta: { title: '定时任务管理', icon: 'profile', permission: ['job'] },
        children: [
          {
            path: '/job/list',
            name: 'JobList',
            component: () => import('@/views/job/JobList'),
            meta: { title: '定时任务信息', keepAlive: true, icon: 'profile', permission: ['job'] }
          },
          {
            path: '/job/info',
            name: 'JobInfo',
            hidden: true,
            component: () => import('@/views/job/JobInfo'),
            meta: { title: '定时任务详情', keepAlive: true, icon: 'profile', permission: ['job'] }
          },
          {
            path: '/job/config',
            name: 'JobForm',
            hidden: true,
            component: () => import('@/views/job/form/JobForm.vue'),
            meta: { title: '任务配置', icon: 'profile', permission: ['job'] }
          },
          {
            path: '/job/batch/list',
            name: 'JobBatchList',
            component: () => import('@/views/job/JobBatchList'),
            meta: { title: '定时任务批次', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/batch/info',
            name: 'JobBatchInfo',
            hidden: true,
            component: () => import('@/views/job/JobBatchInfo'),
            meta: { title: '定时任务批次详情', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/task/list',
            name: 'JobTaskList',
            hidden: true,
            component: () => import('@/views/job/JobTaskList'),
            meta: { title: '任务项', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/workflow/list',
            name: 'WorkflowList',
            component: () => import('@/views/job/WorkflowList'),
            meta: { title: '工作流信息', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/workflow/edit',
            name: 'WorkflowEdit',
            hidden: true,
            component: () => import('@/views/job/WorkflowEdit'),
            meta: { title: '工作流编辑', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/workflow/copy',
            name: 'WorkflowCopy',
            hidden: true,
            component: () => import('@/views/job/WorkflowCopy'),
            meta: { title: '工作流复制', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/workflow/detail',
            name: 'WorkflowDetail',
            hidden: true,
            component: () => import('@/views/job/WorkflowDetail'),
            meta: { title: '工作流详情', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/workflow/batch/list',
            name: 'WorkflowBatchList',
            component: () => import('@/views/job/WorkflowBatchList'),
            meta: { title: '工作流任务批次', icon: 'profile', permission: ['jobBatch'] }
          },
          {
            path: '/job/workflow/batch/detail',
            name: 'WorkflowBatchDetail',
            hidden: true,
            component: () => import('@/views/job/WorkflowBatchDetail'),
            meta: { title: '工作流任务批次详情', icon: 'profile', permission: ['jobBatch'] }
          }, {
            path: '/job/notify/list',
            name: 'JobNotifyList',
            component: () => import('@/views/job/JobNotifyList'),
            meta: { title: '通知列表', icon: 'profile', keepAlive: true, permission: ['jobNotify'] }
          },
          {
            path: '/job/notify/config',
            name: 'JobNotifyForm',
            hidden: true,
            component: () => import('@/views/job/form/JobNotifyForm.vue'),
            meta: { title: '通知配置', icon: 'profile', keepAlive: true, permission: ['jobNotify'] }
          }
        ]
      },
      {
        path: '/user-list',
        name: 'UserList',
        component: () => import('@/views/user/UserList'),
        meta: { title: '用户管理', icon: 'user', permission: ['user'] }
      },
      {
        path: '/user-form',
        name: 'UserForm',
        hidden: true,
        component: () => import('@/views/user/UserForm'),
        meta: { title: '新增或更新用户', icon: 'profile', permission: ['userForm'] }
      }
    ]
  },
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }
]

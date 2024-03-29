package com.aizuda.easy.retry.server.job.task.support.callback;

import akka.actor.ActorRef;
import cn.hutool.core.collection.CollUtil;
import com.aizuda.easy.retry.common.core.enums.JobTaskTypeEnum;
import com.aizuda.easy.retry.server.common.akka.ActorGenerator;
import com.aizuda.easy.retry.server.common.cache.CacheRegisterTable;
import com.aizuda.easy.retry.server.common.dto.RegisterNodeInfo;
import com.aizuda.easy.retry.server.common.util.ClientInfoUtils;
import com.aizuda.easy.retry.server.job.task.dto.JobExecutorResultDTO;
import com.aizuda.easy.retry.server.job.task.support.JobTaskConverter;
import com.aizuda.easy.retry.template.datasource.persistence.po.JobTask;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: www.byteblogs.com
 * @date : 2023-10-07 10:24
 * @since : 2.4.0
 */
@Component
@Slf4j
public class BroadcastClientCallbackHandler extends AbstractClientCallbackHandler {

    @Override
    public JobTaskTypeEnum getTaskInstanceType() {
        return JobTaskTypeEnum.BROADCAST;
    }

    @Override
    protected void doCallback(final ClientCallbackContext context) {

        JobExecutorResultDTO jobExecutorResultDTO = JobTaskConverter.INSTANCE.toJobExecutorResultDTO(context);
        jobExecutorResultDTO.setTaskId(context.getTaskId());
        jobExecutorResultDTO.setMessage(context.getExecuteResult().getMessage());
        jobExecutorResultDTO.setResult(context.getExecuteResult().getResult());
        jobExecutorResultDTO.setTaskType(getTaskInstanceType().getType());

        ActorRef actorRef = ActorGenerator.jobTaskExecutorResultActor();
        actorRef.tell(jobExecutorResultDTO, actorRef);

    }

    @Override
    protected String chooseNewClient(ClientCallbackContext context) {
        Set<RegisterNodeInfo> nodes = CacheRegisterTable.getServerNodeSet(context.getGroupName(), context.getNamespaceId());
        if (CollUtil.isEmpty(nodes)) {
            log.error("无可执行的客户端信息. jobId:[{}]", context.getJobId());
            return null;
        }

        JobTask jobTask = context.getJobTask();
        String clientInfo = jobTask.getClientInfo();
        String clientId = ClientInfoUtils.clientId(clientInfo);
        RegisterNodeInfo serverNode = CacheRegisterTable.getServerNode(context.getGroupName(), context.getNamespaceId(), clientId);
        if (Objects.isNull(serverNode)) {
            List<JobTask> jobTasks = jobTaskMapper.selectList(new LambdaQueryWrapper<JobTask>()
                    .eq(JobTask::getTaskBatchId, context.getTaskBatchId()));

            Set<String> clientIdList = jobTasks.stream()
                    .map(jobTask1 -> ClientInfoUtils.clientId(jobTask1.getClientInfo()))
                    .collect(Collectors.toSet());
            Set<String> remoteClientIdSet = nodes.stream().map(RegisterNodeInfo::getHostId).collect(Collectors.toSet());
            Sets.SetView<String> diff = Sets.difference(remoteClientIdSet, clientIdList);

            String newClientId = CollUtil.getFirst(diff.stream().iterator());
            RegisterNodeInfo registerNodeInfo = CacheRegisterTable.getServerNode(context.getGroupName(), context.getNamespaceId(), newClientId);
            if (Objects.isNull(registerNodeInfo)) {
                // 如果找不到新的客户端信息，则返回原来的客户端信息
                return clientInfo;
            }

            return ClientInfoUtils.generate(registerNodeInfo);
        }

        return clientInfo;
    }
}

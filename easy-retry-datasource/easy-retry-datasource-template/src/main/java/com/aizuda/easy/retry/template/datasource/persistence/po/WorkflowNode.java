package com.aizuda.easy.retry.template.datasource.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 工作流节点
 * </p>
 *
 * @author xiaowoniu
 * @since 2023-12-12
 */
@Getter
@Setter
@TableName("workflow_node")
public class WorkflowNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 命名空间id
     */
    private String namespaceId;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 任务信息id
     */
    private Long jobId;

    /**
     * 1、任务节点 2、条件节点
     */
    private Byte nodeType;

    /**
     * 1、SpEl、2、Aviator 3、QL
     */
    private Byte expressionType;

    /**
     * 失败策略 1、跳过 2、阻塞
     */
    private Byte failStrategy;

    /**
     * 工作流节点状态 0、关闭、1、开启
     */
    private Byte workflowNodeStatus;

    /**
     * 节点表达式
     */
    private String nodeExpression;

    /**
     * 创建时间
     */
    private LocalDateTime createDt;

    /**
     * 修改时间
     */
    private LocalDateTime updateDt;

    /**
     * 逻辑删除 1、删除
     */
    private Byte deleted;

    /**
     * 扩展字段
     */
    private String extAttrs;
}

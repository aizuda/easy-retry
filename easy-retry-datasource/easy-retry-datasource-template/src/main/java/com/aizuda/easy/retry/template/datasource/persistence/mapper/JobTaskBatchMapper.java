package com.aizuda.easy.retry.template.datasource.persistence.mapper;

import com.aizuda.easy.retry.template.datasource.persistence.dataobject.JobBatchQueryDO;
import com.aizuda.easy.retry.template.datasource.persistence.dataobject.JobBatchResponseDO;
import com.aizuda.easy.retry.template.datasource.persistence.po.JobTaskBatch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 调度任务 Mapper 接口
 * </p>
 *
 * @author www.byteblogs.com
 * @since 2023-09-24
 */
@Mapper
public interface JobTaskBatchMapper extends BaseMapper<JobTaskBatch> {

    List<JobBatchResponseDO> selectJobBatchList(IPage<JobTaskBatch> iPage, @Param("queryDO") JobBatchQueryDO queryDO);
}

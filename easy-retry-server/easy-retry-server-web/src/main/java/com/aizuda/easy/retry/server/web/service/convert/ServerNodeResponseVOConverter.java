package com.aizuda.easy.retry.server.web.service.convert;

import com.aizuda.easy.retry.template.datasource.persistence.po.ServerNode;
import com.aizuda.easy.retry.server.web.model.response.ServerNodeResponseVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author www.byteblogs.com
 * @date 2023-06-06
 * @since 2.0
 */
@Mapper
public interface ServerNodeResponseVOConverter {

    ServerNodeResponseVOConverter INSTANCE = Mappers.getMapper(ServerNodeResponseVOConverter.class);

    List<ServerNodeResponseVO> toServerNodeResponseVO(List<ServerNode> serverNodes);

}

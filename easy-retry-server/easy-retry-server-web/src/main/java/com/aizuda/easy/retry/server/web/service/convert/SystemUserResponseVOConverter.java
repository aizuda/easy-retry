package com.aizuda.easy.retry.server.web.service.convert;

import com.aizuda.easy.retry.server.web.model.request.UserSessionVO;
import com.aizuda.easy.retry.template.datasource.persistence.po.SystemUser;
import com.aizuda.easy.retry.server.web.model.response.SystemUserResponseVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author www.byteblogs.com
 * @date 2022-03-05
 * @since 1.0.0
 */
@Mapper
public interface SystemUserResponseVOConverter {

    SystemUserResponseVOConverter INSTANCE = Mappers.getMapper(SystemUserResponseVOConverter.class);

    SystemUserResponseVO convert(UserSessionVO systemUser);

    SystemUserResponseVO convert(SystemUser systemUser);

    List<SystemUserResponseVO> batchConvert(List<SystemUser> systemUsers);
}

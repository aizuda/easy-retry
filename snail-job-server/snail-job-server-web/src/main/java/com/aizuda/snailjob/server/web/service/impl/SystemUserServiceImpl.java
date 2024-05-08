package com.aizuda.snailjob.server.web.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.aizuda.snailjob.common.core.util.JsonUtil;
import com.aizuda.snailjob.server.common.config.SystemProperties;
import com.aizuda.snailjob.server.common.exception.SnailJobServerException;
import com.aizuda.snailjob.server.web.annotation.RoleEnum;
import com.aizuda.snailjob.server.web.model.base.PageResult;
import com.aizuda.snailjob.server.web.model.request.SystemUserQueryVO;
import com.aizuda.snailjob.server.web.model.request.SystemUserRequestVO;
import com.aizuda.snailjob.server.web.model.request.UserPermissionRequestVO;
import com.aizuda.snailjob.server.web.model.request.UserSessionVO;
import com.aizuda.snailjob.server.web.model.response.PermissionsResponseVO;
import com.aizuda.snailjob.server.web.model.response.SystemUserResponseVO;
import com.aizuda.snailjob.server.web.service.SystemUserService;
import com.aizuda.snailjob.server.web.service.convert.NamespaceResponseVOConverter;
import com.aizuda.snailjob.server.web.service.convert.PermissionsResponseVOConverter;
import com.aizuda.snailjob.server.web.service.convert.SystemUserResponseVOConverter;
import com.aizuda.snailjob.template.datasource.persistence.mapper.NamespaceMapper;
import com.aizuda.snailjob.template.datasource.persistence.mapper.SystemUserMapper;
import com.aizuda.snailjob.template.datasource.persistence.mapper.SystemUserPermissionMapper;
import com.aizuda.snailjob.template.datasource.persistence.po.Namespace;
import com.aizuda.snailjob.template.datasource.persistence.po.SystemUser;
import com.aizuda.snailjob.template.datasource.persistence.po.SystemUserPermission;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author opensnail
 * @since 2022-03-05
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl implements SystemUserService {
    public static final long EXPIRE_TIME = 3600 * 24 * 1000;

    private final SystemUserMapper systemUserMapper;
    private final SystemUserPermissionMapper systemUserPermissionMapper;
    private final NamespaceMapper namespaceMapper;
    private final SystemProperties systemProperties;

    @Override
    public SystemUserResponseVO login(SystemUserRequestVO requestVO) {

        SystemUser systemUser = systemUserMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUsername, requestVO.getUsername().trim()));
        validateUserPassword(requestVO, systemUser);

        String token = getToken(systemUser);

        SystemUserResponseVO systemUserResponseVO = SystemUserResponseVOConverter.INSTANCE.convert(systemUser);
        systemUserResponseVO.setToken(token);

        getPermission(systemUser.getRole(), systemUser.getId(), systemUserResponseVO);

        return systemUserResponseVO;
    }

    private static void validateUserPassword(SystemUserRequestVO requestVO, SystemUser systemUser) {
        if (Objects.isNull(systemUser)) {
            throw new SnailJobServerException("用户名或密码错误");
        }

        if (!SecureUtil.sha256(requestVO.getPassword()).equals(systemUser.getPassword())) {
            throw new SnailJobServerException("用户名或密码错误");
        }
    }

    private void getPermission(Integer role, Long userId, final SystemUserResponseVO systemUserResponseVO) {

        LambdaQueryWrapper<Namespace> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Namespace::getId, Namespace::getUniqueId, Namespace::getName);
        if (RoleEnum.USER.getRoleId().equals(role)) {
            List<SystemUserPermission> systemUserPermissions = systemUserPermissionMapper.selectList(
                new LambdaQueryWrapper<SystemUserPermission>()
                    .select(SystemUserPermission::getNamespaceId)
                    .eq(SystemUserPermission::getSystemUserId, userId)
                    .groupBy(SystemUserPermission::getNamespaceId));
            queryWrapper.in(Namespace::getUniqueId, systemUserPermissions.stream()
                .map(SystemUserPermission::getNamespaceId).collect(Collectors.toList()));
        }

        List<Namespace> namespaces = namespaceMapper.selectList(queryWrapper);
        systemUserResponseVO.setNamespaceIds(
            NamespaceResponseVOConverter.INSTANCE.toNamespaceResponseVOs(namespaces));
    }

    @Override
    public SystemUserResponseVO getUserInfo(UserSessionVO systemUser) {
        SystemUserResponseVO systemUserResponseVO = SystemUserResponseVOConverter.INSTANCE.convert(systemUser);

        getPermission(systemUser.getRole(), systemUser.getId(), systemUserResponseVO);

        return systemUserResponseVO;
    }

    @Override
    @Transactional
    public void addUser(SystemUserRequestVO requestVO) {
        long count = systemUserMapper.selectCount(
            new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, requestVO.getUsername()));
        if (count > 0) {
            throw new SnailJobServerException("该用户已存在");
        }

        SystemUser systemUser = new SystemUser();
        systemUser.setUsername(requestVO.getUsername());
        systemUser.setPassword(SecureUtil.sha256(requestVO.getPassword()));
        systemUser.setRole(requestVO.getRole());

        Assert.isTrue(1 == systemUserMapper.insert(systemUser), () -> new SnailJobServerException("新增用户失败"));

        // 只添加为普通用户添加权限
        List<UserPermissionRequestVO> groupNameList = requestVO.getPermissions();
        if (CollectionUtils.isEmpty(groupNameList) || RoleEnum.ADMIN.getRoleId().equals(requestVO.getRole())) {
            return;
        }

        for (UserPermissionRequestVO permission : groupNameList) {
            SystemUserPermission systemUserPermission = new SystemUserPermission();
            systemUserPermission.setSystemUserId(systemUser.getId());
            systemUserPermission.setGroupName(permission.getGroupName());
            systemUserPermission.setNamespaceId(permission.getNamespaceId());
            Assert.isTrue(1 == systemUserPermissionMapper.insert(systemUserPermission),
                () -> new SnailJobServerException("新增用户权限失败"));
        }

    }

    @Override
    @Transactional
    public void update(SystemUserRequestVO requestVO) {
        SystemUser systemUser = systemUserMapper.selectOne(
            new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getId, requestVO.getId()));
        if (Objects.isNull(systemUser)) {
            throw new SnailJobServerException("该用户不存在");
        }

        if (!systemUser.getUsername().equals(requestVO.getUsername())) {
            long count = systemUserMapper.selectCount(
                new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, requestVO.getUsername()));
            if (count > 0) {
                throw new SnailJobServerException("该用户已存在");
            }
        }

        systemUser.setUsername(requestVO.getUsername());
        if (StrUtil.isNotBlank(requestVO.getPassword())) {
            systemUser.setPassword(SecureUtil.sha256(requestVO.getPassword()));
        }

        systemUser.setRole(requestVO.getRole());

        Assert.isTrue(1 == systemUserMapper.updateById(systemUser), () -> new SnailJobServerException("更新用户失败"));

        // 只添加为普通用户添加权限
        List<UserPermissionRequestVO> permissions = requestVO.getPermissions();
        if (CollectionUtils.isEmpty(permissions) || RoleEnum.ADMIN.getRoleId().equals(requestVO.getRole())) {
            return;
        }

        systemUserPermissionMapper.delete(new LambdaQueryWrapper<SystemUserPermission>()
            .eq(SystemUserPermission::getSystemUserId, systemUser.getId()));

        for (UserPermissionRequestVO permission : permissions) {
            SystemUserPermission systemUserPermission = new SystemUserPermission();
            systemUserPermission.setSystemUserId(systemUser.getId());
            systemUserPermission.setGroupName(permission.getGroupName());
            systemUserPermission.setNamespaceId(permission.getNamespaceId());
            Assert.isTrue(1 == systemUserPermissionMapper.insert(systemUserPermission),
                () -> new SnailJobServerException("更新用户权限失败"));
        }
    }

    @Override
    public PageResult<List<SystemUserResponseVO>> getSystemUserPageList(SystemUserQueryVO queryVO) {
        PageDTO<SystemUser> userPageDTO = new PageDTO<>(queryVO.getPage(), queryVO.getSize());
        userPageDTO = systemUserMapper.selectPage(userPageDTO,
            Wrappers.<SystemUser>lambdaQuery()
                .likeRight(StrUtil.isNotBlank(queryVO.getUsername()), SystemUser::getUsername, queryVO.getUsername())
                .orderByDesc(SystemUser::getId));

        if (CollectionUtils.isEmpty(userPageDTO.getRecords())) {
            return new PageResult<>(userPageDTO, Collections.emptyList());
        }

        List<SystemUserResponseVO> userResponseVOList = SystemUserResponseVOConverter.INSTANCE.batchConvert(
            userPageDTO.getRecords());
        List<SystemUserPermission> userPermissions = systemUserPermissionMapper.selectList(
            Wrappers.<SystemUserPermission>lambdaQuery()
                    .in(SystemUserPermission::getSystemUserId,
                        userResponseVOList.stream().map(SystemUserResponseVO::getId).collect(Collectors.toSet())));

        Set<String> uniqueIds = userPermissions.stream()
            .map(SystemUserPermission::getNamespaceId)
            .collect(Collectors.toSet());

        List<Namespace> namespaces = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(uniqueIds)) {
            namespaces = namespaceMapper.selectList(Wrappers.<Namespace>lambdaQuery()
                .select(Namespace::getId, Namespace::getUniqueId, Namespace::getName)
                .in(Namespace::getUniqueId, uniqueIds));
        }

        Map<String, String> namespaceMap = namespaces.stream().collect(Collectors.toMap(Namespace::getUniqueId, Namespace::getName));

        Map<Long, List<SystemUserPermission>> userPermissionsMap = userPermissions.stream()
                .collect(Collectors.groupingBy(SystemUserPermission::getSystemUserId));
        userResponseVOList.stream()
                .filter(systemUserResponseVO -> systemUserResponseVO.getRole().equals(RoleEnum.USER.getRoleId()))
                .forEach(systemUserResponseVO -> {
                    List<SystemUserPermission> userPermissions1 = userPermissionsMap.getOrDefault(systemUserResponseVO.getId(), Lists.newArrayList());

                    List<PermissionsResponseVO> permissionsResponseVOS = Lists.newArrayList();
                    for (SystemUserPermission systemUserPermission : userPermissions1) {
                        PermissionsResponseVO responseVO = new PermissionsResponseVO();
                        responseVO.setGroupName(systemUserPermission.getGroupName());
                        responseVO.setNamespaceId(systemUserPermission.getNamespaceId());
                        responseVO.setNamespaceName(namespaceMap.get(systemUserPermission.getNamespaceId()));
                        permissionsResponseVOS.add(responseVO);
                    }

                systemUserResponseVO.setPermissions(permissionsResponseVOS);
            });

        return new PageResult<>(userPageDTO, userResponseVOList);
    }

    @Override
    public SystemUserResponseVO getSystemUserByUserName(String username) {
        SystemUser systemUser = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, username));
        if (Objects.isNull(systemUser)) {
            throw new SnailJobServerException("用户不存在");
        }

        SystemUserResponseVO responseVO = SystemUserResponseVOConverter.INSTANCE.convert(systemUser);
        getPermission(systemUser.getRole(), systemUser.getId(), responseVO);

        List<SystemUserPermission> systemUserPermissions = systemUserPermissionMapper.selectList(
                new LambdaQueryWrapper<SystemUserPermission>()
                        .select(SystemUserPermission::getNamespaceId, SystemUserPermission::getGroupName)
                        .eq(SystemUserPermission::getSystemUserId, responseVO.getId()));
        responseVO.setPermissions(PermissionsResponseVOConverter.INSTANCE.toPermissionsResponseVOs(systemUserPermissions));


        return responseVO;
    }

    @Override
    public List<PermissionsResponseVO> getSystemUserPermissionByUserName(Long id) {

        List<SystemUserPermission> systemUserPermissions = systemUserPermissionMapper.selectList(
                new LambdaQueryWrapper<SystemUserPermission>()
                        .select(SystemUserPermission::getNamespaceId, SystemUserPermission::getGroupName)
                        .eq(SystemUserPermission::getSystemUserId, id));

        if (CollectionUtils.isEmpty(systemUserPermissions)) {
            return Lists.newArrayList();
        }

        Map<String, List<SystemUserPermission>> permissionsMap = systemUserPermissions.stream().collect(Collectors.groupingBy(SystemUserPermission::getNamespaceId));

        LambdaQueryWrapper<Namespace> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Namespace::getId, Namespace::getUniqueId, Namespace::getName);
        queryWrapper.in(Namespace::getUniqueId, permissionsMap.keySet());
        List<Namespace> namespaces = namespaceMapper.selectList(queryWrapper);
        Map<String, String> map = namespaces.stream().collect(Collectors.toMap(Namespace::getUniqueId, Namespace::getName));

        List<PermissionsResponseVO> response = new ArrayList<>();
        permissionsMap.forEach((namespaceId, values) -> {
            PermissionsResponseVO responseVO = new PermissionsResponseVO();
            responseVO.setNamespaceName(map.get(namespaceId));
            responseVO.setNamespaceId(namespaceId);
            responseVO.setGroupNames(values.stream().map(SystemUserPermission::getGroupName).collect(Collectors.toSet()));
            response.add(responseVO);
        });

        return response;
    }

    @Override
    @Transactional
    public boolean delUser(final Long id) {
        systemUserPermissionMapper.delete(
                new LambdaQueryWrapper<SystemUserPermission>()
                        .eq(SystemUserPermission::getSystemUserId, id)
        );
        return 1 == systemUserMapper.deleteById(id);
    }

    /**
     * 生成Token
     */
    private String getToken(SystemUser systemUser) {
        String sign = systemUser.getPassword();
        return JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .withAudience(JsonUtil.toJsonString(SystemUserResponseVOConverter.INSTANCE.convert(systemUser)))
                .sign(Algorithm.HMAC256(sign));
    }
}

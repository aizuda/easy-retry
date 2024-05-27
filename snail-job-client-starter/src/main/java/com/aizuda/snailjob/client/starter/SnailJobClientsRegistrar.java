package com.aizuda.snailjob.client.starter;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * Snail Job 客户端注册器
 *
 * @author: opensnail
 * @date : 2022-03-04 18:44
 */
public class SnailJobClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private StandardEnvironment standardEnvironment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableSnailJob.class.getName());
        Map<String, Object> systemEnvironment = standardEnvironment.getSystemProperties();
        systemEnvironment.put("snail-job.aop.order", attrs.get("order"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        StandardEnvironment standardEnvironment = (StandardEnvironment) environment;
        this.standardEnvironment = standardEnvironment;
        Map<String, Object> systemEnvironment = standardEnvironment.getSystemProperties();
        // 添加了 EnableSnailJob 默认就是开启，无需手动配置
        systemEnvironment.put("snail-job.enabled", true);
    }
}

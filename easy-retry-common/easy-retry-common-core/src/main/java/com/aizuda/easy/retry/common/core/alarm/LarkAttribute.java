package com.aizuda.easy.retry.common.core.alarm;

import lombok.Data;

import java.util.List;

/**
 * 飞书
 *
 * @author: www.byteblogs.com
 * @date : 2023-05-31 13:45
 * @since 1.4.0
 */
@Data
public class LarkAttribute {

    private String webhookUrl;

    private List<String> ats;

}

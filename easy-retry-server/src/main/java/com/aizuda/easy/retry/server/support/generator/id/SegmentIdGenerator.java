package com.aizuda.easy.retry.server.support.generator.id;

import com.aizuda.easy.retry.common.core.enums.IdGeneratorMode;
import com.aizuda.easy.retry.server.support.generator.IdGenerator;
import org.springframework.stereotype.Component;

/**
 * 号段模式
 *
 * @author www.byteblogs.com
 * @date 2023-05-04
 * @since 2.0
 */
@Component
public class SegmentIdGenerator implements IdGenerator {

    @Override
    public boolean supports(int mode) {
        return IdGeneratorMode.SEGMENT.getMode() == mode;
    }

    @Override
    public String idGenerator(String group) {
        return null;
    }
}

package com.aizuda.snailjob.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aizuda.snailjob.common.core.constant.SystemConstants;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author: opensnail
 * @date : 2021-11-25 09:54
 */
@Slf4j
public class DingDingUtils {

    public static final String atLabel = "@{0}";

    /**
     * 组装OapiRobotSendRequest
     *
     * @param title
     * @param text
     * @return
     */
    public static OapiRobotSendRequest buildSendRequest(String title, String text, List<String> ats) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        // 防止文本过长钉钉限流，目前最大为4000
        markdown.setText(StrUtil.sub(getAtText(ats, text, atLabel), 0, 4000));
        request.setMarkdown(markdown);

        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(ats);
        request.setAt(at);
        if (CollUtil.isNotEmpty(ats)) {
            at.setIsAtAll(ats.stream().map(String::toLowerCase).anyMatch(SystemConstants.AT_ALL::equals));
        }
        return request;
    }

    public static String getAtText(List<String> ats, String text, String atLabel) {
        if (CollUtil.isEmpty(ats)) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        ats.stream().filter(StrUtil::isNotBlank)
                .forEach(at -> sb.append(MessageFormat.format(atLabel, at)));
        return sb.toString();
    }

    /**
     * @param request
     */
    public static boolean sendMessage(OapiRobotSendRequest request, String url) {

        try {
            if (StrUtil.isBlank(url)) {
                return false;
            }

            DingTalkClient client = new DefaultDingTalkClient(url);
            client.execute(request);

            return true;
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("dingDingProcessNotify", e);
        }

        return false;
    }

}

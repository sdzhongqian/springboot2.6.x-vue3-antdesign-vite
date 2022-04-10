package com.qingfeng.framework.configure;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.qingfeng.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @ProjectName P6spySqlFormatConfigure
 * @author Administrator
 * @version 1.0.0
 * @Description 打印SQL
 * @createTime 2022/1/11 16:13
 */
public class P6spySqlFormatConfigure implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return StringUtils.isNotBlank(sql) ? DateTimeUtil.getDateTimeStr()
                + " | 耗时 " + elapsed + " ms | SQL 语句：" + StringUtils.LF + sql.replaceAll("[\\s]+", StringUtils.SPACE) + ";" : StringUtils.EMPTY;
    }
}
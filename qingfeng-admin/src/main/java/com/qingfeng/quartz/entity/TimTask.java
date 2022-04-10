package com.qingfeng.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @ProjectName TimTask
 * @author Administrator
 * @version 1.0.0
 * @Description 定时器
 * @createTime 2022/3/19 0019 8:52
 */
@Data
public class TimTask implements Serializable {

    private static final long serialVersionUID = -4352868070794165001L;

    /**
     * sched_name
     */
    @TableField(exist = false)
    private String sched_name;

    /**
     * 任务名称
     */
    @TableField(exist = false)
    private String job_name;

    /**
     * 任务分组
     */
    @TableField(exist = false)
    private String job_group;

    /**
     * 任务描述
     */
    @TableField(exist = false)
    private String description;
    /**
     * 执行类
     */
    @TableField(exist = false)
    private String job_class_name;
    /**
     * 执行表达式
     */
    @TableField(exist = false)
    private String cron_expression;
    /**
     * trigger_name
     */
    @TableField(exist = false)
    private String trigger_name;
    /**
     * 执行状态
     */
    @TableField(exist = false)
    private String trigger_state;
    /**
     * old_job_name
     */
    @TableField(exist = false)
    private String old_job_name;
    /**
     * old_job_group
     */
    @TableField(exist = false)
    private String old_job_group;

    @TableField(exist = false)
    private String start_time;

}
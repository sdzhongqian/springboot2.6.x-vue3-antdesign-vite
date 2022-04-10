package com.qingfeng.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.Serializable;

/**
 * @ProjectName TestJob
 * @author Administrator
 * @version 1.0.0
 * @Description 实现序列化接口、防止重启应用出现quartz Couldn't retrieve job because a required class was not found 的问题
 * @createTime 2022/3/19 0019 8:50
 */
public class TestJob implements  Job,Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//可注入Service 执行相关业务操作
		System.out.println("任务执行成功");
	}
}

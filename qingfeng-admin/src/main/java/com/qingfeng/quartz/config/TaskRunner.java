package com.qingfeng.quartz.config;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @ProjectName TaskRunner
 * @author Administrator
 * @version 1.0.0
 * @Description 初始化一个测试Demo任务
 * @createTime 2022/3/19 0019 8:49
 */
@Component
public class TaskRunner implements ApplicationRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

	@Autowired
	@Qualifier("Scheduler")
    private Scheduler scheduler;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void run(ApplicationArguments var) throws Exception{
		System.out.println("TaskRunner#####:定时器");
	}

}
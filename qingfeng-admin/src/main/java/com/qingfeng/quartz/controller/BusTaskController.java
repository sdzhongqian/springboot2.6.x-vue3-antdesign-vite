package com.qingfeng.quartz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.quartz.entity.Bustask;
import com.qingfeng.quartz.entity.QuartzEntity;
import com.qingfeng.quartz.service.IBusTaskService;
import com.qingfeng.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @ProjectName BusTaskController
 * @author Administrator
 * @version 1.0.0
 * @Description 业务任务
 * @createTime 2022/3/19 0019 8:55
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/quartz/busTask")
public class BusTaskController extends BaseController {

	@Autowired
	private IBusTaskService busTaskService;
	@Autowired @Qualifier("Scheduler")
	private Scheduler scheduler;

	/**
	 * @title listPage
	 * @description 查询数据列表
	 * @author Administrator
	 * @updateTime 2022/3/19 0019 9:02
	 */
	@GetMapping("/listPage")
	@PreAuthorize("hasAnyAuthority('busTask:info')")
	public MyResponse listPage(QueryRequest queryRequest, Bustask bustask) {
		String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
		//处理数据权限
		String user_id = userParams.split(":")[1];
		Map<String, Object> dataTable = MyUtil.getDataTable(busTaskService.findListPage(bustask, queryRequest));
		return new MyResponse().data(dataTable);
	}

	/**
	 * @title save
	 * @description 保存信息
	 * @author Administrator
	 * @updateTime 2022/3/19 0019 9:02
	 */
	@PostMapping
	@PreAuthorize("hasAnyAuthority('busTask:add')")
	public void save(@Valid @RequestBody Bustask bustask,HttpServletResponse response) throws Exception {
		Json json = new Json();
		try {
			// 创建用户
			String id = GuidUtil.getUuid();
			bustask.setId(id);
			String time = DateTimeUtil.getDateTimeStr();
			bustask.setCreate_time(time);
			bustask.setTrigger_time(time);
			//处理用户数据
			String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
			String user_id = authParams.split(":")[1];
			String organize_id = authParams.split(":")[2];
			bustask.setCreate_user(user_id);
			bustask.setCreate_organize(organize_id);
			bustask.setJob_group(organize_id);
			bustask.setJob_class_name("com.qingfeng.quartz.job.MessageJob");
			bustask.setTrigger_state("Y");

			QueryWrapper queryWrapper = new QueryWrapper();
			queryWrapper.eq("job_name",bustask.getJob_name());
			queryWrapper.eq("job_group",bustask.getJob_group());
			Bustask btask = busTaskService.getOne(queryWrapper);

			if(Verify.verifyIsNotNull(btask)){
				json.setSuccess(false);
				json.setMsg("操作失败，标题已存在。");
			}else{
				busTaskService.save(bustask);
				try {
					QuartzEntity quartz = new QuartzEntity();
					quartz.setJobName(bustask.getJob_name());
					quartz.setJobGroup(bustask.getJob_group());
					quartz.setDescription(bustask.getDescription()+"#"+bustask.getNotice_user());
					quartz.setCronExpression(bustask.getCron_expression());
					quartz.setJobClassName(bustask.getJob_class_name());

					//获取Scheduler实例、废弃、使用自动注入的scheduler、否则spring的service将无法注入
					//Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
					//如果是修改  展示旧的 任务
					if(quartz.getOldJobGroup()!=null){
						JobKey key = new JobKey(quartz.getOldJobName(),quartz.getOldJobGroup());
						scheduler.deleteJob(key);
					}
					Class cls = Class.forName(quartz.getJobClassName()) ;
					cls.newInstance();
					//构建job信息
					JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
							quartz.getJobGroup())
							.withDescription(quartz.getDescription()).build();
					// 触发时间点
					CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
					Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger"+quartz.getJobName(), quartz.getJobGroup())
							.startNow().withSchedule(cronScheduleBuilder).build();
					//交由Scheduler安排触发
					scheduler.scheduleJob(job, trigger);

					json.setSuccess(true);
					json.setMsg("操作成功。");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			String message = "新增信息失败";
			log.error(message, e);
			throw new BizException(message);
		}
		this.writeJson(response,json);
	}

	/**
	 * @title update
	 * @description 更新信息
	 * @author Administrator
	 * @updateTime 2022/3/19 0019 9:02
	 */
	@PutMapping
	@PreAuthorize("hasAnyAuthority('busTask:edit')")
	public void update(@Valid @RequestBody Bustask bustask,HttpServletResponse response) throws Exception {
		Json json = new Json();
		try {
			//处理用户数据
			String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
			String user_id = authParams.split(":")[1];
			String organize_id = authParams.split(":")[2];

			String time = DateTimeUtil.getDateTimeStr();
			bustask.setUpdate_time(time);
			bustask.setTrigger_time(time);
			bustask.setUpdate_user(user_id);
			bustask.setTrigger_state("Y");
			bustask.setJob_group(organize_id);
			bustask.setJob_class_name("com.qingfeng.quartz.job.MessageJob");

			QueryWrapper queryWrapper = new QueryWrapper();
			queryWrapper.eq("job_name",bustask.getJob_name());
			queryWrapper.eq("job_group",bustask.getJob_group());
			queryWrapper.ne("id",bustask.getId());
			Bustask btask = busTaskService.getOne(queryWrapper);
			if(Verify.verifyIsNotNull(btask)){
				json.setSuccess(false);
				json.setMsg("操作失败，标题已存在。");
			}else{
				busTaskService.updateById(bustask);
				try {
					QuartzEntity quartz = new QuartzEntity();
					quartz.setJobName(bustask.getJob_name());
					quartz.setJobGroup(bustask.getJob_group());
					quartz.setDescription(bustask.getDescription()+"#"+bustask.getNotice_user());
					quartz.setCronExpression(bustask.getCron_expression());
					quartz.setJobClassName(bustask.getJob_class_name());
					if(Verify.verifyIsNotNull(bustask.getOldJobName())){
						quartz.setOldJobName(bustask.getOldJobName());
						quartz.setOldJobGroup(bustask.getOldJobGroup());
					}
					//获取Scheduler实例、废弃、使用自动注入的scheduler、否则spring的service将无法注入
					//Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
					//如果是修改  展示旧的 任务
					if(quartz.getOldJobGroup()!=null){
						JobKey key = new JobKey(quartz.getOldJobName(),quartz.getOldJobGroup());
						scheduler.deleteJob(key);
					}
					Class cls = Class.forName(quartz.getJobClassName()) ;
					cls.newInstance();
					//构建job信息
					JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
							quartz.getJobGroup())
							.withDescription(quartz.getDescription()).build();
					// 触发时间点
					CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
					Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger"+quartz.getJobName(), quartz.getJobGroup())
							.startNow().withSchedule(cronScheduleBuilder).build();
					//交由Scheduler安排触发
					scheduler.scheduleJob(job, trigger);
					json.setSuccess(true);
					json.setMsg("操作成功。");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			String message = "修改用户失败";
			log.error(message, e);
			throw new BizException(message);
		}
		this.writeJson(response,json);
	}

	/**
	 * @title del
	 * @description 删除信息
	 * @author Administrator
	 * @updateTime 2022/3/19 0019 9:02
	 */
	@DeleteMapping("/{param_ids}")
	@PreAuthorize("hasAnyAuthority('busTask:del')")
	public void del(@NotBlank(message = "{required}") @PathVariable String param_ids, HttpServletResponse response) throws Exception {
		Json json = new Json();
		try {
			String[] ids = param_ids.split(StringPool.COMMA);
			Collection<Bustask> list = busTaskService.listByIds(Arrays.asList(ids));
			for (Bustask bt:list) {

				TriggerKey triggerKey = TriggerKey.triggerKey(bt.getJob_name(), bt.getJob_group());
				// 停止触发器
				scheduler.pauseTrigger(triggerKey);
				// 移除触发器
				scheduler.unscheduleJob(triggerKey);
				// 删除任务
				scheduler.deleteJob(JobKey.jobKey(bt.getJob_name(), bt.getJob_group()));
			}
			busTaskService.removeByIds(Arrays.asList(ids));
			json.setSuccess(true);
			json.setMsg("删除信息成功");
		} catch (Exception e) {
			String message = "删除信息失败";
			json.setSuccess(false);
			json.setMsg(message);
			log.error(message, e);
			throw new BizException(message);
		}
		this.writeJson(response,json);
	}



	/**
	 * @title execution
	 * @description 执行
	 * @author Administrator
	 * @updateTime 2022/3/19 0019 9:03
	 */
	@GetMapping("/execution")
	@PreAuthorize("hasAnyAuthority('busTask:execution')")
	public void execution(@RequestParam String jobName, @RequestParam String jobGroup, HttpServletResponse response) throws Exception {
		Json json = new Json();
		try {
			JobKey key = new JobKey(jobName,jobGroup);
			scheduler.triggerJob(key);
			json.setSuccess(true);
			json.setMsg("执行成功");
		} catch (Exception e) {
			String message = "执行失败";
			json.setSuccess(false);
			json.setMsg(message);
			log.error(message, e);
			throw new BizException(message);
		}
		this.writeJson(response,json);
	}


	/**
	 * @title stopOrRestore
	 * @description 停止和恢复
	 * @author Administrator
	 * @updateTime 2022/3/19 0019 9:03
	 */
	@GetMapping("/stopOrRestore")
	@PreAuthorize("hasAnyAuthority('busTask:stopOrRestore')")
	public void stopOrRestore(Bustask bustask,HttpServletResponse response) throws Exception  {
		Json json = new Json();
		try {
			JobKey key = new JobKey(bustask.getJob_name(),bustask.getJob_group());
			if(bustask.getTrigger_state().equals("N")){
				scheduler.pauseJob(key);
				System.out.println("停止。。。。。");
			}else if(bustask.getTrigger_state().equals("Y")){
				scheduler.resumeJob(key);
				bustask.setTrigger_time(DateTimeUtil.getDateTimeStr());
			}
			bustask.setUpdate_time(DateTimeUtil.getDateTimeStr());
			busTaskService.updateById(bustask);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (SchedulerException e) {
			String message = "操作失败";
			json.setSuccess(false);
			json.setMsg(message);
			log.error(message, e);
			throw new BizException(message);
		}
		this.writeJson(response,json);
	}


}

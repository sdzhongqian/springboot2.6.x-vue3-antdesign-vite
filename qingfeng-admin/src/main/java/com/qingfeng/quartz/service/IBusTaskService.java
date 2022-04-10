package com.qingfeng.quartz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.quartz.entity.Bustask;

/**
 * @ProjectName IBusTaskService
 * @author Administrator
 * @version 1.0.0
 * @Description IBusTaskService
 * @createTime 2022/3/19 0019 8:54
 */
public interface IBusTaskService extends IService<Bustask> {

    IPage<Bustask> findListPage(Bustask bustask, QueryRequest request);

}
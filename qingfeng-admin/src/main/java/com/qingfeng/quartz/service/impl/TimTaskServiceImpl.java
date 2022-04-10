package com.qingfeng.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.quartz.mapper.TimTaskMapper;
import com.qingfeng.quartz.entity.TimTask;
import com.qingfeng.quartz.service.ITimTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @ProjectName TimTaskServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description 定时任务
 * @createTime 2022/3/19 0019 8:54
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TimTaskServiceImpl extends ServiceImpl<TimTaskMapper, TimTask> implements ITimTaskService {


    public IPage<TimTask> findListPage(TimTask timTask, QueryRequest request){
        Page<TimTask> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, timTask);
    }



}
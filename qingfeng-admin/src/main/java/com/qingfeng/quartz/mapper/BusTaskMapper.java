package com.qingfeng.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.quartz.entity.Bustask;
import org.apache.ibatis.annotations.Param;

/**
 * @ProjectName BusTaskMapper
 * @author Administrator
 * @version 1.0.0
 * @Description 业务任务Mapper类
 * @createTime 2022/3/19 0019 8:52
 */
public interface BusTaskMapper extends BaseMapper<Bustask> {

    IPage<Bustask> findListPage(Page page, @Param("obj") Bustask bustask);

}
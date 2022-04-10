package com.qingfeng.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.quartz.entity.TimTask;
import org.apache.ibatis.annotations.Param;

/**
 * @ProjectName TimTaskMapper
 * @author Administrator
 * @version 1.0.0
 * @Description TimTaskMapper
 * @createTime 2022/3/19 0019 8:53
 */
public interface TimTaskMapper extends BaseMapper<TimTask> {

    IPage<TimTask> findListPage(Page page, @Param("obj") TimTask timTask);


}
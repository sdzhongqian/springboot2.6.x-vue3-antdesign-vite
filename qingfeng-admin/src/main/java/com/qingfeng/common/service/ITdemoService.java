package com.qingfeng.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.common.entity.Tdemo;

import java.util.List;

/**
 * @ProjectName ITdemoService
 * @author Administrator
 * @version 1.0.0
 * @Description ITdemoService接口
 * @createTime 2022/1/19 0019 22:55
 */
public interface ITdemoService extends IService<Tdemo> {

    //查询数据分页列表
    IPage<Tdemo> findListPage(Tdemo tdemo, QueryRequest request);

    //查询数据列表
    List<Tdemo> findList(Tdemo tdemo);

}
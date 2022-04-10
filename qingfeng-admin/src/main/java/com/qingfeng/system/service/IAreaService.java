package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Area;

import java.util.List;

/**
 * @ProjectName IAreaService
 * @author Administrator
 * @version 1.0.0
 * @Description IAreaService接口
 * @createTime 2022/1/19 0019 23:50
 */
public interface IAreaService extends IService<Area> {

    //查询数据分页列表
    IPage<Area> findListPage(Area area, QueryRequest request);

    //查询数据列表
    List<Area> findList(Area area);

}
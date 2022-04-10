package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Area;
import com.qingfeng.system.mapper.AreaMapper;
import com.qingfeng.system.service.IAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName AreaServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description AreaServiceImpl接口
 * @createTime 2022/1/19 0019 23:37
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:38
     */
    public IPage<Area> findListPage(Area area, QueryRequest request){
        Page<Area> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, area);
    }

    /**
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:38
     */
    public List<Area> findList(Area area){
        return this.baseMapper.findList(area);
    }



}
package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Area;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName AreaMapper
 * @author Administrator
 * @version 1.0.0
 * @Description AreaMapper
 * @createTime 2022/1/19 0019 23:36
 */
public interface AreaMapper extends BaseMapper<Area> {

    //查询数据分页列表
    IPage<Area> findListPage(Page page, @Param("obj") Area area);

    //查询数据列表
    List<Area> findList(Area area);

}
package com.qingfeng.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.common.entity.Tdemo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName TdemoMapper
 * @author Administrator
 * @version 1.0.0
 * @Description TdemoMapper
 * @createTime 2022/1/19 0019 22:54
 */
public interface TdemoMapper extends BaseMapper<Tdemo> {

    //查询数据分页列表
    IPage<Tdemo> findListPage(Page page, @Param("obj") Tdemo tdemo);

    //查询数据列表
    List<Tdemo> findList(Tdemo tdemo);

}
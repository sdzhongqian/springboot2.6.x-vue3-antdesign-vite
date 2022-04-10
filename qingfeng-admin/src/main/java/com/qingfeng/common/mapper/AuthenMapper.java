package com.qingfeng.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.common.entity.Authen;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName AuthenMapper
 * @author Administrator
 * @version 1.0.0
 * @Description AuthenMapper
 * @createTime 2022/1/19 0019 22:54
 */
public interface AuthenMapper extends BaseMapper<Authen> {

    //查询数据分页列表
    IPage<Authen> findListPage(Page page, @Param("obj") Authen authen);

    //查询数据列表
    List<Authen> findList(Authen authen);

}
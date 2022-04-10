package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Role;
import com.qingfeng.utils.PageData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName RoleMapper
 * @author Administrator
 * @version 1.0.0
 * @Description 角色mapper
 * @createTime 2022/1/19 0019 21:55
 */
public interface RoleMapper extends BaseMapper<Role> {

    //查询数据分页列表
    IPage<Role> findListPage(Page page, @Param("obj") Role role);

    //查询数据列表
    List<Role> findSimpleList(PageData pd);

}
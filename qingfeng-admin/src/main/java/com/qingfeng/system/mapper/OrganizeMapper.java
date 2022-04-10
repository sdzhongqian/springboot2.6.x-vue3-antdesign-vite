package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Organize;
import com.qingfeng.system.entity.Role;
import com.qingfeng.utils.PageData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName OrganizeMapper
 * @author Administrator
 * @version 1.0.0
 * @Description 组织信息mapper
 * @createTime 2022/1/19 0019 21:40
 */
public interface OrganizeMapper extends BaseMapper<Organize> {

    //查询数据分页列表
    IPage<Organize> findListPage(Page page, @Param("obj") Organize organize);

    //查询组织列表
    List<Organize> findList(Organize organize);

    //查询组织角色列表
    List<Role> findOrganizeRoleList(PageData pd);

    //查询树形表格
    List<PageData> findTreeTableList(PageData pd);

    //删除组织角色
    void delOrganizeRole(PageData pd);


    /**
     * @title findOrganizeList
     * @description 查询组织列表-流程候选组
     * @author Administrator
     * @updateTime 2021/9/11 0011 10:13
     */
    List<PageData> findOrganizeList(PageData pd);


}
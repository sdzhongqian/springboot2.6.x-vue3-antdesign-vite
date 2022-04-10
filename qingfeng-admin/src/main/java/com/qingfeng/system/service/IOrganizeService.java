package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Organize;
import com.qingfeng.system.entity.Role;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IOrganizeService
 * @author Administrator
 * @version 1.0.0
 * @Description 组织信息service接口
 * @createTime 2022/1/19 0019 21:40
 */
public interface IOrganizeService extends IService<Organize> {

    //查询数据分页列表
    IPage<Organize> findListPage(Organize organize, QueryRequest request);

    //查询数据列表
    List<Organize> findList(Organize organize);

    //查询组织角色列表
    List<Role> findOrganizeRoleList(PageData pd);

    //查询数据表
    List<PageData> findTreeTableList(PageData pd);

    //更新权限
    void updateAuth(PageData pd);


    /**
     * @title findOrganizeList
     * @description 查询组织列表-流程候选组
     * @author Administrator
     * @updateTime 2021/9/11 0011 10:13
     */
    List<PageData> findOrganizeList(PageData pd);



}
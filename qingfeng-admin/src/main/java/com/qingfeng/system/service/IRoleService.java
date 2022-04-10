package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Role;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IRoleService
 * @author Administrator
 * @version 1.0.0
 * @Description IRoleService接口
 * @createTime 2022/1/19 0019 21:55
 */
public interface IRoleService extends IService<Role> {

    //查询数据分页列表
    IPage<Role> findListPage(Role role, QueryRequest request);

    //查询数据列表
    List<Role> findList(Role role);

    //查询数据列表
    List<Role> findSimpleList(PageData pd);

}
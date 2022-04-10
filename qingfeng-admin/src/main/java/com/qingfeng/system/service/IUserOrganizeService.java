package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IUserOrganizeService
 * @author Administrator
 * @version 1.0.0
 * @Description IUserOrganizeService接口
 * @createTime 2022/1/20 0020 0:18
 */
public interface IUserOrganizeService extends IService<UserOrganize> {

    //查询用户组织详情
    UserOrganize findUserOrganizeInfo(UserOrganize userOrganize);

    //查询数据分页列表
    IPage<UserOrganize> findListPage(UserOrganize userOrganize, QueryRequest request);

    //保存用户组织信息
    void saveUserOrganize(UserOrganize userOrganize);

    //更新用户组织信息
    void updateUserOrganize(UserOrganize userOrganize);

    //查询用户组织
    List<UserOrganize> findUserOrganize(PageData pd);

}
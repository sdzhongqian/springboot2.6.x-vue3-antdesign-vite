package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Group;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IGroupService
 * @author Administrator
 * @version 1.0.0
 * @Description IGroupService接口
 * @createTime 2022/1/19 0019 23:50
 */
public interface IGroupService extends IService<Group> {

    //查询数据分页列表
    IPage<Group> findListPage(Group group, QueryRequest request);

    //查询数据列表
    List<Group> findList(Group group);

    //保存信息
    void saveGroup(Group group);

    //更新信息
    void updateGroup(Group group);

    //查询组用户
    List<PageData> findGroupUser(PageData pd);

}
package com.qingfeng.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.system.entity.RoleMenu;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IRoleMenuService
 * @author Administrator
 * @version 1.0.0
 * @Description IRoleMenuService接口
 * @createTime 2022/1/20 0020 0:17
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    //查询数据分页列表
    List<PageData> findRoleMenuList(PageData pd);

    //查询数据列表
    void delRoleMenu(PageData pd);

}
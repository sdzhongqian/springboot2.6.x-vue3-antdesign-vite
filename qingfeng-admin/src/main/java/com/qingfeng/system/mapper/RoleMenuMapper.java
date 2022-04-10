package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingfeng.system.entity.RoleMenu;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName RoleMenuMapper
 * @author Administrator
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/1/20 0020 0:11
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    //查询角色菜单列表
    List<PageData> findRoleMenuList(PageData pd);

    //删除角色菜单
    void delRoleMenu(PageData pd);

}
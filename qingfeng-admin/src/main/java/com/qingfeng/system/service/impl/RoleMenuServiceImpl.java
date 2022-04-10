package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.system.entity.RoleMenu;
import com.qingfeng.system.mapper.RoleMenuMapper;
import com.qingfeng.system.service.IRoleMenuService;
import com.qingfeng.utils.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName RoleMenuServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description RoleMenuServiceImpl实现类
 * @createTime 2022/1/20 0020 0:19
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    /**
     * @title findRoleMenuList
     * @description 查询角色菜单
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:19
     */
    public List<PageData> findRoleMenuList(PageData pd){
        return this.baseMapper.findRoleMenuList(pd);
    }

    /**
     * @title delRoleMenu
     * @description 删除角色菜单
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:19
     */
    public void delRoleMenu(PageData pd){
        this.baseMapper.delRoleMenu(pd);
    }

}
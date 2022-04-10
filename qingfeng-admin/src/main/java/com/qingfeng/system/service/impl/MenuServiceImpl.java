package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Menu;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.mapper.MenuMapper;
import com.qingfeng.system.service.IMenuService;
import com.qingfeng.utils.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName MenuServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description MenuServiceImpl接口
 * @createTime 2022/1/19 0019 22:46
 */
@Slf4j
@Service("menuService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:33
     */
    public IPage<Menu> findListPage(Menu menu, QueryRequest request){
        Page<Role> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, menu);
    }


    /**
     * @title findAuthMenuList
     * @description 查询权限菜单列表
     * @author Administrator
     * @updateTime 2022/1/23 0023 11:32
     */
    public List<PageData> findAuthMenuList(PageData pd){
        return this.baseMapper.findUserPermissions(pd);
    }

    /**
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/23 0023 11:32
     */
    public List<PageData> findList(PageData pd){
        return this.baseMapper.findList(pd);
    }


}
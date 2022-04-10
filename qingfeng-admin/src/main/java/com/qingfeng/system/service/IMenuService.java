package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Menu;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IMenuService
 * @author Administrator
 * @version 1.0.0
 * @Description IMenuService接口
 * @createTime 2022/1/19 0019 22:45
 */
public interface IMenuService extends IService<Menu> {

    //查询数据分页列表
    IPage<Menu> findListPage(Menu menu, QueryRequest request);


    //根据权限查询菜单信息
    List<PageData> findAuthMenuList(PageData pd);

    //查询菜单列表
    List<PageData> findList(PageData pd);

}
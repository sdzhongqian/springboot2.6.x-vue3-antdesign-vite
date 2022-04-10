package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Menu;
import com.qingfeng.utils.PageData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName MenuMapper
 * @author Administrator
 * @version 1.0.0
 * @Description 菜单mapper
 * @createTime 2022/1/19 0019 22:45
 */
public interface MenuMapper extends BaseMapper<Menu> {

    //查询数据分页列表
    IPage<Menu> findListPage(Page page, @Param("obj") Menu menu);

    //根据权限查询菜单信息
    List<PageData> findUserPermissions(PageData pd);

    //查询菜单列表
    List<PageData> findList(PageData pd);

}
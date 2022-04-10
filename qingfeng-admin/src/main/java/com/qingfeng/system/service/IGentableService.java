package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Gentable;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IGentableService
 * @author Administrator
 * @version 1.0.0
 * @Description IGentableService
 * @createTime 2022/3/20 0020 9:11
 */
public interface IGentableService extends IService<Gentable> {

    //查询数据表分页
    List<PageData> findTableListPage(PageData pd);

    //查询数据表数量
    Integer findTableListNum(PageData pd);

    //查询数据表列表
    List<PageData> findTableList(PageData pd);

    //保存数据表
    void saveTable(PageData pd);

    //查询数据分页列表
    IPage<Gentable> findListPage(Gentable gentable, QueryRequest request);

    //删除
    void del(String ids);

    //更新数据表
    void updateTable(Gentable gentable);

    //查询菜单详情
    PageData findMenuInfo(PageData pd);

}
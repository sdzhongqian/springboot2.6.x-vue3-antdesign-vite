package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Dictionary;

import java.util.List;

/**
 * @ProjectName IDictionaryService
 * @author Administrator
 * @version 1.0.0
 * @Description IDictionaryService接口
 * @createTime 2022/1/19 0019 22:55
 */
public interface IDictionaryService extends IService<Dictionary> {

    //查询数据分页列表
    IPage<Dictionary> findListPage(Dictionary dictionary, QueryRequest request);

    //查询数据列表
    List<Dictionary> findList(Dictionary dictionary);

}
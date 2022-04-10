package com.qingfeng.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.system.entity.Genfield;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName IGenfieldService
 * @author Administrator
 * @version 1.0.0
 * @Description IGenfieldService
 * @createTime 2022/3/20 0020 9:10
 */
public interface IGenfieldService extends IService<Genfield> {

    //查询字段列表
    List<Genfield> findFieldList(PageData pd);


}
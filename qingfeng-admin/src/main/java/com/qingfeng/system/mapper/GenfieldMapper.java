package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingfeng.system.entity.Genfield;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @ProjectName GenfieldMapper
 * @author Administrator
 * @version 1.0.0
 * @Description GenfieldMapper
 * @createTime 2022/3/20 0020 9:10
 */
public interface GenfieldMapper extends BaseMapper<Genfield> {

    //查询字段列表
    List<Genfield> findFieldList(PageData pd);

}
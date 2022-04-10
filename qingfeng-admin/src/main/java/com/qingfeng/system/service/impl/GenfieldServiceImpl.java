package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.system.entity.Genfield;
import com.qingfeng.system.mapper.GenfieldMapper;
import com.qingfeng.system.service.IGenfieldService;
import com.qingfeng.utils.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @ProjectName GenfieldServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description GenfieldServiceImpl
 * @createTime 2022/3/20 0020 9:11
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class GenfieldServiceImpl extends ServiceImpl<GenfieldMapper, Genfield> implements IGenfieldService {

    /**
     * @title findFieldList
     * @description 查询字段列表
     * @author qingfeng
     * @updateTime 2021/4/3 0003 20:01
     */
    public List<Genfield> findFieldList(PageData pd){
        return this.baseMapper.findFieldList(pd);
    }

}
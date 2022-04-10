package com.qingfeng.common.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.common.entity.Tdemo;
import com.qingfeng.common.mapper.TdemoMapper;
import com.qingfeng.common.service.ITdemoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName TdemoServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description TdemoServiceImpl接口
 * @createTime 2022/1/19 0019 22:55
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TdemoServiceImpl extends ServiceImpl<TdemoMapper, Tdemo> implements ITdemoService {


    /**
     * @ProjectName TdemoServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据分页列表
     * @createTime 2022/1/19 0019 22:55
     */
    public IPage<Tdemo> findListPage(Tdemo tdemo, QueryRequest request){
        Page<Tdemo> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, tdemo);
    }

    /**
     * @ProjectName TdemoServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据列表
     * @createTime 2022/1/19 0019 22:55
     */
    public List<Tdemo> findList(Tdemo tdemo){
        return this.baseMapper.findList(tdemo);
    }


}
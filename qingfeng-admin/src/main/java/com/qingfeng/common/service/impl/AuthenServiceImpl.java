package com.qingfeng.common.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.common.entity.Authen;
import com.qingfeng.common.mapper.AuthenMapper;
import com.qingfeng.common.service.IAuthenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName AuthenServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description AuthenServiceImpl接口
 * @createTime 2022/1/19 0019 22:55
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AuthenServiceImpl extends ServiceImpl<AuthenMapper, Authen> implements IAuthenService {


    /**
     * @ProjectName AuthenServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据分页列表
     * @createTime 2022/1/19 0019 22:55
     */
    public IPage<Authen> findListPage(Authen authen, QueryRequest request){
        Page<Authen> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, authen);
    }

    /**
     * @ProjectName AuthenServiceImpl
     * @author Administrator
     * @version 1.0.0
     * @Description 查询数据列表
     * @createTime 2022/1/19 0019 22:55
     */
    public List<Authen> findList(Authen authen){
        return this.baseMapper.findList(authen);
    }


}
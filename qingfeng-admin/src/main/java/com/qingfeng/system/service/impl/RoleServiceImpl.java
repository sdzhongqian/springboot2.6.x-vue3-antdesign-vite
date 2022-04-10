package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.mapper.RoleMapper;
import com.qingfeng.system.service.IRoleService;
import com.qingfeng.utils.PageData;
import com.qingfeng.utils.Verify;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName RoleServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description RoleServiceImpl实现类
 * @createTime 2022/1/19 0019 21:55
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:57
     */
    public IPage<Role> findListPage(Role role, QueryRequest request){
        Page<Role> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, role);
    }

    /**
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:57
     */
    public List<Role> findList(Role role){
        QueryWrapper queryWrapper = new QueryWrapper();
        if(Verify.verifyIsNotNull(role.getName())){
            queryWrapper.like("name",role.getName());
        }
        if(Verify.verifyIsNotNull(role.getShort_name())){
            queryWrapper.like("short_name",role.getShort_name());
        }
        if(Verify.verifyIsNotNull(role.getStatus())){
            queryWrapper.eq("status",role.getStatus());
        }
        return this.list(queryWrapper);
    }


    /**
     * @title findSimpleList
     * @description 查询数据列表
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:36
     */
    public List<Role> findSimpleList(PageData pd){
        return this.baseMapper.findSimpleList(pd);
    }


}
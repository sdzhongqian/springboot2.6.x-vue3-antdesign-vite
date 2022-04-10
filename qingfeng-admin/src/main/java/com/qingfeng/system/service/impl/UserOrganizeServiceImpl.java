package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.mapper.UserOrganizeMapper;
import com.qingfeng.system.service.IUserOrganizeService;
import com.qingfeng.utils.DateTimeUtil;
import com.qingfeng.utils.GuidUtil;
import com.qingfeng.utils.PageData;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName UserOrganizeServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description UserOrganizeServiceImpl实现类
 * @createTime 2022/1/20 0020 0:19
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserOrganizeServiceImpl extends ServiceImpl<UserOrganizeMapper, UserOrganize> implements IUserOrganizeService {

    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:20
     */
    @Override
    public IPage<UserOrganize> findListPage(UserOrganize userOrganize, QueryRequest request) {
        Page<UserOrganize> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, userOrganize);
    }

    /**
     * @title findUserOrganizeInfo
     * @description 查询用户组织信息
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:20
     */
    public UserOrganize findUserOrganizeInfo(UserOrganize userOrganize){
        return this.baseMapper.findUserOrganizeInfo(userOrganize);
    }

    /**
     * @title saveUserOrganize
     * @description 保存用户组织信息
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:20
     */
    public void saveUserOrganize(UserOrganize userOrganize){
        String id = GuidUtil.getUuid();
        String time = DateTimeUtil.getDateTimeStr();
        userOrganize.setId(id);
        userOrganize.setType("1");
        userOrganize.setUse_status("0");
        userOrganize.setOrder_by("1");
        userOrganize.setCreate_time(time);
        //处理数据权限
        String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
        userOrganize.setCreate_user(authParams.split(":")[1]);
        save(userOrganize);
    }

    /**
     * @title updateUserOrganize
     * @description 更新用户组织信息
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:20
     */
    public void updateUserOrganize(UserOrganize userOrganize){
        // 更新
        String time = DateTimeUtil.getDateTimeStr();
        userOrganize.setUpdate_time(time);
        updateById(userOrganize);
    }

    /**
     * @title findUserOrganize
     * @description 查询用户组织信息
     * @author Administrator
     * @updateTime 2022/1/20 0020 0:20
     */
    public List<UserOrganize> findUserOrganize(PageData pd){
        return this.baseMapper.findUserOrganize(pd);
    }


}
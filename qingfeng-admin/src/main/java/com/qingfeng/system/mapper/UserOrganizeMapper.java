package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.utils.PageData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName UserOrganizeMapper
 * @author Administrator
 * @version 1.0.0
 * @Description UserOrganizeMapper
 * @createTime 2022/1/20 0020 0:12
 */
public interface UserOrganizeMapper extends BaseMapper<UserOrganize> {

    //查询数据分页列表
    IPage<UserOrganize> findListPage(Page page, @Param("obj") UserOrganize userOrganize);

    //查询用户组织详情
    UserOrganize findUserOrganizeInfo(UserOrganize userOrganize);

    //查询用户组织信息
    List<UserOrganize> findUserOrganize(PageData pd);

}
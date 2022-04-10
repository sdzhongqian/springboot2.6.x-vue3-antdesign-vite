package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.entity.SystemUser;
import com.qingfeng.utils.PageData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月18日 21:56:00
 */
public interface UserMapper extends BaseMapper<SystemUser> {

    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:52
     */
    IPage<SystemUser> findListPage(Page page, @Param("systemUser") SystemUser systemUser);

    /**
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:52
     */
    List<SystemUser> findList(SystemUser systemUser);


    //查询用户详情
    PageData findUserInfo(PageData pd);

    //询用户角色信息
    List<Role> findUserRoleList(PageData pd);

    //查询用户组织信息
    PageData findUserOrganizeInfo(PageData pd);

    //删除用户角色
    void delUserRole(PageData pd);

    //更新用户组织状态
    void updateUserOrgUseStatus(PageData pd);



    /**
     * @title findUserList
     * @description 查询用户信息列表
     * @author Administrator
     * @updateTime 2021/9/11 0011 10:00
     */
    List<PageData> findUserList(PageData pd);

}

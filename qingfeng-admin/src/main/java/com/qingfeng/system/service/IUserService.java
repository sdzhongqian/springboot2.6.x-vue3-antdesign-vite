package com.qingfeng.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.entity.SystemUser;
import com.qingfeng.utils.PageData;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月18日 21:55:00
 */
public interface IUserService extends IService<SystemUser> {


    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:46
     */
    IPage<SystemUser> findListPage(SystemUser systemUser, QueryRequest request);

    /**
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:46
     */
    List<SystemUser> findList(SystemUser systemUser);

    /**
     * @title createUser
     * @description 新增用户
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:46
     */
    void createUser(SystemUser systemUser);

    /**
     * @title updateUser
     * @description 修改用户
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:46
     */
    void updateUser(SystemUser systemUser);

    /**
     * @title deleteUsers
     * @description 删除用户
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:46
     */
    void deleteUsers(String[] userIds);


    //查询用户详情
    PageData findUserInfo(PageData pd);

    //查询用户角色信息
    List<Role> findUserRoleList(PageData pd);

    //查询用户组织信息
    PageData findUserOrganizeInfo(PageData pd);

    //更新密码
    void updatePwd(SystemUser user);

    //更新权限
    void updateAuth(PageData pd);

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

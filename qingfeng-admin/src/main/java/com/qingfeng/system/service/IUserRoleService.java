package com.qingfeng.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.system.entity.UserRole;

/**
 * @ProjectName IUserRoleService
 * @author Administrator
 * @version 1.0.0
 * @Description IUserRoleService接口
 * @createTime 2022/1/20 0020 0:18
 */
public interface IUserRoleService extends IService<UserRole> {

    //根据角色id删除用户角色信息
    void deleteUserRolesByRoleId(String[] roleIds);

    //根据用户id删除用户角色信息
    void deleteUserRolesByUserId(String[] userIds);
}
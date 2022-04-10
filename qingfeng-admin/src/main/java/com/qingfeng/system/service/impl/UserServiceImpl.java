package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.common.entity.UploadFile;
import com.qingfeng.common.service.IUploadService;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.entity.SystemUser;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.entity.UserRole;
import com.qingfeng.system.mapper.UserMapper;
import com.qingfeng.system.service.IUserGroupService;
import com.qingfeng.system.service.IUserOrganizeService;
import com.qingfeng.system.service.IUserRoleService;
import com.qingfeng.system.service.IUserService;
import com.qingfeng.utils.*;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月18日 21:56:00
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, SystemUser> implements IUserService {

    @Autowired
    private IUserOrganizeService userOrganizeService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IUploadService uploadService;

    /**
     * @title findListPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:47
     */
    public IPage<SystemUser> findListPage(SystemUser systemUser, QueryRequest request) {
        Page<SystemUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.findListPage(page, systemUser);
    }

    /**
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:47
     */
    @Override
    public List<SystemUser> findList(SystemUser systemUser) {
        List<SystemUser> list = this.baseMapper.findList(systemUser);
        return list;
    }

    /**
     * @title createUser
     * @description 创建用户信息
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:51
     */
    @Transactional
    public void createUser(SystemUser systemUser) {
        // 创建用户
        String id = GuidUtil.getUuid();
        systemUser.setId(id);
        String time = DateTimeUtil.getDateTimeStr();
        systemUser.setCreate_time(time);
        systemUser.setStatus("0");
        systemUser.setType("1");
        systemUser.setPwd_error_num("0");
        systemUser.setLogin_password(passwordEncoder.encode(systemUser.getLogin_password()));
        //处理数据权限
        String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
        systemUser.setCreate_user(authParams.split(":")[1]);
        systemUser.setCreate_organize(authParams.split(":")[2]);
        save(systemUser);
        //保存用户组织信息
        UserOrganize userOrganize = new UserOrganize();
        userOrganize.setId(GuidUtil.getUuid());
        userOrganize.setUser_id(id);
        userOrganize.setType("0");
        userOrganize.setUse_status("0");
        userOrganize.setOrganize_id(systemUser.getOrganize_id());
        userOrganize.setOrganize_name(systemUser.getOrganize_name());
        userOrganize.setOrder_by("1");
        userOrganize.setCreate_user(authParams.split(":")[1]);
        userOrganize.setCreate_time(time);
        userOrganizeService.save(userOrganize);
        //处理附件
        if(Verify.verifyIsNotNull(systemUser.getFileIds())){
            UploadFile uploadFile = new UploadFile();
            uploadFile.setObj_id(id);
            uploadFile.setUpdate_time(time);
            String fileIds[] = systemUser.getFileIds().split(",");
            for (int j = 0; j < fileIds.length; j++) {
                uploadFile.setId(fileIds[j]);
                uploadService.updateById(uploadFile);
            }
        }
    }

    /**
     * @title updateUser
     * @description 更新信息
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:51
     */
    @Override
    @Transactional
    public void updateUser(SystemUser systemUser) {
        // 更新用户
        String time = DateTimeUtil.getDateTimeStr();
        systemUser.setUpdate_time(time);
        String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
        systemUser.setUpdate_user(authParams.split(":")[1]);
        updateById(systemUser);

        //处理附件
        if(Verify.verifyIsNotNull(systemUser.getFileIds())){
            UploadFile uploadFile = new UploadFile();
            uploadFile.setObj_id(systemUser.getId());
            uploadFile.setUpdate_time(time);
            String fileIds[] = systemUser.getFileIds().split(",");
            for (int j = 0; j < fileIds.length; j++) {
                uploadFile.setId(fileIds[j]);
                uploadService.updateById(uploadFile);
            }
        }
    }

    /**
     * @title deleteUsers
     * @description 删除用户信息
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:51
     */
    @Override
    @Transactional
    public void deleteUsers(String[] ids) {
        List<String> list = Arrays.asList(ids);
        removeByIds(list);
        //删除关联组织
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("user_id",ids);
        userOrganizeService.remove(queryWrapper);
        //删除组用户关联表
        userGroupService.remove(queryWrapper);
        //删除用户角色关联
        userRoleService.remove(queryWrapper);
    }



    /**
     * @title updatePwd
     * @description 更新密码
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:39
     */
    @Transactional
    public void updatePwd(SystemUser user) {
        String[] ids = user.getIds().split(",");
        List<SystemUser> list = new ArrayList<SystemUser>();
        for (String id:ids) {
            SystemUser u = new SystemUser();
            u.setId(id);
            u.setLogin_password(passwordEncoder.encode(user.getLogin_password()));
            u.setUpdate_time(DateTimeUtil.getDateTimeStr());
            list.add(u);
            System.out.println("##:"+ JsonToMap.bean2json(u));
        }
        updateBatchById(list);
    }



    /**
     * 查询用户详情
     * @param pd
     * @return
     */
    public PageData findUserInfo(PageData pd){
        return this.baseMapper.findUserInfo(pd);
    }

    /**
     * 查询用户角色信息
     * @param pd
     * @return
     */
    public List<Role> findUserRoleList(PageData pd){
        return this.baseMapper.findUserRoleList(pd);
    }

    /**
     * 查询用户组织信息
     * @param pd
     * @return
     */
    public PageData findUserOrganizeInfo(PageData pd){
        return this.baseMapper.findUserOrganizeInfo(pd);
    }

    /**
     * @title updateAuth
     * @description 更新权限
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:40
     */
    public void updateAuth(PageData pd){
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        //处理数据权限
        String create_user = userParams.split(":")[1];
        String time = DateTimeUtil.getDateTimeStr();
        String[] role_ids = pd.get("role_ids").toString().split(",");
        //删除用户角色表。
        pd.put("user_id",pd.get("id"));
//        pd.put("role_ids", Arrays.asList(role_ids));
        this.baseMapper.delUserRole(pd);
        if(Verify.verifyIsNotNull(pd.get("role_ids"))){
            String user_id = pd.get("id").toString();
            List<UserRole> list = new ArrayList<UserRole>();
            //执行保存
            for (int i = 0; i < role_ids.length; i++) {
                UserRole userRole = new UserRole();
                //主键id
                userRole.setId(GuidUtil.getUuid());
                userRole.setRole_id(role_ids[i]);
                userRole.setUser_id(user_id);
                userRole.setCreate_time(time);
                userRole.setCreate_user(create_user);
                list.add(userRole);
            }
            userRoleService.saveBatch(list);
        }

        //处理数据权限
        UserOrganize userOrganize = new UserOrganize();
        String[] showAuthData = pd.get("showAuthData").toString().split(",");
        String[] operaAuthData = pd.get("operaAuthData").toString().split(",");
        StringBuilder authOrgIds = new StringBuilder();
        StringBuilder authParams = new StringBuilder();
        if(Verify.verifyIsNotNull(pd.get("showAuthData"))){
            for (int i = 0; i < showAuthData.length; i++) {
                authOrgIds.append(showAuthData[i]).append(",");
                if(ArrayUtils.contains(operaAuthData,showAuthData[i])){
                    authParams.append(showAuthData[i]).append(":Y").append(",");
                }else{
                    authParams.append(showAuthData[i]).append(":N").append(",");
                }
            }
            if(authOrgIds.length()>0){
                userOrganize.setAuthOrgIds(authOrgIds.substring(0,authOrgIds.length()-1));
                userOrganize.setAuthParams(authParams.substring(0,authParams.length()-1));
            }
        }
        userOrganize.setUpdate_time(time);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",pd.get("user_id").toString());
        queryWrapper.eq("organize_id",pd.get("organize_id").toString());
        userOrganizeService.update(userOrganize,queryWrapper);
    }

    /**
     * @title updateUserOrgUseStatus
     * @description 更新用户组织状态
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:40
     */
    public void updateUserOrgUseStatus(PageData pd){
        this.baseMapper.updateUserOrgUseStatus(pd);
    }


    /**
     * @title findUserList
     * @description 查询用户信息列表
     * @author Administrator
     * @updateTime 2021/9/11 0011 10:00
     */
    public List<PageData> findUserList(PageData pd){
        return this.baseMapper.findUserList(pd);
    }



}

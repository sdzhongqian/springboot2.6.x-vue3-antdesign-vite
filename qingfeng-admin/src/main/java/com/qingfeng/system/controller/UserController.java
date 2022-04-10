package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.common.entity.UploadFile;
import com.qingfeng.common.service.IUploadService;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Organize;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.entity.SystemUser;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.*;
import com.qingfeng.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月18日 21:55:00
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IOrganizeService organizeService;
    @Autowired
    public IUploadService uploadService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserOrganizeService userOrganizeService;

    /**
     * @title listPage
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:29
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('user:info')")
    public MyResponse listPage(QueryRequest queryRequest, SystemUser user) {
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        //处理数据权限
        String user_id = userParams.split(":")[1];
        UserOrganize uoParam = new UserOrganize();
        uoParam.setUser_id(user_id);
        UserOrganize userOrganize = userOrganizeService.findUserOrganizeInfo(uoParam);
        List<String> auth_organize_ids = new ArrayList<String>();
        if(Verify.verifyIsNotNull(userOrganize)){
            if(Verify.verifyIsNotNull(userOrganize.getAuthOrgIds())){
                auth_organize_ids = Arrays.asList(userOrganize.getAuthOrgIds().split(","));
            }
        }
        user.setAuth_user(user_id);
        user.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(userService.findListPage(user, queryRequest));
        return new MyResponse().data(dataTable);
    }

    /**
     * @title list
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:44
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('user:info')")
    public MyResponse list(SystemUser systemUser) {
        List<SystemUser> list = userService.findList(systemUser);
        return new MyResponse().data(list);
    }

    /**
     * @title save
     * @description 保存数据
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:44
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('user:add')")
    public MyResponse save(@Valid @RequestBody SystemUser systemUser) throws BizException {
        Json json = new Json();
        try {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("login_name", systemUser.getLogin_name());
            Collection list = userService.listByMap(columnMap);
            if(list.size()==0){
                this.userService.createUser(systemUser);
                json.setSuccess(true);
            }else{
                json.setSuccess(false);
                json.setMsg("登录名称【"+ systemUser.getLogin_name()+"】已经存在！");
            }
        } catch (Exception e) {
            String message = "新增用户失败";
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }

    /**
     * @title update
     * @description 更新数据
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:44
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('user:edit')")
    public MyResponse update(@Valid @RequestBody SystemUser systemUser) throws BizException {
        Json json = new Json();
        try {
            boolean bol = true;
            if(!systemUser.getOld_login_name().equals(systemUser.getLogin_name())){
                //判断登录用户名称是否存在
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("login_name", systemUser.getLogin_name());
                Collection list = userService.listByMap(columnMap);
                if(list.size()>0){
                    json.setSuccess(false);
                    json.setMsg("登录名称【"+ systemUser.getLogin_name()+"】已经存在！");
                    bol = false;
                }
            }
            if(bol){
                json.setSuccess(true);
                this.userService.updateUser(systemUser);
            }
        } catch (Exception e) {
            String message = "修改用户失败";
            json.setSuccess(false);
            log.error(message, e);
            e.printStackTrace();
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }

    /**
     * @title del
     * @description 删除数据
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:44
     */
    @DeleteMapping("/{userIds}")
    @PreAuthorize("hasAnyAuthority('user:del')")
    public MyResponse del(@NotBlank(message = "{required}") @PathVariable String userIds, HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] ids = userIds.split(StringPool.COMMA);
            this.userService.deleteUsers(ids);
            json.setSuccess(true);
            json.setMsg("删除信息成功");
        } catch (Exception e) {
            String message = "删除信息失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }

    /**
     * @title deleteUsers
     * @description 更新状态
     * @author Administrator
     * @updateTime 2022/1/18 0018 22:44
     */
    @PostMapping("/updateStatus")
    public MyResponse updateStatus(@Valid @RequestBody SystemUser systemUser) throws BizException {
        Json json = new Json();
        try {
            this.userService.updateById(systemUser);
            json.setSuccess(true);
        } catch (Exception e) {
            String message = "状态修改失败";
            json.setMsg(message);
            json.setSuccess(false);
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }


    /**
     * @title findUserInfo
     * @description 查询用户细腻详情
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:17
     */
    @GetMapping("/findUserInfo")
    public void findUserInfo(@RequestHeader HttpHeaders headers, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        PageData pd = new PageData(request);
        String[] tokens = SecurityContextHolder.getContext().getAuthentication().getName().split(":");
        pd.put("user_id",tokens[1]);
        String organize_id = tokens[2];
        PageData uPd = userService.findUserInfo(pd);
        PageData rolePd = new PageData();
        //查询当前用户拥有的角色信息
        pd.put("organize_id",organize_id);
        List<Role> userRoles = userService.findUserRoleList(pd);
        rolePd.put("permissions",userRoles);
        //查询当前组织信息
        PageData orgPd = userService.findUserOrganizeInfo(pd);
        uPd.put("role",rolePd);
        uPd.put("orgPd",orgPd);
        //查询当前用户拥有的权限信息
        List<PageData> list = menuService.findAuthMenuList(pd);
        uPd.put("authList",list);
        //返回用户信息 用于辅助查询（文档下载）
        uPd.put("authName",SecurityContextHolder.getContext().getAuthentication().getName());
        Json json = new Json();
        json.setMsg("获取数据成功。");
        json.setCode(200);
        json.setData(uPd);
        json.setSuccess(true);
        this.writeJson(response,json);
    }


    /**
     * @title updatePwd
     * @description 更新密码
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:18
     */
    @GetMapping("/updatePwd")
    @PreAuthorize("hasAnyAuthority('user:resetPwd')")
    public MyResponse updatePwd(SystemUser user) throws BizException {
        Json json = new Json();
        try {
            userService.updatePwd(user);
            json.setSuccess(true);
        } catch (Exception e) {
            String message = "状态修改失败";
            json.setMsg(message);
            json.setSuccess(false);
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }

    /**
     * @title findUserOrganizeListPage
     * @description 查询用户组织分页信息
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:18
     */
    @GetMapping("/findUserOrganizeListPage")
    @PreAuthorize("hasAnyAuthority('user:info')")
    public MyResponse findUserOrganizeListPage(QueryRequest queryRequest,UserOrganize userOrganize) {
        //处理数据权限
        Map<String, Object> dataTable = MyUtil.getDataTable(userOrganizeService.findListPage(userOrganize, queryRequest));
        return new MyResponse().data(dataTable);
    }


    /**
     * @title saveOrUpdateUserOrganize
     * @description 保存或更新用户组织
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:18
     */
    @PostMapping("/saveOrUpdateUserOrganize")
    public void saveOrUpdateUserOrganize(@Valid @RequestBody UserOrganize userOrganize,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            if(Verify.verifyIsNotNull(userOrganize.getId())){
                userOrganizeService.updateUserOrganize(userOrganize);
            }else{
                userOrganizeService.saveUserOrganize(userOrganize);
            }
            json.setSuccess(true);
            json.setMsg("更新权限信息成功");
        } catch (Exception e) {
            String message = "更新权限信息失败";
            e.printStackTrace();
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }


    /**
     * @title delUserOrganize
     * @description 删除用户组织
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:19
     */
    @DeleteMapping("/delUserOrganize/{id}")
    @PreAuthorize("hasAnyAuthority('user:del')")
    public void delUserOrganize(@NotBlank(message = "{required}") @PathVariable String id,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            userOrganizeService.removeById(id);
            json.setSuccess(true);
            json.setMsg("删除信息成功");
        } catch (Exception e) {
            String message = "删除信息失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title findRoleAuth
     * @description 查询角色权限
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:19
     */
    @PostMapping("/findRoleAuth")
    public MyResponse findRoleAuth(@RequestBody PageData pd) throws BizException {
        pd.put("user_id",pd.get("id"));
        List<Role> roleLs = roleService.findSimpleList(pd);
        List<Role> myRoleLs = userService.findUserRoleList(pd);
        List<UserOrganize> orgList = userOrganizeService.findUserOrganize(pd);
        pd.put("roleLs",roleLs);
        pd.put("myRoleLs",myRoleLs);
        pd.put("orgList",orgList);
        return new MyResponse().data(pd);
    }

    /**
     * @title findOrganizeAuth
     * @description 查询组织权限
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:19
     */
    @PostMapping("/findOrganizeAuth")
    public MyResponse findOrganizeAuth(@RequestBody PageData pd) throws BizException {
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        //处理数据权限
        String user_id = userParams.split(":")[1];
        String organize_id = userParams.split(":")[2];
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",user_id);
        SystemUser user = userService.getOne(queryWrapper);

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("id",organize_id);
        Organize organize = organizeService.getOne(queryWrapper1);
        if (user.getType().equals("0")) {//管理员
            pd.put("org_cascade", "org");
        } else {
            pd.put("org_cascade", organize.getOrg_cascade());
        }
        List<PageData> list = organizeService.findTreeTableList(pd);
        //查询用户的数据权限数据
        pd.put("user_id", pd.get("id"));
        PageData p = userService.findUserOrganizeInfo(pd);
        if (Verify.verifyIsNotNull(p.get("authOrgIds"))) {
            String[] authOrgIds = p.get("authOrgIds").toString().split(",");
            String[] authParams = p.get("authParams").toString().split(",");
            StringBuilder showAuthData = new StringBuilder();
            StringBuilder operaAuthData = new StringBuilder();
            for (int i = 0; i < authOrgIds.length; i++) {
                showAuthData.append(authOrgIds[i]).append(",");
                if (authParams[i].contains("Y")) {
                    operaAuthData.append(authOrgIds[i]).append(",");
                }
            }
            if (showAuthData.length() > 0) {
                p.put("showAuthData", showAuthData.substring(0, showAuthData.length() - 1));
            } else {
                p.put("showAuthData", "");
            }
            if (operaAuthData.length() > 0) {
                p.put("operaAuthData", operaAuthData.substring(0, operaAuthData.length() - 1));
            } else {
                p.put("operaAuthData", "");
            }
        }else{
            p.put("showAuthData", "");
            p.put("operaAuthData", "");
        }
        pd.put("list",list);
        pd.put("object",p);
        return new MyResponse().data(pd);
    }

    /**
     * @title updateAuth
     * @description 更新权限
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:19
     */
    @PostMapping("/updateAuth")
    public void updateAuth(@RequestBody PageData pd,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            userService.updateAuth(pd);
            json.setSuccess(true);
            json.setMsg("更新权限信息成功");
        } catch (Exception e) {
            String message = "更新权限信息失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title findLoginUser
     * @description 查询登录用户
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:20
     */
    @GetMapping("/findLoginUser")
    public MyResponse findLoginUser() {
        PageData pd = new PageData();
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        //处理数据权限
        String user_id = userParams.split(":")[1];
        pd.put("user_id",user_id);
        PageData p = userService.findUserInfo(pd);
        //处理图片
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("obj_id",pd.get("id"));
        List<UploadFile> fileList = uploadService.listObjs(queryWrapper);
        p.put("pathUrl", ParaUtil.cloudfile);
        if(fileList.size()>0){
            p.put("imageUrl", ParaUtil.cloudfile+fileList.get(0).getFile_path());
        }else{
            p.put("imageUrl", "");
        }
        //查询用户组织
        pd.put("user_id",p.get("id"));
        List<UserOrganize> organizeList = userOrganizeService.findUserOrganize(pd);
        p.put("organizeList",organizeList);
        return new MyResponse().data(p);
    }


    /**
     * @title updateUser
     * @description 更新用户信息
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:20
     */
    @PostMapping("/updateUser")
    public MyResponse updateUser(@Valid @RequestBody SystemUser user) throws BizException {
        Json json = new Json();
        try {
            String time = DateTimeUtil.getDateTimeStr();
            String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
            //处理数据权限
            String user_id = userParams.split(":")[1];
            user.setCreate_user(user_id);
            user.setUpdate_time(time);
            this.userService.updateUser(user);
        } catch (Exception e) {
            String message = "修改用户失败";
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }

    /**
     * @title updateMyPwd
     * @description 重置密码
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:20
     */
    @PostMapping("/updateMyPwd")
    public MyResponse updateMyPwd(@RequestBody PageData pd) throws BizException {
        Json json = new Json();
        try {
            String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
            //处理数据权限
            String user_id = userParams.split(":")[1];
            pd.put("user_id",user_id);
            PageData user = userService.findUserInfo(pd);
            if(passwordEncoder.matches(pd.get("old_password").toString(),user.get("login_password").toString())){
                String time = DateTimeUtil.getDateTimeStr();
                SystemUser systemUser = new SystemUser();
                systemUser.setLogin_password(passwordEncoder.encode(pd.get("login_password").toString()));
                systemUser.setUpdate_time(time);
                systemUser.setId(user_id);
                userService.updateById(systemUser);
                json.setSuccess(true);
                json.setMsg("密码重置成功。");
            }else{
                json.setSuccess(false);
                json.setMsg("旧密码不正确，请重新输入。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(false);
            String message = "密码重置失败";
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }

    /**
     * @title updateSwitchOrganize
     * @description 组织切换
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:20
     */
    @PostMapping("/updateSwitchOrganize")
    public MyResponse updateSwitchOrganize(@RequestBody PageData pd) throws BizException {
        Json json = new Json();
        try {
            String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
            //处理数据权限
            String user_id = userParams.split(":")[1];
            String organize_id = userParams.split(":")[2];
            System.out.println("----------------------");
            System.out.println(pd);
            if(!pd.get("organize_id").equals(organize_id)){
                //将所有的个人信息use_status更新为：1
                PageData param = new PageData();
                param.put("user_id",user_id);
                param.put("use_status","1");
                param.put("update_time",DateTimeUtil.getDateTimeStr());
                userService.updateUserOrgUseStatus(param);
                param.put("use_status","0");
                param.put("organize_id",pd.get("organize_id"));
                userService.updateUserOrgUseStatus(param);
            }
            json.setSuccess(true);
            json.setMsg("组织切换成功。");
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("组织切换成功。");
            String message = "组织切换失败";
            log.error(message, e);
            throw new BizException(message);
        }
        return new MyResponse().data(json);
    }



}

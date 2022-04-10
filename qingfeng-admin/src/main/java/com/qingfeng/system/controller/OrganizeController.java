package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Organize;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.entity.SystemUser;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.IOrganizeService;
import com.qingfeng.system.service.IRoleService;
import com.qingfeng.system.service.IUserOrganizeService;
import com.qingfeng.system.service.IUserService;
import com.qingfeng.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @ProjectName OrganizeController
 * @author Administrator
 * @version 1.0.0
 * @Description 组织信息
 * @createTime 2022/1/19 0019 21:39
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/organize")
public class OrganizeController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IOrganizeService organizeService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IUserOrganizeService userOrganizeService;


    /**
     * @title listPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:39
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('organize:info')")
    public MyResponse listPage(QueryRequest queryRequest, Organize organize) {
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
        organize.setAuth_user(user_id);
        organize.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(organizeService.findListPage(organize, queryRequest));
        return new MyResponse().data(dataTable);
    }

    /**
     * @title save
     * @description 保存方法
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:39
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('organize:add')")
    public void save(@Valid @RequestBody Organize organize,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 创建用户
            String id = GuidUtil.getUuid();
            organize.setId(id);
            String time = DateTimeUtil.getDateTimeStr();
            organize.setCreate_time(time);
            organize.setStatus("0");
            organize.setType("1");
            //处理数据权限
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            organize.setCreate_user(authParams.split(":")[1]);
            organize.setCreate_organize(authParams.split(":")[2]);
            organize.setOrg_cascade(organize.getOrg_cascade()+id+"_");
            int level_num = Integer.parseInt(organize.getLevel_num())+1;
            organize.setLevel_num(level_num+"");
            this.organizeService.save(organize);
            json.setSuccess(true);
            json.setMsg("新增信息成功");
        } catch (Exception e) {
            String message = "新增组织失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title update
     * @description 更新方法
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:39
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('organize:edit')")
    public void update(@Valid @RequestBody Organize organize,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 更新组织信息
            String time = DateTimeUtil.getDateTimeStr();
            organize.setUpdate_time(time);
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            organize.setUpdate_user(authParams.split(":")[1]);
            this.organizeService.updateById(organize);
            json.setSuccess(true);
            json.setMsg("修改组织成功");
        } catch (Exception e) {
            String message = "修改组织失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title delete
     * @description 删除方法
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:39
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('organize:del')")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] del_ids = ids.split(StringPool.COMMA);
            this.organizeService.removeByIds(Arrays.asList(del_ids));
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
     * @title updateStatus
     * @description 更新状态
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:39
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('organize:status')")
    public void updateStatus(@Valid @RequestBody Organize organize,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.organizeService.updateById(organize);
            json.setSuccess(true);
            json.setMsg("状态修改成功");
        } catch (Exception e) {
            String message = "状态修改失败";
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
     * @updateTime 2021/4/3 0003 21:07
     */
    @PostMapping("/findRoleAuth")
    public MyResponse findRoleAuth(@RequestBody PageData pd) throws BizException {
        pd.put("organize_id",pd.get("id"));
        List<Role> roleLs = roleService.findSimpleList(pd);
        List<Role> myRoleLs = organizeService.findOrganizeRoleList(pd);
        pd.put("roleLs",roleLs);
        pd.put("myRoleLs",myRoleLs);
        return new MyResponse().data(pd);
    }

    /**
     * @title updateAuth
     * @description 更新权限
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:07
     */
    @PostMapping("/updateAuth")
    public void updateAuth(@RequestBody PageData pd,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            organizeService.updateAuth(pd);
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
     * @title getTreeList
     * @description 查询树形列表
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:07
     */
    @GetMapping("/list")
    public MyResponse list(Organize organize) throws IOException  {
        //处理数据权限
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        String user_id = userParams.split(":")[1];
//        String organize_id = userParams.split(":")[2];
        PageData pd = new PageData();
        pd.put("user_id",user_id);
        PageData orgPd = userService.findUserOrganizeInfo(pd);
        List<String> auth_organize_ids = new ArrayList<String>();
        if(Verify.verifyIsNotNull(orgPd)){
            if(Verify.verifyIsNotNull(orgPd.get("authOrgIds"))){
                auth_organize_ids = Arrays.asList(orgPd.get("authOrgIds").toString().split(","));
            }
        }
        organize.setAuth_user(user_id);
        organize.setAuth_organize_ids(auth_organize_ids);
        //获取用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",user_id);
        SystemUser user = userService.getOne(queryWrapper);
        if(!user.getType().equals("0")){//管理员
            organize.setOrg_cascade(orgPd.get("org_cascade").toString());
        }
        List<Organize> list = organizeService.findList(organize);
        return new MyResponse().data(list);
    }


}

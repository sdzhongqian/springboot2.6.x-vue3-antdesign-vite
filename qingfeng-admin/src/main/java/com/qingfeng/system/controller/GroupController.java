package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Group;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.IGroupService;
import com.qingfeng.system.service.IUserOrganizeService;
import com.qingfeng.system.service.IUserService;
import com.qingfeng.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * @ProjectName GroupController
 * @author Administrator
 * @version 1.0.0
 * @Description 用户组信息
 * @createTime 2022/1/19 0019 23:48
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/group")
public class GroupController extends BaseController {

    @Autowired
    private IGroupService groupService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserOrganizeService userOrganizeService;

    /**
     * @title listPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:48
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('group:info')")
    public MyResponse listPage(QueryRequest queryRequest, Group group) {
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
        group.setAuth_user(user_id);
        group.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(groupService.findListPage(group, queryRequest));
        return new MyResponse().data(dataTable);
    }


    /**
     * @title list
     * @description 查询用户组列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:48
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('group:info')")
    public MyResponse list(QueryRequest queryRequest, Group group) {
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
        group.setAuth_user(user_id);
        group.setAuth_organize_ids(auth_organize_ids);
        List<Group> list = groupService.findList(group);
        return new MyResponse().data(list);
    }


    /**
     * @title save
     * @description 保存数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:49
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('group:add')")
    public void save(@Valid @RequestBody Group group,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.groupService.saveGroup(group);
            json.setSuccess(true);
            json.setMsg("新增信息成功");
        } catch (Exception e) {
            String message = "新增信息失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            e.printStackTrace();
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title update
     * @description 更新数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:49
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('group:edit')")
    public void update(@Valid @RequestBody Group group,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.groupService.updateGroup(group);
            json.setSuccess(true);
            json.setMsg("修改信息成功");
        } catch (Exception e) {
            String message = "修改信息失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title delete
     * @description 删除数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:49
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('group:del')")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] del_ids = ids.split(StringPool.COMMA);
            this.groupService.removeByIds(Arrays.asList(del_ids));
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
     * @updateTime 2022/1/19 0019 23:50
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('group:status')")
    public void updateStatus(@Valid @RequestBody Group group,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.groupService.updateById(group);
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
     * @title findGroupUser
     * @description 查询组用户
     * @author Administrator
     * @updateTime 2022/1/23 0023 17:27
     */
    @GetMapping("/findGroupUser")
    public void findGroupUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = new PageData(request);
        Json json = new Json();
        try {
            List<PageData> list = groupService.findGroupUser(pd);
            json.setSuccess(true);
            json.setData(list);
            json.setMsg("查询组用户信息成功");
        } catch (Exception e) {
            String message = "查询组用户信息失败";
            json.setSuccess(false);
            json.setMsg(message);
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

}

package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Role;
import com.qingfeng.system.entity.RoleMenu;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.*;
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
import java.util.*;

/**
 * @ProjectName RoleController
 * @author Administrator
 * @version 1.0.0
 * @Description 角色信息
 * @createTime 2022/1/19 0019 21:56
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrganizeService organizeService;
    @Autowired
    private IRoleMenuService roleMenuService;
    @Autowired
    private IUserOrganizeService userOrganizeService;

    /**
     * @title listPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:56
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('role:info')")
    public MyResponse listPage(QueryRequest queryRequest, Role role) {
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
        role.setAuth_user(user_id);
        role.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(roleService.findListPage(role, queryRequest));
        return new MyResponse().data(dataTable);
    }

    /**
     * @title save
     * @description 保存方法
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:56
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('role:add')")
    public void save(@Valid @RequestBody Role role,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 创建用户
            String id = GuidUtil.getUuid();
            role.setId(id);
            String time = DateTimeUtil.getDateTimeStr();
            role.setCreate_time(time);
            role.setStatus("0");
            role.setType("1");
            //处理数据权限
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            role.setCreate_user(authParams.split(":")[1]);
            role.setCreate_organize(authParams.split(":")[2]);
            this.roleService.save(role);
            json.setSuccess(true);
            json.setMsg("新增信息成功");
        } catch (Exception e) {
            String message = "新增信息失败";
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
     * @updateTime 2022/1/19 0019 21:56
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('role:edit')")
    public void update(@Valid @RequestBody Role role,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 更新组织信息
            String time = DateTimeUtil.getDateTimeStr();
            role.setUpdate_time(time);
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            role.setUpdate_user(authParams.split(":")[1]);
            this.roleService.updateById(role);
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
     * @description 删除方法
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:56
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('role:del')")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] del_ids = ids.split(StringPool.COMMA);
            this.roleService.removeByIds(Arrays.asList(del_ids));
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
     * @title 更新状态
     * @description 更新状态
     * @author Administrator
     * @updateTime 2022/1/19 0019 21:57
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('role:status')")
    public void updateStatus(@Valid @RequestBody Role role,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.roleService.updateById(role);
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
     * @title updateAuth
     * @description 更新权限
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:09
     */
    @PostMapping("/updateAuth")
    public void updateAuth(@RequestBody PageData pd,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
            String create_user = userParams.split(":")[1];

            String role_id = pd.get("role_id").toString();
            String time = DateTimeUtil.getDateTimeStr();
            String[] ids = pd.get("ids").toString().split(",");
//            pd.put("menu_ids", Arrays.asList(ids));
            roleMenuService.delRoleMenu(pd);
            List<RoleMenu> list = new ArrayList<RoleMenu>();
            for (int i = 0; i < ids.length; i++) {
                RoleMenu roleMenu = new RoleMenu();
                //主键id
                roleMenu.setId(GuidUtil.getUuid());
                roleMenu.setMenu_id(ids[i]);
                roleMenu.setRole_id(role_id);
                roleMenu.setCreate_user(create_user);
                roleMenu.setCreate_time(time);
                list.add(roleMenu);
            }
            roleMenuService.saveBatch(list);
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
     * @title findRoleMenuList
     * @description 查询角色菜单信息
     * @author qingfeng
     * @updateTime 2021/4/3 0003 21:09
     */
    @PostMapping("/findRoleMenuList")
    public MyResponse findRoleMenuList(@RequestBody PageData pd) throws BizException {
        List<PageData> roleMenuList = roleMenuService.findRoleMenuList(pd);
        return new MyResponse().data(roleMenuList);
    }


}

package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Menu;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.IMenuService;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName MenuController
 * @author Administrator
 * @version 1.0.0
 * @Description 菜单信息
 * @createTime 2022/1/19 0019 22:43
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/menu")
public class MenuController extends BaseController {

    @Autowired
    private IMenuService menuService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserOrganizeService userOrganizeService;

    /**
     * @title listPage
     * @description TODO
     * @author Administrator
     * @updateTime 2022/1/19 0019 22:43
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('menu:info')")
    public MyResponse listPage(QueryRequest queryRequest, Menu menu) {
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
        menu.setAuth_user(user_id);
        menu.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(menuService.findListPage(menu, queryRequest));
        return new MyResponse().data(dataTable);
    }

    /**
     * @title save
     * @description 保存数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 22:43
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('menu:add')")
    public void save(@Valid @RequestBody Menu menu,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 创建用户
            String id = GuidUtil.getUuid();
            menu.setId(id);
            String time = DateTimeUtil.getDateTimeStr();
            menu.setCreate_time(time);
            menu.setStatus("0");
            //处理数据权限
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            menu.setCreate_user(authParams.split(":")[1]);
            menu.setCreate_organize(authParams.split(":")[2]);
            menu.setTitle(menu.getName());
            menu.setMenu_cascade(menu.getMenu_cascade()+id+"_");
            int level_num = Integer.parseInt(menu.getLevel_num())+1;
            menu.setLevel_num(level_num+"");
            this.menuService.save(menu);
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
     * @description 更新数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 22:43
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('menu:edit')")
    public void update(@Valid @RequestBody Menu menu,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 更新组织信息
            String time = DateTimeUtil.getDateTimeStr();
            menu.setUpdate_time(time);
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            menu.setUpdate_user(authParams.split(":")[1]);
            menu.setTitle(menu.getName());
            this.menuService.updateById(menu);
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
     * @updateTime 2022/1/19 0019 22:44
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('menu:del')")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] del_ids = ids.split(StringPool.COMMA);
            this.menuService.removeByIds(Arrays.asList(del_ids));
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
     * @updateTime 2022/1/19 0019 22:44
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('menu:status')")
    public void updateStatus(@Valid @RequestBody Menu menu,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.menuService.updateById(menu);
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
     * @title: findList
     * @description: 查询菜单列表
     * @author: qingfeng
     * @date: 2021/3/9 0009 23:05
     */
    @PostMapping("/list")
    public MyResponse list(@RequestBody PageData pd) throws IOException {
        if(Verify.verifyIsNotNull(pd.get("types"))){
            List<String> list = Arrays.asList(pd.get("types").toString().split(","));
            pd.put("typeList",list);
        }
        List<PageData> list = menuService.findList(pd);
        return new MyResponse().data(list);
    }


}
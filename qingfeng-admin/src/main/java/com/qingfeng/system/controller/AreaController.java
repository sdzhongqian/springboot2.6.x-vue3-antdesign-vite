package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Area;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.IAreaService;
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
 * @ProjectName AreaController
 * @author Administrator
 * @version 1.0.0
 * @Description 地区信息
 * @createTime 2022/1/19 0019 23:41
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/area")
public class AreaController extends BaseController {

    @Autowired
    private IAreaService areaService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserOrganizeService userOrganizeService;

    /**
     * @title listPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:41
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('area:info')")
    public MyResponse listPage(QueryRequest queryRequest, Area area) {
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
        area.setAuth_user(user_id);
        area.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(areaService.findListPage(area, queryRequest));
        return new MyResponse().data(dataTable);
    }

    /**
     * @title save
     * @description 保存数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:41
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('area:add')")
    public void save(@Valid @RequestBody Area area,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 创建用户
            String id = GuidUtil.getUuid();
            area.setId(id);
            String time = DateTimeUtil.getDateTimeStr();
            area.setCreate_time(time);
            area.setStatus("0");
            area.setType("1");
            //处理数据权限
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            area.setCreate_user(authParams.split(":")[1]);
            area.setCreate_organize(authParams.split(":")[2]);

            area.setArea_cascade(area.getArea_cascade()+id+"_");
            int level_num = Integer.parseInt(area.getLevel_num())+1;
            area.setLevel_num(level_num+"");
            this.areaService.save(area);
            json.setSuccess(true);
            json.setMsg("新增信息成功");
        } catch (Exception e) {
            json.setSuccess(false);
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
     * @updateTime 2022/1/19 0019 23:41
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('area:edit')")
    public void update(@Valid @RequestBody Area area,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 更新组织信息
            String time = DateTimeUtil.getDateTimeStr();
            area.setUpdate_time(time);
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            area.setUpdate_user(authParams.split(":")[1]);
            this.areaService.updateById(area);
            json.setSuccess(true);
            json.setMsg("修改信息成功");
        } catch (Exception e) {
            json.setSuccess(false);
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
     * @updateTime 2022/1/19 0019 23:41
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('area:del')")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] del_ids = ids.split(StringPool.COMMA);
            this.areaService.removeByIds(Arrays.asList(del_ids));
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
     * @updateTime 2022/1/19 0019 23:41
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('area:status')")
    public void updateStatus(@Valid @RequestBody Area area,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.areaService.updateById(area);
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
     * @title findList
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 23:42
     */
    @GetMapping("/list")
    public MyResponse list(Area area) throws IOException  {
        //处理数据权限
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        String user_id = userParams.split(":")[1];
        PageData pd = new PageData();
        pd.put("user_id",user_id);
        PageData orgPd = userService.findUserOrganizeInfo(pd);
        List<String> auth_organize_ids = new ArrayList<String>();
        if(Verify.verifyIsNotNull(orgPd)){
            if(Verify.verifyIsNotNull(orgPd.get("authOrgIds"))){
                auth_organize_ids = Arrays.asList(orgPd.get("authOrgIds").toString().split(","));
            }
        }
        area.setAuth_user(user_id);
        area.setAuth_organize_ids(auth_organize_ids);
        List<Area> list = areaService.findList(area);
        return new MyResponse().data(list);
    }

}

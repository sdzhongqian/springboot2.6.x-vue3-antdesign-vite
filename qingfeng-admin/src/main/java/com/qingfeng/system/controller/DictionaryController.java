package com.qingfeng.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.system.entity.Dictionary;
import com.qingfeng.system.entity.UserOrganize;
import com.qingfeng.system.service.IDictionaryService;
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
 * @ProjectName DictionaryController
 * @author Administrator
 * @version 1.0.0
 * @Description 字典信息
 * @createTime 2022/1/19 0019 22:52
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/dictionary")
public class DictionaryController extends BaseController {

    @Autowired
    private IDictionaryService dictionaryService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserOrganizeService userOrganizeService;

    /**
     * @title listPage
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 22:52
     */
    @GetMapping("/listPage")
    @PreAuthorize("hasAnyAuthority('dictionary:info')")
    public MyResponse listPage(QueryRequest queryRequest, Dictionary dictionary) {
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
        dictionary.setAuth_user(user_id);
        dictionary.setAuth_organize_ids(auth_organize_ids);
        Map<String, Object> dataTable = MyUtil.getDataTable(dictionaryService.findListPage(dictionary, queryRequest));
        return new MyResponse().data(dataTable);
    }

    /**
     * @title save
     * @description 保存数据
     * @author Administrator
     * @updateTime 2022/1/19 0019 22:53
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('dictionary:add')")
    public void save(@Valid @RequestBody Dictionary dictionary,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 创建用户
            String id = GuidUtil.getUuid();
            dictionary.setId(id);
            String time = DateTimeUtil.getDateTimeStr();
            dictionary.setCreate_time(time);
            dictionary.setStatus("0");
            dictionary.setType("1");
            //处理数据权限
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            dictionary.setCreate_user(authParams.split(":")[1]);
            dictionary.setCreate_organize(authParams.split(":")[2]);

            dictionary.setDic_cascade(dictionary.getDic_cascade()+id+"_");
            int level_num = Integer.parseInt(dictionary.getLevel_num())+1;
            dictionary.setLevel_num(level_num+"");
            this.dictionaryService.save(dictionary);
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
     * @updateTime 2022/1/19 0019 22:53
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('dictionary:edit')")
    public void update(@Valid @RequestBody Dictionary dictionary,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            // 更新组织信息
            String time = DateTimeUtil.getDateTimeStr();
            dictionary.setUpdate_time(time);
            String authParams = SecurityContextHolder.getContext().getAuthentication().getName();
            dictionary.setUpdate_user(authParams.split(":")[1]);
            this.dictionaryService.updateById(dictionary);
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
     * @updateTime 2022/1/19 0019 22:53
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('dictionary:del')")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            String[] del_ids = ids.split(StringPool.COMMA);
            this.dictionaryService.removeByIds(Arrays.asList(del_ids));
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
     * @updateTime 2022/1/19 0019 22:53
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('dictionary:status')")
    public void updateStatus(@Valid @RequestBody Dictionary dictionary,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            this.dictionaryService.updateById(dictionary);
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
     * @title list
     * @description 查询数据列表
     * @author Administrator
     * @updateTime 2022/1/19 0019 22:53
     */
    @GetMapping("/list")
    public MyResponse list(Dictionary dictionary) throws IOException  {
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
        dictionary.setAuth_user(user_id);
        dictionary.setAuth_organize_ids(auth_organize_ids);
        List<Dictionary> list = dictionaryService.findList(dictionary);
        return new MyResponse().data(list);
    }

}

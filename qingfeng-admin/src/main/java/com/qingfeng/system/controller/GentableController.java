package com.qingfeng.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingfeng.base.controller.BaseController;
import com.qingfeng.base.entity.QueryRequest;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.framework.properties.ServerSystemProperties;
import com.qingfeng.system.entity.Genfield;
import com.qingfeng.system.entity.Gentable;
import com.qingfeng.system.service.IGenfieldService;
import com.qingfeng.system.service.IGentableService;
import com.qingfeng.utils.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

/**
 * @ProjectName GentableController
 * @author Administrator
 * @version 1.0.0
 * @Description GentableController
 * @createTime 2022/3/20 0020 9:12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/gencode")
public class GentableController extends BaseController {

    @Autowired
    private IGentableService gentableService;
    @Autowired
    private IGenfieldService genfieldService;
    @Autowired
    private ServerSystemProperties serverSystemProperties;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    /**
     * @title tbleListPage
     * @description 查询分页列表
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:12
     */
    @PostMapping("/tableListPage")
    public void tableListPage(@RequestBody PageData pd, HttpServletResponse response) throws Exception {
        //处理分页
        System.out.println("---------------------");
        System.out.println(pd.toString());
        if(Verify.verifyIsNull(pd.get("pageSize"))){
            pd.put("pageSize",10);
        }
        if(Verify.verifyIsNull(pd.get("pageNum"))){
            pd.put("pageNum",1);
        }
        pd.put("start_page",(Integer.parseInt(pd.get("pageNum").toString())-1)*Integer.parseInt(pd.get("pageSize").toString()));
        List<PageData> list = gentableService.findTableListPage(pd);
        int num = gentableService.findTableListNum(pd);
        Json json = new Json();
        json.setCount(num);
        json.setData(list);
        this.writeJson(response,json);
    }

    /**
     * @title save
     * @description 保存
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:12
     */
    @PostMapping("/save")
    public void save(@RequestBody PageData pd,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            gentableService.saveTable(pd);
            json.setSuccess(true);
            json.setMsg("新增信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(false);
            String message = "新增信息失败";
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }


    /**
     * @title listPage
     * @description 查询数据分页列表
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:13
     */
    @GetMapping("/listPage")
    public MyResponse listPage(QueryRequest queryRequest, Gentable gentable) {
        PageData pd = new PageData();
//        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        IPage<Gentable> list = gentableService.findListPage(gentable,queryRequest);
        for (Gentable gtable:list.getRecords()) {
            pd.put("table_id",gtable.getId());
            pd.put("excludeField",Arrays.asList(serverSystemProperties.getGencodeField().split(",")));
            List<Genfield> fieldList = genfieldService.findFieldList(pd);
            gtable.setFieldList(fieldList);
            gtable.setExcludeField(serverSystemProperties.getGencodeField());
        }
        Map<String, Object> dataTable = MyUtil.getDataTable(list);
        return new MyResponse().data(dataTable);
    }


    /**
     * @title delete
     * @description 删除
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:13
     */
    @DeleteMapping("/{ids}")
    public void delete(@NotBlank(message = "{required}") @PathVariable String ids,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            gentableService.del(ids);
            json.setSuccess(true);
            json.setMsg("删除信息成功");
        } catch (Exception e) {
            json.setSuccess(false);
            String message = "删除失败";
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title update
     * @description 更新
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:13
     */
    @PostMapping("/update")
    public void update(@RequestBody Gentable gentable,HttpServletResponse response) throws Exception {
        Json json = new Json();
        try {
            gentableService.updateTable(gentable);
            json.setSuccess(true);
            json.setMsg("更新信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(false);
            String message = "更新信息失败";
            log.error(message, e);
            throw new BizException(message);
        }
        this.writeJson(response,json);
    }

    /**
     * @title gencode
     * @description 代码生成
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:13
     */
    @GetMapping("/gencode")
    public void gencode(@RequestParam String id, HttpServletResponse response) throws Exception  {
        PageData pd = new PageData();
        pd.put("id",id);
        Gentable gentable = gentableService.getById(id);
        pd.put("table_id",gentable.getId());
        List<Genfield> fieldList = genfieldService.findFieldList(pd);
        if(gentable.getTemp_type().equals("0")){//单表
        }else if(gentable.getTemp_type().equals("1")){//树表
            Iterator<Genfield> itLs=fieldList.iterator();
            while(itLs.hasNext()){
                Genfield genfield=itLs.next();
                if(genfield.getField_name().equals(gentable.getTree_pid())){
                    itLs.remove();
                }
            }
        }
        //1、创建数据模型
        Map<String,Object> root = new HashMap<String,Object>();
        //2、为数据模型添加值
        root.put("pd", pd);
        root.put("tablePd",gentable);
        root.put("fieldList",fieldList);

        String gen_path = PathUtil.getSystemPath();
        if(Verify.verifyIsNotNull(gentable.getGen_path())){
            gen_path = gentable.getGen_path();
        }
        gen_path = gen_path+"gencode/";
        //生成Mapper.xml
        fprint("gencode/TemplateMapper.xml.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+ FreemarkerParaUtil.mapper+StrUtil.upperFirst(gentable.getBus_name())+"Mapper.xml");
        //生成Mapper.java
        fprint("gencode/TemplateMapper.java.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.mapper+StrUtil.upperFirst(gentable.getBus_name())+"Mapper.java");
        //生成Service
        fprint("gencode/ITemplateService.java.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.service+"I"+StrUtil.upperFirst(gentable.getBus_name())+"Service.java");
        //生成ServiceImpl
        fprint("gencode/TemplateServiceImpl.java.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.service+FreemarkerParaUtil.impl+StrUtil.upperFirst(gentable.getBus_name())+"ServiceImpl.java");
        //生成Controller
        fprint("gencode/TemplateController.java.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.controller+StrUtil.upperFirst(gentable.getBus_name())+"Controller.java");
        //生成Controller
        fprint("gencode/TemplateEntity.java.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.entity+StrUtil.upperFirst(gentable.getBus_name())+".java");

        //=====================================vue端代码生成============================================
        //生成INDEX
        fprint("gencode/TemplateIndex.vue.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.vue+gentable.getBus_name()+"/Index.vue");
        //生成EDIE
        fprint("gencode/TemplateEdit.vue.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.vue+gentable.getBus_name()+"/Edit.vue");
        //生成INFo
        fprint("gencode/TemplateInfo.vue.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.vue+gentable.getBus_name()+"/Info.vue");
        if(gentable.getTemp_type().equals("1")) {//树表
            //生成TREE
            fprint("gencode/TemplateTree.vue.ftl", root, gen_path + gentable.getBus_name() + File.separator + gentable.getMod_name() + File.separator + FreemarkerParaUtil.vue + gentable.getBus_name() + "/Tree.vue");
        }
        //生成API
        fprint("gencode/TemplateApi.ts.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.vue+gentable.getBus_name()+"/"+gentable.getBus_name()+".ts");


        //=====================================处理菜单信息==================================================
        //处理数据权限
        String userParams = SecurityContextHolder.getContext().getAuthentication().getName();
        String user_id = userParams.split(":")[1];
        String organize_id = userParams.split(":")[2];
        //查询父级菜单
        PageData param = new PageData();
        param.put("id",gentable.getMenu_id());
        System.out.println("==========================");
        PageData menuPd = gentableService.findMenuInfo(param);
        System.out.println(menuPd);
        if(Verify.verifyIsNotNull(menuPd)){
            String time = DateTimeUtil.getDateTimeStr();
            List<PageData> menuList = new ArrayList<PageData>();
            //组织菜单信息
            PageData mPd = new PageData();
            String menu_id = GuidUtil.getUuid();
            mPd.put("id",menu_id);
            mPd.put("type","1");
            mPd.put("title",gentable.getMenu_name());
            mPd.put("path","/"+gentable.getMod_name()+"/"+gentable.getBus_name());
            mPd.put("component","/"+gentable.getMod_name()+"/"+gentable.getBus_name());
            mPd.put("permission",gentable.getBus_name());
            mPd.put("keepAlive","true");
            mPd.put("parent_id",menuPd.get("id"));
            mPd.put("order_by",Integer.parseInt(menuPd.get("child_num").toString())+1);
            mPd.put("status","0");
            mPd.put("remark","");
            mPd.put("create_time",time);
            mPd.put("create_user",time);
            mPd.put("create_organize",time);
            mPd.put("create_user",user_id);
            mPd.put("create_organize",organize_id);
            menuList.add(mPd);
            //组织菜单功能按钮-添加
            PageData btnAddPd = new PageData();
            String btnAdd_id = GuidUtil.getUuid();
            btnAddPd.put("id",btnAdd_id);
            btnAddPd.put("title","添加");
            btnAddPd.put("path","/");
            btnAddPd.put("component","/");
            btnAddPd.put("keepAlive","true");
            btnAddPd.put("permission","add");
            btnAddPd.put("parent_id",menu_id);
            btnAddPd.put("type","2");
            btnAddPd.put("order_by","1");
            btnAddPd.put("status","0");
            btnAddPd.put("remark","");
            btnAddPd.put("create_time",time);
            btnAddPd.put("create_user",user_id);
            btnAddPd.put("create_organize",organize_id);
            menuList.add(btnAddPd);
            //组织菜单功能按钮-编辑
            PageData btnEditPd = new PageData();
            String btnEdit_id = GuidUtil.getUuid();
            btnEditPd.put("id",btnEdit_id);
            btnEditPd.put("title","编辑");
            btnEditPd.put("path","/");
            btnEditPd.put("component","/");
            btnEditPd.put("keepAlive","true");
            btnEditPd.put("permission","edit");
            btnEditPd.put("parent_id",menu_id);
            btnEditPd.put("type","2");
            btnEditPd.put("order_by","2");
            btnEditPd.put("status","0");
            btnEditPd.put("remark","");
            btnEditPd.put("create_time",time);
            btnEditPd.put("create_user",user_id);
            btnEditPd.put("create_organize",organize_id);
            menuList.add(btnEditPd);
            //组织菜单功能按钮-删除
            PageData btnDelPd = new PageData();
            String btnDel_id = GuidUtil.getUuid();
            btnDelPd.put("id",btnDel_id);
            btnDelPd.put("title","删除");
            btnDelPd.put("path","/");
            btnDelPd.put("component","/");
            btnDelPd.put("keepAlive","true");
            btnDelPd.put("permission","del");
            btnDelPd.put("parent_id",menu_id);
            btnDelPd.put("type","2");
            btnDelPd.put("order_by","3");
            btnDelPd.put("status","0");
            btnDelPd.put("remark","");
            btnDelPd.put("create_time",time);
            btnDelPd.put("create_user",user_id);
            btnDelPd.put("create_organize",organize_id);
            menuList.add(btnDelPd);
            //组织菜单功能按钮-详情
            PageData btnInfoPd = new PageData();
            String btnInfo_id = GuidUtil.getUuid();
            btnInfoPd.put("id",btnInfo_id);
            btnInfoPd.put("title","详情");
            btnInfoPd.put("path","/");
            btnInfoPd.put("component","/");
            btnInfoPd.put("keepAlive","true");
            btnInfoPd.put("permission","info");
            btnInfoPd.put("parent_id",menu_id);
            btnInfoPd.put("type","2");
            btnInfoPd.put("order_by","4");
            btnInfoPd.put("status","0");
            btnInfoPd.put("remark","");
            btnInfoPd.put("create_time",time);
            btnInfoPd.put("create_user",user_id);
            btnInfoPd.put("create_organize",organize_id);
            menuList.add(btnInfoPd);
            root.put("mPd",mPd);
            root.put("menuList",menuList);
            System.out.println("00000000000000000000");
            fprint("gencode/TemplateMenuSql.sql.ftl", root, gen_path+gentable.getBus_name()+File.separator+gentable.getMod_name()+File.separator+FreemarkerParaUtil.sql+gentable.getBus_name()+"_menu.sql");
        }

        Json json = new Json();
        json.setData(gen_path+gentable.getBus_name());
        json.setSuccess(true);
        json.setMsg("代码生成成功。");
        this.writeJson(response,json);
    }


    /**
     * @title downloadCode
     * @description 下载
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:14
     */
    @GetMapping("/downloadCode")
    public void downloadCode(HttpServletResponse response,@RequestParam String id) throws Exception {
        Gentable gentable = gentableService.getById(id);
        String path = gentable.getGen_path()+"gencode/"+gentable.getBus_name();
        ZipUtils.toZip(path+File.separator+gentable.getMod_name(), path+"/"+gentable.getBus_name()+".zip", true);
        StringBuffer sb = new StringBuffer();
        FileUtil.downFile(response, path+"/"+gentable.getBus_name()+".zip",sb.append(gentable.getMenu_name().trim()).append("【代码】.zip").toString());
    }


    public void fprint(String templatePath,Object obj,String outPath) throws Exception {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        configuration.setClassForTemplateLoading(this.getClass(), "/templates/");
        System.out.println(outPath);
        //ContextLoader loader = new ContextLoader();
        Template template = configuration.getTemplate(templatePath);

        String dirpath = outPath.substring(0,outPath.lastIndexOf("/"));
        System.out.println(dirpath);
        File dirFile = new File(dirpath);
        if(!dirFile.exists()){
            dirFile.mkdir();
            dirFile.mkdirs();
        }

        File file = new File(outPath);
        Writer out = new FileWriter(file);
        template.process(obj, out);//输出
        out.close();
    }

    /**
     * @title viewCode
     * @description 代码预览
     * @author Administrator
     * @updateTime 2022/3/20 0020 9:15
     */
    @GetMapping("/viewCode")
    public MyResponse viewCode(@RequestParam String id)  {
        Gentable gentable = gentableService.getById(id);

        String path = gentable.getGen_path()+"gencode/"+gentable.getBus_name();
        path = path+File.separator+gentable.getMod_name();
        //读取文件夹下所有的文件
        List<File> fileList = FileUtil.traverseFolder1(path);
        List<PageData> list = new ArrayList<PageData>();
        for (File file: fileList) {
            PageData param = new PageData();
            param.put("name",file.getName());
            param.put("content", StringEscapeUtils.escapeHtml(FileUtil.readFileContent(file)));
            list.add(param);
        }
        return new MyResponse().data(list);
    }



}

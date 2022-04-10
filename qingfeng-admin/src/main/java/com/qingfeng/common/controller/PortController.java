package com.qingfeng.common.controller;

import com.qingfeng.utils.JsonToMap;
import com.qingfeng.utils.PageData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @ProjectName PortController
 * @author Administrator
 * @version 1.0.0
 * @Description port
 * @createTime 2022/4/5 0005 23:51
 */
@Controller
@RequestMapping("/port")
public class PortController {

    @GetMapping("/index")
    public void index(@RequestBody String data, HttpServletResponse response) throws Exception {
        PageData pd = JsonToMap.parseJSON2Pd(data);
        System.out.println("获取接口参数："+pd.toString());

        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("认证成功。");
    }

}
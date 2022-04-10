package com.qingfeng.common.controller;

import com.qingfeng.common.service.IDemoService;
import com.qingfeng.common.service.ITdemoService;
import com.qingfeng.framework.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description DemoController
 * @createTime 2022年01月11日 14:18:00
 */
@Controller
@RequestMapping("/common/demo")
public class DemoController {

    @Autowired
    private IDemoService demoService;


    @GetMapping("/")
    public void index(HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("Hello Word");
    }

    @GetMapping("/testException")
    public void testException(HttpServletResponse response) throws Exception {
//        String str=null;
//        str.equals("111");
        if(1==1){
            throw new BizException("10","异常");
        }
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("Hello Word");
    }


    @GetMapping("/hello")
    public void hello(@RequestParam("name") String name,HttpServletResponse response) throws Exception{
        System.out.println(demoService.sayHello(name));
        PrintWriter out = response.getWriter();
        out.write(demoService.sayHello(name));
    }

    @GetMapping("/hello2")
    public String circuitBreaker(@RequestParam("name") String name){
        return demoService.circuitBreaker(name);
    }


}
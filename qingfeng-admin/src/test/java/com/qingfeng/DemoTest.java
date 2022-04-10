package com.qingfeng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingfeng.common.entity.Demo;
import com.qingfeng.common.service.IDemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月11日 15:39:00
 */
@SpringBootTest
public class DemoTest {

    @Autowired
    private IDemoService demoService;


    @Test
    void save() {
        Demo demo = new Demo();
        demo.setId("3");
        demo.setName("李六");
        demo.setRemark("备注");
        demo.setCreate_time("20220111");
        demoService.save(demo);
    }

    @Test
    void update() {
        Demo demo = new Demo();
        demo.setId("1");
        demo.setName("李四");
        demo.setRemark("备注");
        demo.setCreate_time("20220111");
        demoService.updateById(demo);
    }

    @Test
    void info() {
        Demo demo = demoService.getById("1");
        System.out.println(demo);
    }


    @Test
    void list() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.like("name","李");
        List<Demo> list = demoService.list(wrapper);
        for (Demo demo:list) {
            System.out.println(demo);
        }

    }


    @Test
    public void listPage() throws Exception {
        LambdaQueryWrapper<Demo> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.like(Demo::getName , "李");

        Page<Demo> userPage = new Page<Demo>(1 , 2);
        IPage<Demo> userIPage = demoService.findPage(userPage , userLambdaQueryWrapper);
        System.out.println("总页数： "+userIPage.getPages());
        System.out.println("总记录数： "+userIPage.getTotal());
        userIPage.getRecords().forEach(System.out::println);
    }

    @Test
    public void testException() {
        String str=null;
        str.equals("111");
    }


}
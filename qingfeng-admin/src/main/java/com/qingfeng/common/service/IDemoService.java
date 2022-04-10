package com.qingfeng.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qingfeng.common.entity.Demo;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月11日 15:36:00
 */
public interface IDemoService extends IService<Demo> {

    IPage<Demo> findPage(Page page, LambdaQueryWrapper queryWrapper);

    //限流降级
    public String sayHello(String name);

    //熔断降级
    public String circuitBreaker(String name);
}
package com.qingfeng.common.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.common.entity.Demo;
import com.qingfeng.common.mapper.DemoMapper;
import com.qingfeng.common.service.IDemoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年01月11日 15:37:00
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@DS("slave")
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements IDemoService {

    @DS("base")
    public IPage<Demo> findPage(Page page, LambdaQueryWrapper queryWrapper){
        return this.baseMapper.selectPage(page,queryWrapper);
    }


    /**
     * 限流降级
     * @return
     */
    @SentinelResource(value = "sayHello", blockHandler = "sayHelloExceptionHandler")
    public String sayHello(String name){
        return "hello,"+ name;
    }

    /**
     * 熔断降级
     * @return
     */
    @SentinelResource(value = "circuitBreaker", fallback = "circuitBreakerFallback", blockHandler = "sayHelloExceptionHandler")
    public String circuitBreaker(String name){
        if ("zhangsan".equals(name)){
            return "hello,"+ name;
        }
        throw new RuntimeException("发生异常");
    }

    public String circuitBreakerFallback(String name){
//        return "服务异常，熔断降级, 请稍后重试!";
        return "The service is abnormal, the circuit breaker is downgraded, please try again later!";
    }

    public String sayHelloExceptionHandler(String name, BlockException ex){
//        return "访问过快，限流降级, 请稍后重试!";
        return "The access is too fast, the current limit is downgraded, please try again later!";
    }

}
package com.qingfeng.framework.configure;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description TODO
 * @createTime 2022年04月01日 22:36:00
 */
@Configuration
public class SentinelConfigure {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void doInit() {
        initFlowRule();
    }

    public static void initFlowRule(){
        List<FlowRule> rules=new ArrayList<>();
        FlowRule flowRule=new FlowRule();
        flowRule.setResource("/auth/captcha"); //针对那个资源设置规则
        flowRule.setGrade( RuleConstant.FLOW_GRADE_QPS);//QPS或者并发数
        flowRule.setCount(1); //QPS=5
        flowRule.setControlBehavior ( RuleConstant.CONTROL_BEHAVIOR_DEFAULT );//直接拒绝
        rules.add(flowRule);
        FlowRuleManager.loadRules(rules);
    }

}

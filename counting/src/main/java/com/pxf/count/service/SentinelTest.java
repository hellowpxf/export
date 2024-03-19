package com.pxf.count.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.pxf.count.dao.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:SentinelTest
 * @author:pxf
 * @data:2024/03/02
 **/
@Service
public class SentinelTest {

    @SentinelResource(value ="hello",blockHandler = "blockHandlerForGetUser")
    public String getUserById(String id) {
        System.out.println(id);
        return  id;
    }

    public String blockHandlerForGetUser(String id, BlockException ex) {
        return "admin";
    }

    public  static void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource("hello");
        // Set max qps to 20
        rule1.setCount(2);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);
    }
}

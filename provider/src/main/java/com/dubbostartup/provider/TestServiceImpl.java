package com.dubbostartup.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.dubbostartup.common.service.TestService;

@Service(version = "${demo.service.version}")
public class TestServiceImpl implements TestService {

    @Override
    public String sayHello(String name) {

        System.out.println("---------------------------");

        return "Hi, " + name + "!";
    }
}
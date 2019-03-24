package com.dubbostartup.consumer.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dubbostartup.common.service.TestService;

@RestController
public class TestController {

    @Reference(version = "${demo.service.version}")
    private TestService testService;

    @RequestMapping("/sayHello/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return testService.sayHello(name);
    }
}
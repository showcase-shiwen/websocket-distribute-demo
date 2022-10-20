package com.lsw.websocket.controller;


import cn.hutool.http.HttpUtil;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.healthcheck.impl.Http;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("cluster")
public class ClusterController {

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String appName;

    @PostMapping("broadcastAll")
    public String sendMsg(String msg) throws NacosException {
        List<Instance> instanceList=get();
        instanceList.parallelStream().forEach(instance -> {
            StringBuffer url=new StringBuffer();
            url.append("http://");
            url.append(instance.getIp());
            url.append(":");
            url.append(instance.getPort());
            url.append("/sendMsg");
            Map<String, Object> paramMap=new HashMap<>();
            paramMap.put("msg",msg);
            String respStr=HttpUtil.post(url.toString(),paramMap);
        });
        return "success";
    }
    @GetMapping("onlineCount")
    public Integer onlineCount(String msg) throws NacosException {
        java.util.concurrent.atomic.AtomicInteger atomicInteger=new AtomicInteger();
        List<Instance> instanceList=get();
        instanceList.parallelStream().forEach(instance -> {
            StringBuffer url=new StringBuffer();
            url.append("http://");
            url.append(instance.getIp());
            url.append(":");
            url.append(instance.getPort());
            url.append("/onlineCount");
            atomicInteger.getAndAdd(Integer.valueOf(HttpUtil.get(url.toString())));
        });
        return atomicInteger.get();
    }
    @GetMapping("instances")
    public List<Instance> get() throws NacosException {
        return namingService.getAllInstances(appName);
    }
}

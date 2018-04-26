package com.pinyougou.manager.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.manager.controller
 * @date 2018/4/25/0025
 */

@RequestMapping("/login")
@RestController
public class LoginController {

    @RequestMapping("/getInfo")
    public Map getUserInfo(){
        //获取登录的用户的登录名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName",name);
        return map;
    }
}


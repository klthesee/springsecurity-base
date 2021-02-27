package com.zk.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chs
 * @since 2021-02-24
 */
@RestController
@RequestMapping("/user")
public class UsersController {
    @GetMapping("login")
    public String login(){
        return "login";
    }
}


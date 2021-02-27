package com.zk.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/index2")
    public String index2(){
        return "index2";
    }



    @Secured({"ROLE_user","ROLE_manager","ROLE_admin"})
    @GetMapping("/update")
    public String update(){
        return "enter update";
    }

    @GetMapping("/preAuthorize-test")
    @PreAuthorize("hasAnyAuthority('menu:system')")
    public String preAuthorizeTest(){
        return "PreAuthorize - hasAnyAuthority('menu:system')";
    }

    /**
     * @PostAuthorize 方法之后校验，没有该权限但是方法执行了
     * @return
     */
    @PostAuthorize("hasAnyAuthority('no-menu:system')")
    @GetMapping("postAuthorize-test")
    public String postAuthorizeTest(){
        log.info("执行了postAuthorizeTest");
        return "PostAuthorize - postAuthorizeTest ";
    }
}

package com.qingfeng.auth.controller;

import com.qingfeng.auth.service.ValidateCodeService;
import com.qingfeng.framework.exception.BizException;
import com.qingfeng.utils.MyResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * @ProjectName SecurityController
 * @author Administrator
 * @version 1.0.0
 * @Description SecurityController
 * @createTime 2022/3/5 0005 18:06
 */
@RequestMapping("auth")
@RestController
public class SecurityController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;
    @Autowired
    private ValidateCodeService validateCodeService;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * @title captcha
     * @description 获取验证码
     * @author Administrator
     * @updateTime 2021/4/3 0003 19:10
     * @throws
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateCodeService.create(request, response);
    }

    /**
     * @title testOauth
     * @description 测试权限
     * @author Administrator
     * @updateTime 2021/4/3 0003 19:10
     * @throws
     */
    @GetMapping("/oauth/test")
    public String testOauth() {
        System.out.println("--------------------------==-----------");
        System.out.println(userDetailsService.loadUserByUsername("admin").getAuthorities());
        return "oauth";
    }

    /**
     * @title currentUser
     * @description 获取当前用户
     * @author Administrator
     * @updateTime 2021/4/3 0003 19:10
     * @throws
     */
    @GetMapping("/user")
    public Principal currentUser(Principal principal) {
        System.out.println("------------------查询开始");
        System.out.println(userDetailsService.loadUserByUsername("admin").getAuthorities());
        System.out.println("------------------查询完成");
        return principal;
    }

    /**
     * @title signout
     * @description 推出登录
     * @author Administrator
     * @updateTime 2021/4/3 0003 19:11
     * @throws
     */
    @DeleteMapping("/signout")
    public MyResponse signout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String token = StringUtils.replace(authorization, "bearer ", "");
        MyResponse qfResponse = new MyResponse();
        if (!consumerTokenServices.revokeToken(token)) {
            throw new BizException("退出登录失败");
        }
        return qfResponse.message("退出登录成功");
    }
}

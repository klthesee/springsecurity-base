package com.zk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true) // 开启权限注解
public class SecurityConfig3 extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    //注入数据源
    @Autowired
    private DataSource dataSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true); // 自动创建表
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
/*        http.formLogin()
            .loginPage("/login.html") // 自定义登录页面
            .loginProcessingUrl("/user/login")//spring security帮你做验证,这个UsernamePasswordAuthenticationFilter过滤器去做认证
            .defaultSuccessUrl("/test/index").permitAll()//登录成功后的跳转路径
            .and().authorizeRequests().antMatchers("/", "/test/hello", "/user/login").permitAll()//设置哪些路径不需要认证就可以访问
            .anyRequest().authenticated()//指明哪些请求可以被任何权限的用户访问
            .and().csrf().disable();//关闭csrf防护*/
        // 没有权限页面
        http.exceptionHandling().accessDeniedPage("/unauth.html");
        // 加权限控制（基于权限/用户角色）
        http.formLogin()
            .loginPage("/login.html") // 自定义登录页面
            .loginProcessingUrl("/user/login")//spring security帮你做验证,这个UsernamePasswordAuthenticationFilter过滤器去做认证
//            .defaultSuccessUrl("/test/index") //登录成功后的跳转路径
            .defaultSuccessUrl("/success.html") //登录成功后的跳转路径
                .permitAll()
            .and().authorizeRequests().antMatchers("/", "/test/hello", "/user/login").permitAll()//设置哪些路径不需要认证就可以访问
                .antMatchers("/test/index","/test/index2")
                // 基于权限
                // 1.hasAuthority() 拥有abc权限才能访问/test/index路径.在UserDetailsService中添加权限
//                    .hasAuthority("abc")
                // 2.hasAnyAuthority() 校验多个权限 拥有abc或eat权限才能访问/test/index路径.在UserDetailsService中添加权限
//                .hasAnyAuthority("abc,eat")
                // 基于角色
                // 3.hasRole() 会给传入的字符串加 ROLE_ 前缀
//                .hasRole("admin")
                // 4..hasAnyRole() 满足任意一个角色就可访问
                .hasAnyRole("user")

                // 记住我
                .anyRequest().authenticated()
                .and().rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(600) // token有效时长,单位秒
                .userDetailsService(userDetailsService)
            .and().csrf().disable();

        // 退出登录
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/test/hello").permitAll();
    }
}

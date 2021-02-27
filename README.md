# springsecurity-base

idea全局搜索 ctrl + shift + n

原理：
spring security 本质是一个过滤器链

FilterSecurityInterceptor 方法级别的权限过滤器，位于过滤器链的最底端
ExceptionTransactionFilter 异常过滤器
UsernamePasswordAuthenticationFilter 对login方法的post请求进行拦截，校验表单中的用户名和密码


#### 两个重要接口：
UserDetailService
PasswordEncoder
1.
继承这个过滤器UsernamePasswordAuthenticationFilter 重写里面的方法，认证方法，认证成功，认证失败
创建一个类实现UserDetailService接口，查数据库中的用户名密码写在这个类中,返回一个User对象（spring security中提供的User对象）
2.PasswordEncoder
对密码进行加密，加密后存放到User对象进行返回

#### web权限方案
认证、授权
##### 1.认证
方法1：通过配置文件
spring:
  security:
    user:
      name: lbw
      password: 123456
      
方法2：通过配置类
~~~
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("123");
        auth.inMemoryAuthentication().withUser("sbyh").password(password).roles("admin");
    }
    
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
~~~
方法3：编写自定义实现类,即实现UserDetailService接口
如果没有上面两种，但是实现了UserDetailService接口，sec会去找这个接口的实现类
~~~
@Configuration
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
~~~

从数据库中验证
~~~
CREATE TABLE users(
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	username varchar(50) NOT NULL,
	password VARCHAR(100) NOT NULL
)
ALTER TABLE users ADD INDEX un_pwd_idex(username,password);
~~~

自定义登录页面

2.授权
授权有4个方法
hasAuthority 1个权限 如果当前用户有指定权限，有返回true，没返回false
hasAnyAuthority 多个个权限
hasRole 1个角色
hasAnyRole 多个角色

3.自定义403没有权限页面

4. 注解使用
- @Secured 
拥有某个角色才能访问
1.@EnableGlobalMethodSecurity(securedEnabled = true) // 开启权限注解
2.在Controller方法上使用注解，设置角色
@Secured({"ROLE_user","ROLE_manager","ROLE_admin"})

- @PreAuthorize
@EnableGlobalMethodSecurity(prePostEnabled = true) 
在方法上使用该注解
@PreAuthorize("hasAnyAuthority('menu:system')") //4个方法都可以写在这里

- @PostAuthorize
方法返回之后再校验。
方法会执行，但是如果没权限报403

- @PostFilter
对返回数据做过滤

- @PreFilter
对参数做过滤

- 权限表达式

5.用户注销
- 在配置类中添加退出配置

6.基于数据库自动登录
把浏览器关了，重新打开，用户还能继续打开需要认证的页面
实现原理：
在浏览器的cookie中存加密串（token），cookie可以设置有效时长，在数据库中也存这个加密串，再次登录时进行比对，认证成功可以登录。
- 1）创建表
~~~
CREATE TABLE persistent_login(
series VARCHAR(64) NOT NULL,
	username VARCHAR(64) NOT NULL,
	token VARCHAR(64) NOT NULL,
	last_used TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY(series)
)ENGINE=INNODB DEFAULT CHARSET='utf8';
~~~
- 2）在配置类中注入数据源配置操作数据库对象
~~~
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true); // 自动创建表
        return jdbcTokenRepository;
    }
~~~
- 3) 配置启动自动登录，设置token有效时长
~~~
    .anyRequest().authenticated()
    .and().rememberMe().tokenRepository(persistentTokenRepository())
    .tokenValiditySeconds(60) // token有效时长,单位秒
    .userDetailsService(userDetailsService)
~~~
- 4) 前端设置复选框，选择是否自动登录

7.csrf攻击
参考bilibili 魔王不造反 大佬视频https://www.bilibili.com/video/BV1iW411171s
示例：
正常访问的网站A(存在csrf漏洞) localhost:9000 用户在这里进行转账
恶意网站B localhost:9204 用户在同一个浏览器进入B网站，B网页有一张图片指向A网站的转账操作。csrf完成。
~~~
<html>
<body>
<img src="http://localhost:9000/sendMoney?toUser=hacker01&money=10000"/>
</body>
</html>
~~~

防御：
- 方法1：使用post，但post也不安全 黑客可以在前端中使用立即函数提交表单
- 方法2：加验证码，黑客也能破解：黑客人工操作或者机器识别认证，然后提交转账
- 方法3：http的refer请求头，记录用户来自于哪个网站/网页,可以知道它来自哪个接口/网站。但是refer可以修改，黑客又可以攻击
- 方法4：加token。不把token放在cookie中，而放在请求头或者url中。token要随机的，服务端每次验证完token要销毁，下一次验证要重新生成token。否则黑客拿到了token就可以伪造用户请求。
  使用 anti csrf token

meta中便于读取。

![image-20210225224921114](README.assets/image-20210225224921114.png)

- 方法5：加入自定义header，与方法4类似
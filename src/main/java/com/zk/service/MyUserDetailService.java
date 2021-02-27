package com.zk.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.entity.Users;
import com.zk.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        没有查询数据库认证用户信息
//        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("role");
//        return new User("mary",new BCryptPasswordEncoder().encode("123"),auths);

        Users user = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 可以通过数据动态查询该用户的权限，这里为了演示，写死了每个用户都有admin权限
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("abc,ROLE_admin,menu:system,ROLE_user");
        return new User(user.getUsername(),new BCryptPasswordEncoder().encode(user.getPassword()),auths);
    }
}

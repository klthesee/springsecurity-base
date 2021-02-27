package com.zk.service.impl;

import com.zk.mapper.UsersMapper;
import com.zk.entity.Users;
import com.zk.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chs
 * @since 2021-02-24
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}

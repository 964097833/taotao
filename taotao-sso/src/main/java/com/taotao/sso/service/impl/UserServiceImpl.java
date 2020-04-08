package com.taotao.sso.service.impl;

import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.UserService;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${USER_TOKEN_KEY}")
    private String USER_TOKEN_KEY;
    @Value("${USER_REDIS_EXPIRE}")
    private Integer USER_REDIS_EXPIRE;

    @Override
    public TaotaoResult checkData(String param, Integer type) {

        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();

        if (1 == type) {
            criteria.andUsernameEqualTo(param);
        } else if (2 == type) {
            criteria.andPhoneEqualTo(param);
        } else {
            criteria.andEmailEqualTo(param);
        }

        List<TbUser> list = userMapper.selectByExample(example);

        if (list == null || list.size()==0) {
            return TaotaoResult.ok(true);
        }

        return TaotaoResult.ok(false);
    }

    @Override
    public TaotaoResult createUser(TbUser user) {

        //补全user
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        userMapper.insert(user);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult userLogin(String username, String password) {
        //根据用户名查询
        //创建查询条件
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        //执行查询
        List<TbUser> list = userMapper.selectByExample(example);
        //当数据库中无此数据时，返回
        if (list == null || list.size()==0) {
            return TaotaoResult.build(400, "用户名或密码错误");
        }
        TbUser user = list.get(0);
        //密码的MD5值比对
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            return TaotaoResult.build(400, "用户名或密码错误");
        }
        //生成token
        String token = UUID.randomUUID().toString();
        //保存用户之前情况密码
        user.setPassword(null);
        //将用户信息的token放进redis中
        jedisClient.set(USER_TOKEN_KEY + ":" + token, JsonUtils.objectToJson(user));
        //设置用户信息在redis种的过期时间
        jedisClient.expire(USER_TOKEN_KEY + ":" + token, USER_REDIS_EXPIRE);
        return TaotaoResult.ok(token);
    }

    @Override
    public TaotaoResult getUserByToken(String token) {
        //在redis中查询对应的用户信息
        String json = jedisClient.get(USER_TOKEN_KEY + ":" + token);
        //判断用户信息是否为空
        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "用户未登录或登录信息已过期");
        }
        //重新设置过期时间
        jedisClient.expire(USER_TOKEN_KEY + ":" + token, USER_REDIS_EXPIRE);

        return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
    }

    @Override
    public TaotaoResult userLogout(String token) {

        //判断输入的token是否有效
        String json = jedisClient.get(USER_TOKEN_KEY + ":" + token);
        //判断用户信息是否为空
        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "此token已无效");
        }

        jedisClient.del(USER_TOKEN_KEY + ":" + token);

        return TaotaoResult.ok();
    }
}

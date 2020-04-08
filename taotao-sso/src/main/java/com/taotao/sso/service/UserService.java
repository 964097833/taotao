package com.taotao.sso.service;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserService {

    TaotaoResult checkData(String param, Integer type);
    TaotaoResult createUser(TbUser user);
    TaotaoResult userLogin(String username, String password);

    TaotaoResult getUserByToken(String token);

    TaotaoResult userLogout(String token);
}

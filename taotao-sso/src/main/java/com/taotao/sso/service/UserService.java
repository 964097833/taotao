package com.taotao.sso.service;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    TaotaoResult checkData(String param, Integer type);
    TaotaoResult createUser(TbUser user);
    TaotaoResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response);

    TaotaoResult getUserByToken(String token);

    TaotaoResult userLogout(String token);
}

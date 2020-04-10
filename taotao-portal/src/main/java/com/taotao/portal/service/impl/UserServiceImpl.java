package com.taotao.portal.service.impl;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.UserServoice;
import com.taotao.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserServoice {

    @Value("${SSO_BASE_URL}")
    public String SSO_BASE_URL;
    @Value("${SSO_USER_TOKEN}")
    private String SSO_USER_TOKEN;
    @Value("${SSO_USER_LOGIN}")
    public String SSO_USER_LOGIN;

    @Override
    public TbUser getUserByToken(String token) {

        try {
            //调用sso系统的服务，根据token取用户信息
            String json = HttpClientUtil.doGet(SSO_BASE_URL + SSO_USER_TOKEN + token);
            /*if (StringUtils.isBlank(json)) {
                System.out.println("这里面是空的");
            }*/
            //把json转换成taotaoresult
            TaotaoResult result = TaotaoResult.formatToPojo(json, TbUser.class);
            if (result.getStatus() == 200) {
                TbUser user = (TbUser) result.getData();
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

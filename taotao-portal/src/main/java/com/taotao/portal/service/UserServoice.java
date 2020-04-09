package com.taotao.portal.service;

import com.taotao.pojo.TbUser;

public interface UserServoice {

    TbUser getUserByToken(String token);
}

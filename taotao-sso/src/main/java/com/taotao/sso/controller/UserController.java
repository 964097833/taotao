package com.taotao.sso.controller;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
import com.taotao.utils.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 检查数据
     * @param param
     * @param type
     * @param callback
     * @return
     */
    @RequestMapping("/check/{param}/{type}")
    @ResponseBody
    private Object checkData(@PathVariable String param, @PathVariable Integer type, String callback) {
        TaotaoResult result = null;
        //检查参数的合法性
        if (type < 1 || type > 3) {
            result = TaotaoResult.build(400, "内容类型参数不合法");
        }
        if (result != null) {
            if (callback != null) {
                MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
                mappingJacksonValue.setJsonpFunction(callback);
                return mappingJacksonValue;
            } else {
                return result;
            }
        }
        //查询参数是否存在
        try {
            result = userService.checkData(param, type);
        } catch (Exception e) {
            result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
        if (callback != null) {
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        } else {
            return result;
        }
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult createUser(TbUser user) {
        try {
            TaotaoResult result = userService.createUser(user);
            return result;
        } catch (Exception e) {
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult userLogin(String username, String password,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            TaotaoResult result = userService.userLogin(username, password, request, response);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 根据token查询用户信息
     * @param token
     * @param callback
     * @return
     */
    @RequestMapping("/token/{token}")
    @ResponseBody
    public Object getUserByToken(@PathVariable String token, String callback) {
        TaotaoResult taotaoResult = null;
        try {
            taotaoResult = userService.getUserByToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            taotaoResult = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }

        if (StringUtils.isBlank(callback)) {
            return taotaoResult;
        } else {
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(taotaoResult);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
    }

    @RequestMapping("/logout/{token}")
    @ResponseBody
    public Object userLogout(@PathVariable String token, String callback) {

        TaotaoResult result = null;

        try {
            result = userService.userLogout(token);
        } catch (Exception e) {
            e.printStackTrace();
            result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }

        if (StringUtils.isBlank(callback)) {
            return result;
        } else {
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
    }

    @RequestMapping("/showRegister")
    public String showRegister() {
        return "register";
    }
    @RequestMapping("/showLogin")
    public String showLogin(String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }
}

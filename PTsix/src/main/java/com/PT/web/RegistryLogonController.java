package com.PT.web;


import com.PT.bean.StorekeeperInfoBean;
import com.PT.entity.Store;
import com.PT.entity.User;
import com.PT.service.RegistryLogonService;
import com.PT.tools.BeanToMapUtil;
import com.PT.tools.ResponseData;
import com.PT.tools.TokenOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;


/**
 * created by yxhuang
 * 登录和注册控制
 */
@Controller
@RequestMapping("/api/v1")
public class RegistryLogonController {

    static long TM = 60L*1000L*30L;
    @Autowired
    RegistryLogonService registryLogonService;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> login(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String phone = map.get("phone");
        String password = map.get("password");
        ResponseData responseData = ResponseData.ok();
        User user = registryLogonService.login(phone, password);
        if(null != user) {
            String token = UUID.randomUUID().toString();
            TokenOptions.setKey(TokenOptions.TOKEN_PREFIX+user.getId(), token);
            responseData.putDataValue("userId", user.getId());
            responseData.putDataValue("name", user.getName());
            responseData.putDataValue("phone", user.getPhone());
            responseData.putDataValue("role", user.getRole());
            responseData.putDataValue("accessToken", token);
        } else {
            response.setStatus(400);
            responseData = ResponseData.badRequest();
            responseData.putDataValue("statusCode", 1);
            responseData.putDataValue("errorDesc", "用户名或密码错误");
        }
        return responseData.getData();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> register(@RequestBody Map<String, Object> out, HttpServletResponse response) {
        out.put("name", out.get("bossName"));
        out.put("phone", out.get("bossPhone"));
        ResponseData responseData = ResponseData.createOk();
        User user = null;
        Store store = null;
        try {
            user = (User) BeanToMapUtil.convertMap(User.class, out);
            store = (Store) BeanToMapUtil.convertMap(Store.class, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        User resultUser = registryLogonService.regist(user, store);
        if(null != resultUser) {
            String token = UUID.randomUUID().toString();
            TokenOptions.setKey(TokenOptions.TOKEN_PREFIX+user.getId(), token);
            responseData.putDataValue("userId", resultUser.getId());
            responseData.putDataValue("name", resultUser.getName());
            responseData.putDataValue("phone", resultUser.getPhone());
            responseData.putDataValue("role", resultUser.getRole());
            responseData.putDataValue("accessToken", token);
            response.setStatus(201);
        } else {
            response.setStatus(400);
            responseData.putDataValue("statusCode", 1);
            responseData.putDataValue("errorDesc", "注册失败,该手机号已注册");
        }
        return responseData.getBody();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> register(@RequestParam("verifyPhone") String verifyPhone, HttpServletResponse response) {
        ResponseData responseData = ResponseData.ok();
        response.setStatus(200);
        if(true == registryLogonService.verifyPhone(verifyPhone)) {
            responseData.putDataValue("phoneExist", 1);
        } else {
            responseData.putDataValue("phoneExist", 0);
        }
        return responseData.getBody();
    }
    @RequestMapping(value = "/user/password", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> forgetPwd(@RequestBody Map<String, Object> out, HttpServletResponse response) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        out.put("name", out.get("bossName"));
        String pwd = (String) out.get("password");
        ResponseData responseData = ResponseData.ok();
        response.setStatus(200);
        StorekeeperInfoBean info = (StorekeeperInfoBean) BeanToMapUtil.convertMap(StorekeeperInfoBean.class, out);
        if(false == registryLogonService.changePassword(info, pwd)) {
            response.setStatus(400);
            responseData.putDataValue("statusCode", 1);
            responseData.putDataValue("errorDesc", "信息验证失败");
        }
        return responseData.getBody();
    }

    @RequestMapping(value = "/user/newPassword", method = RequestMethod.PUT)
    public @ResponseBody Map<String, Object> changePwd(HttpServletRequest request, @RequestBody Map<String, Object> out, HttpServletResponse response) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String oldPwd = (String) out.get("oldPassword");
        String newPwd = (String) out.get("password");
        String id = request.getHeader("userId");
        ResponseData responseData = ResponseData.ok();
        response.setStatus(200);
        if(false == registryLogonService.changePassword(Integer.parseInt(id), oldPwd, newPwd)) {
            response.setStatus(400);
            responseData.putDataValue("statusCode", 1);
            responseData.putDataValue("errorDesc", "旧密码错误");
        }
        return responseData.getBody();
    }
}

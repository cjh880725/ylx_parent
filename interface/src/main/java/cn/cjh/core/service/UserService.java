package cn.cjh.core.service;

import cn.cjh.core.pojo.user.User;

public interface UserService {
    //添加用户
    void add(User user);
    //验证码手机号
    void createSmsCode(String phone);
    //验证码匹配
    boolean checkSmsCode(String phone,String code);
}

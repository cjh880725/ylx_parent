package cn.cjh.core.controller;

import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.user.User;
import cn.cjh.core.service.UserService;
import cn.cjh.core.util.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    /**
     * 添加用户
     */
    @RequestMapping("/add")
    public Result add(@RequestBody User user,String smscode){
        try{
            boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smscode);
            if(checkSmsCode){
                userService.add(user);
                return new Result(true,"添加成功");
            }
            return new Result(false,"验证码输入有误");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        //判断手机号格式
        if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            return new Result(false,"手机号格式不正确");
        }
        try{
            userService.createSmsCode(phone);//生成验证码
            return new Result(true,"验证码发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"验证码发送错误");
        }
    }

}

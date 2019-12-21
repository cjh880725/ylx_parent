package cn.cjh.core.service;

import cn.cjh.core.dao.user.UserDao;
import cn.cjh.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue smsDestination;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;


    @Override
    public void add(User user) {
        user.setCreated(new Date());//添加时间
        user.setUpdated(new Date());//删除时间
        String password = DigestUtils.md5Hex(user.getPassword());//对密码加密
        user.setPassword(password);
        userDao.insert(user);
    }

    //生成短信验证码
    @Override
    public void createSmsCode(final String phone) {
        //生成一个6位的随机数（验证码）
        final String smscode = (long) (Math.random()*1000000)+ "";
        System.out.println("验证码："+smscode);
        //验证码放入redis
        redisTemplate.boundHashOps("smscode").put(phone,smscode);
        //发送到activeMQ
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("1321864");
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",phone);//手机号
                mapMessage.setString("template_code",template_code);//模板编号
                mapMessage.setString("sign_name",sign_name);//签名
                Map map = new HashMap();
                map.put("code",smscode);
                mapMessage.setString("param", JSON.toJSONString(map));//参数
                return mapMessage;
            }
        });
    }

    //判断验证码是否正确
    @Override
    public boolean checkSmsCode(String phone, String code) {
        //得到缓存中的验证码
        String smscode = (String)redisTemplate.boundHashOps("smscode").get(phone);
        if(smscode == null||"".equals(smscode)){
            return false;
        }
        if(!code.equals(smscode)){
            return false;
        }
        return true;
    }
}

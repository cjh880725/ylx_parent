package cn.cjh.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class PageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long[] googsIds =(Long[])objectMessage.getObject();
            System.out.println("itemDeleteListener监听接收到消息……"+googsIds);
            boolean b = itemPageService.deleteItemHtml(googsIds);
            System.out.println("页面删除结果为："+b);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

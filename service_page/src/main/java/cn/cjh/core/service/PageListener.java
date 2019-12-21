package cn.cjh.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try{
            String text = textMessage.getText();
            System.out.println("接受到的消息为："+text);
            //生成页面
            itemPageService.genItemHtml(Long.parseLong(text));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

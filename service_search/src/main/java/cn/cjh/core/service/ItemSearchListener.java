package cn.cjh.core.service;

import cn.cjh.core.pojo.item.Item;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息……");
        try{
            TextMessage textMessage = (TextMessage)message;
            String text = textMessage.getText();
            List<Item> items = JSON.parseArray(text, Item.class);
            for(Item item : items){
                System.out.println(item.getId()+" "+item.getTitle());
                //将spec字段中的json字符串转换为map
                Map specMap =JSON.parseObject(item.getSpec());
                //给带注解的字段赋值
                item.setSpecMap(specMap);
            }
            searchService.importList(items);
            System.out.println("成功导入到索引库……");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

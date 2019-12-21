package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.good.Goods;
import cn.cjh.core.pojo.item.Item;
import cn.cjh.core.pojo.pojogroup.Good;
import cn.cjh.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

   /* @Reference
    private ItemPageService itemPageService;*/
    @Reference
    private GoodsService goodsService;
    @Autowired
    private Destination queueSolrDestination;//用于发送solr导入的消息

    @Autowired
    private Destination queueSolrDeleteDestination;//用户在索引库中删除记录

    @Autowired
    private Destination topicPageDestination;//用于生成商品详细页的消息目标(发布订阅)

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination topicPageDeleteDestination;//用于删除静态网页的消息

/*    @Reference
    private SearchService searchService;*/

  /*  @RequestMapping("/testPage")
    public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }*/
    //分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, Integer page, Integer rows){
        //获取登录用户的名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置商家ID
        goods.setSellerId(name);
        return goodsService.search(goods,page,rows);
    }

    //查询单条数据
    @RequestMapping("/findOne")
    public Good findOne(Long id){
        return goodsService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody Good good){
        //检验是否是当前商家的ID
        Good one = goodsService.findOne(good.getGoods().getId());
        //获取当前登陆的商家ID
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!one.getGoods().getSellerId().equals(name)||!good.getGoods().getSellerId().equals(name)){
            return new Result(false,"非法操作");
        }
        try{
            goodsService.update(good);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){

        try{
            //修改MySQL
            goodsService.updateStatus(ids,status);
            if(status.equals("1")){
                //通过审核
                List<Item> itemList = goodsService.findItemByGoodsIdandStatus(ids, status);
                    System.out.println("ssssssssss"+itemList);
                //调用搜索接口实现数据批量导入
                if(itemList.size()>0){
                   // searchService.importList(itemList);
                    final String jsonString = JSON.toJSONString(itemList);
                    jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                return session.createTextMessage(jsonString);
                            }
                    });
                }else {
                    System.out.println("此商品审核没有通过");
                }
            }
            //静态页生成
            for(final Long id:ids){
                //  itemPageService.genItemHtml(id);
                System.out.println("id:"+id);
                jmsTemplate.send(topicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        System.out.println("!!!!!!!!!!!!!!!!");
                        return session.createTextMessage(id+"");
                    }
                });
            }
            return new Result(true,"审核完成");
        }catch (Exception e){
            return new Result(false,"审核失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete( final Long[] ids){
        try{
            goodsService.delete(ids);
            System.out.println("===="+ids);
           // searchService.deleteGoodsById(Arrays.asList(ids));
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
            //删除页面
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
}
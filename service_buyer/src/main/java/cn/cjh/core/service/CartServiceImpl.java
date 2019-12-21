package cn.cjh.core.service;

import cn.cjh.core.dao.item.ItemDao;
import cn.cjh.core.entity.BuyerCart;
import cn.cjh.core.pojo.item.Item;
import cn.cjh.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<BuyerCart> findCartFromRedis(String username) {
        List<BuyerCart> cartList =(List<BuyerCart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String name, List<BuyerCart> cartList) {
        System.out.println("向redis中存入购物车数据……"+name);
        redisTemplate.boundHashOps("cartList").put(name,cartList);
    }

    @Override
    public List<BuyerCart> addGoodsToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {

        //1.根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }
        //2.获取商家ID  商家ID就是商家信息
        String sellerId = item.getSellerId();

        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        BuyerCart buyerCart = searchCartSellerId(cartList, sellerId);
        System.out.println("sellerId：aa"+sellerId);
        //4.如果购物车列表中不存在该商家的购物车
        if(buyerCart == null){
            //4.1 新建购物车对象
            buyerCart = new BuyerCart();
            buyerCart.setSellerId(sellerId);
            buyerCart.setSellerName(item.getSeller());
            //创建商品订单
            OrderItem orderItem = createOrderItem(item , num);

            List orderList = new ArrayList();
            //相关数据放入商家的集合中
            orderList.add(orderItem);
            //购物车清单
            buyerCart.setOrderItemList(orderList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(buyerCart);
        }else {
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            OrderItem orderItem = searchOrderItemByItemId(buyerCart.getOrderItemList(),itemId);
            //5.1. 如果没有，新增购物车明细
            if(orderItem == null){
                // 创建新购买商品的清单
                orderItem = createOrderItem(item,num);
                //向该该商家的购物车清单中加入商品
                buyerCart.getOrderItemList().add(orderItem);
            }else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                //如果数量操作后小于等于0     则移除此条数据
                if(orderItem.getNum()<=0){
                    buyerCart.getOrderItemList().remove(orderItem);
                }
                //如果移除后BuyerCarts的明细数量为0     则移除此购物车
                if(buyerCart.getOrderItemList().size() == 0){
                    cartList.remove(buyerCart);
                }
            }
        }
        return cartList;
    }

    @Override
    public List<BuyerCart> mergeCartList(List<BuyerCart> cartList1, List<BuyerCart> cartList2) {
        System.out.println("合并购物车");
        for(BuyerCart cart : cartList2){
            for(OrderItem orderItem:cart.getOrderItemList()){
                addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {

        for(OrderItem orderItem :orderItemList){
            if(orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    //新增购物车明细
    private OrderItem createOrderItem(Item item, Integer num) {
        if(num<=0){
            throw new RuntimeException("数量不合法");
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        //总价
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }


    private BuyerCart searchCartSellerId(List<BuyerCart> cartList, String sellerId) {

        for(BuyerCart cart:cartList){

            if(sellerId.equals(cart.getSellerId())){

                return cart;
            }
        }
        return null;

    }

}

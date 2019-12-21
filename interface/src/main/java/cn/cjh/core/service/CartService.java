package cn.cjh.core.service;

import cn.cjh.core.entity.BuyerCart;

import java.util.List;

public interface CartService {

    //从redis中查询购物车
    List<BuyerCart> findCartFromRedis(String username);

    //将购物车保存到redis
    void saveCartListToRedis(String name ,List<BuyerCart> cartList);

    //将商品添加到购物车中
    List<BuyerCart> addGoodsToCartList(List<BuyerCart> cartList,Long itemId,Integer num);

    //购物车合并
    List<BuyerCart> mergeCartList(List<BuyerCart> cartList1, List<BuyerCart> cartList2);

}

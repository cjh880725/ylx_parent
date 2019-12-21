package cn.cjh.core.controller;

import cn.cjh.core.entity.BuyerCart;
import cn.cjh.core.entity.Result;
import cn.cjh.core.service.CartService;
import cn.cjh.core.util.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    //获取购物车所有的返回数据
    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList(){
        //得到登陆人的账号，判断当前是否有人登陆 当用户未登陆时 name 为 anonymousUser
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList","UTF-8");
        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }
        List<BuyerCart> cartList_cookie = JSON.parseArray(cartListString, BuyerCart.class);
        //如果未登录  获取本地购物车
        if("anonymousUser".equals(name)){

            return cartList_cookie;
        }else{
            //如果已登录  从redis中获得数据
            List<BuyerCart> cartList_Redis = cartService.findCartFromRedis(name);
            if(cartList_cookie.size()>0){
                //如果本地存在购物车    合并购物车
                cartList_Redis = cartService.mergeCartList(cartList_Redis,cartList_cookie);
                //清除本地cookie数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(name,cartList_Redis);
            }
            return cartList_Redis;
        }

    }

    //添加商品到购物车
    @RequestMapping("/addGoodsToCartList")
    //@CrossOrigin相当于设置了响应头信息
    @CrossOrigin(origins = "http://localhost:8085",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId ,Integer num){

        response.setHeader("Access-Control-Allow-Origin","http://localhost:8085");
        response.setHeader("Access-Control-Allow-Credentials","true");

        //得到当前登录人的账号，判断当前是否有人登陆
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户为："+name);
        try {
            List<BuyerCart> cartList =findCartList();//获取购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if("anonymousUser".equals(name)) {
                //如果未登录
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                System.out.println("向redis中存入数据");
            }else {
                cartService.saveCartListToRedis(name,cartList);
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}

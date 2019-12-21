package cn.cjh.core.controller;

import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.seller.Seller;
import cn.cjh.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController{

    @Reference
    private SellerService sellerSeriver;
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(seller.getPassword());
        seller.setPassword(password);

        try{
            sellerSeriver.add(seller);
            return new Result(true,"注册成功");
        }catch (Exception e){
            return new Result(false,"注册失败");
        }
    }

}

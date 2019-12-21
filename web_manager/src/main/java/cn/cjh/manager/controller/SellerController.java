package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.seller.Seller;
import cn.cjh.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerSeriver;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows){
        return sellerSeriver.search(seller,page,rows);
    }

    @RequestMapping("/findOne")
    public Seller findOne(String sellerId){
        Seller one = sellerSeriver.findOne(sellerId);
        return one;
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try{
            sellerSeriver.updateStatus(sellerId,status);
            return new Result(true,"成功");
        }catch (Exception e){
            return new Result(false,"失败");
        }
    }
}

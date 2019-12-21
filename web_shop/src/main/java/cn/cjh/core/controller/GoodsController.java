package cn.cjh.core.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.good.Goods;
import cn.cjh.core.pojo.pojogroup.Good;
import cn.cjh.core.service.GoodsService;
import cn.cjh.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    @Reference
    private SearchService searchService;
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

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody Good good){
        //获取登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置商家ID
        good.getGoods().setSellerId(name);
        try{
            goodsService.add(good);
            return new Result(true,"添加成功");
        }catch(Exception e){
            return new Result(false,"添加失败");
        }
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
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            goodsService.delete(ids);
            searchService.deleteGoodsById(Arrays.asList(ids));
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/commitQuit")
    public Result commitQuit(Long[] ids,String status){
        try{
            goodsService.commitQuit(ids,status);
            return new Result(true,"操作成功");
        }catch (Exception e){
            return new Result(false,"操作失败");
        }
    }
}

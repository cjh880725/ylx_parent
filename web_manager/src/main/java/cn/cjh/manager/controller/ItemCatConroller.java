package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.item.ItemCat;
import cn.cjh.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatConroller {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){

        return itemCatService.findByParentId(parentId);
    }

    @RequestMapping("/findAll")
    public List<ItemCat>  findAll(){
        return itemCatService.findAll();
    }

    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat){
        System.out.println("add---"+itemCat);
        try{
            itemCatService.add(itemCat);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){

        return itemCatService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try{
            itemCatService.update(itemCat);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            itemCatService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody ItemCat itemCat, Integer page , Integer rows){
        return itemCatService.search(itemCat,page,rows);
    }


}

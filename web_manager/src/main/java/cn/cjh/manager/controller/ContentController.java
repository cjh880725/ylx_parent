package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.ad.Content;
import cn.cjh.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentServer;

    @RequestMapping("/findAll")
    public List<Content> findAll(){
        return contentServer.findAll();
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Content content){
        try{
            contentServer.add(content);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public Content findOne(Long id){
        return contentServer.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Content content){
        try{
            contentServer.update(content);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long []ids){
        try{
            contentServer.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, Integer page, Integer rows){

        return contentServer.search(content,page,rows);
    }
}

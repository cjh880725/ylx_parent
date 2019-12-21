package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.ad.ContentCategory;
import cn.cjh.core.service.CategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class CategoryController {

    @Reference
    private CategoryService categoryServer;

    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
        return categoryServer.findAll();
    }

    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory contentCategory){
        try{
            categoryServer.add(contentCategory);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        return categoryServer.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory){
        try{
            categoryServer.update(contentCategory);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            categoryServer.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory contentCategory, Integer page, Integer rows){

        return categoryServer.search(contentCategory,page,rows);
    }
}

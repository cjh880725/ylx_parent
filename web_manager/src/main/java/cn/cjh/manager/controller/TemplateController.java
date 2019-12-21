package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.template.TypeTemplate;
import cn.cjh.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {

    @Reference
    private TemplateService templateService;

    @RequestMapping("/search")
    public PageResult findALl(@RequestBody TypeTemplate typeTemplate,Integer page,Integer rows){
        return templateService.findAll(typeTemplate,page,rows);
    }

    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
          return templateService.findOne(id);
    }

    @RequestMapping("/add")
    public Result delete(@RequestBody TypeTemplate typeTemplate){
        try{
            templateService.add(typeTemplate);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(true,"添加失败");
        }
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try{
            templateService.update(typeTemplate);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(true,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            templateService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(true,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return templateService.selectOptionList();
    }
}

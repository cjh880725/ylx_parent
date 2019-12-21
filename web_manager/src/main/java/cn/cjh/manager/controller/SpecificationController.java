package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.entity.SpecEntity;
import cn.cjh.core.pojo.specification.Specification;
import cn.cjh.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    //  分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Specification spec,Integer page,Integer rows){
        PageResult result = specificationService.findPage(spec, page, rows);
        return result;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity specEntity) {
        try {
            specificationService.add(specEntity);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            return new Result(false, "添加失败");
        }
    }
     //查询单个数据
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        return specificationService.findOne(id);
    }
    //更新数据
    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity specEntity ){
        try{
            specificationService.update(specEntity);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }
    //删除数据
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
    //查询全部数据
    @RequestMapping("/findAll")
    public List<Specification> findAll(){
        return specificationService.findAll();
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer rows){

        return specificationService.findPages(page,rows);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }

}

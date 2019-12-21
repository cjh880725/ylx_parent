package cn.cjh.manager.controller;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.Result;
import cn.cjh.core.pojo.good.Brand;
import cn.cjh.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPages")
    public PageResult findPages(Integer page,Integer rows){

        return brandService.findPage(page,rows);
    }

    @RequestMapping("/insert")
    public Result insert(@RequestBody Brand brand){
        try{
            brandService.add(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(true,"添加失败");
        }
    }
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(true,"修改失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search( @RequestBody Brand brand,int page,int rows ){
       return brandService.search(brand,page,rows);
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(true,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){

         return brandService.selectOptionList();
    }
}

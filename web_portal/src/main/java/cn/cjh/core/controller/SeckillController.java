package cn.cjh.core.controller;

import cn.cjh.core.pojo.seckill.SeckillGoods;
import cn.cjh.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillController {

    @Reference
    private SeckillService seckillService;

    @RequestMapping("/findList")
    public List<SeckillGoods> findList(){
        return seckillService.findList();
    }

    @RequestMapping("/findOneFromRedis")
    public SeckillGoods findOneFromRedis(Long id){
        return seckillService.findOneFromRedis(id);
    }

}

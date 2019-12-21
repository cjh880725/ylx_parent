package cn.cjh.core.controller;

import cn.cjh.core.pojo.template.TypeTemplate;
import cn.cjh.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {

    @Reference
    private TemplateService templateService;

    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);
    }

    @RequestMapping("/findSpecList")
    public List<Map> findSpecList(Long id){
        return templateService.findSpecList(id);
    }
}

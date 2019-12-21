package cn.cjh.core.controller;

import cn.cjh.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {

    @Reference
    private SearchService searchService;
    @RequestMapping("/search")
    public Map<String ,Object> search(@RequestBody Map<String ,Object> map){
        return searchService.search(map);
    }
}

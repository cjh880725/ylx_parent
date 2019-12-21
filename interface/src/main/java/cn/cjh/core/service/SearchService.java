package cn.cjh.core.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Map<String, Object> search(Map<String, Object> map);
    List searchCategroyList(Map searchMap);
    void importList(List list);
    void deleteGoodsById(List goodsIdList);
}

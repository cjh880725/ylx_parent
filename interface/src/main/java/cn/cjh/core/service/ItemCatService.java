package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    void add(ItemCat itemCat);

    ItemCat findOne(Long id);

    void delete(Long[] ids);

    void update(ItemCat itemCat);

    PageResult search(ItemCat itemCat, Integer page, Integer rows);

    List<ItemCat> findAll();
}

package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.good.Goods;
import cn.cjh.core.pojo.item.Item;
import cn.cjh.core.pojo.pojogroup.Good;

import java.util.List;

public interface GoodsService {
    void add(Good good);

    PageResult search(Goods goods, Integer page, Integer rows);

    Good findOne(Long id);

    void update(Good good);

    void delete(Long[] ids);

    void updateStatus(Long[] ids, String status);

    void commitQuit(Long[] ids, String status);

    List<Item> findItemByGoodsIdandStatus(Long[] ids, String status);
}

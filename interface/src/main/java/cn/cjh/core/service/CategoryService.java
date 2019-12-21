package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.ad.ContentCategory;

import java.util.List;

public interface CategoryService {
    List<ContentCategory> findAll();

    void add(ContentCategory contentCategory);

    ContentCategory findOne(Long id);

    void update(ContentCategory contentCategory);

    void delete(Long[] ids);

    PageResult search(ContentCategory contentCategory, Integer page, Integer rows);
}

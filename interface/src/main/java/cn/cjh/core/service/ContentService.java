package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.ad.Content;

import java.util.List;

public interface ContentService {
    List<Content> findAll();

    void add(Content content);

    Content findOne(Long id);

    void update(Content content);

    void delete(Long[] ids);

    PageResult search(Content content, Integer page, Integer rows);

    List<Content> findByCategoryId(Long categoryId);
}

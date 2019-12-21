package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    PageResult findAll(TypeTemplate typeTemplate, Integer page, Integer rows);

    TypeTemplate findOne(Long id);

    void add(TypeTemplate typeTemplate);

    void update(TypeTemplate typeTemplate);

    void delete(Long[] ids);

    List<Map> selectOptionList();

    public List<Map> findSpecList(Long id);
}

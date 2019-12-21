package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.SpecEntity;
import cn.cjh.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    //添加数据
    void add(SpecEntity specEntity);
    //分页查询数据+模糊查询
    PageResult findPage(Specification spec, Integer page, Integer rows);
    //查寻单条数据
    SpecEntity findOne(Long id);
    //更新数据
    void update(SpecEntity specEntity);
    //删除数据
    void delete(Long [] ids);

    List<Specification> findAll();

    PageResult findPages(Integer page, Integer rows);

    List<Map> selectOptionList();
}

package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询全部数据
    public List<Brand> findAll();

    //分页查询数据
    public PageResult findPage(Integer pageNum, Integer pageSize);

    //添加数据
    public void add(Brand brand);

    //查询单个数据
    public Brand findOne(Long id);

    //修改数据
    public void update(Brand brand);

    //模糊查询
    PageResult search(Brand brand, int page, int rows);

    //删除
    public void delete(Long[] id);

    List<Map> selectOptionList();
}
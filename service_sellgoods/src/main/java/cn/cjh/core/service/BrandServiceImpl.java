package cn.cjh.core.service;

import cn.cjh.core.dao.good.BrandDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.good.Brand;
import cn.cjh.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;
    @Override
    public List<Brand> findAll() {

        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {

        //设定分页的插件条件
        PageHelper.startPage(pageNum,pageSize);
        //查询
        Page<Brand> page =(Page<Brand>)brandDao.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Brand brand) {
        brandDao.insert(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKey(brand);
    }

    @Override
    public PageResult search(Brand brand, int page, int rows) {

        //设定分页的插件条件
        PageHelper.startPage(page,rows);
        //模糊查询条件拼接
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();

        if(brand != null){
            if(brand.getName() != null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar() != null &&brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<Brand> p = (Page<Brand>)brandDao.selectByExample(brandQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void delete(Long[] ids) {

        for (Long id:ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}

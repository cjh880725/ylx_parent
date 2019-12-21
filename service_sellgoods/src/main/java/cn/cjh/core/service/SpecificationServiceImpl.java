package cn.cjh.core.service;

import cn.cjh.core.dao.specification.SpecificationDao;
import cn.cjh.core.dao.specification.SpecificationOptionDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.entity.SpecEntity;
import cn.cjh.core.pojo.specification.Specification;
import cn.cjh.core.pojo.specification.SpecificationOption;
import cn.cjh.core.pojo.specification.SpecificationOptionQuery;
import cn.cjh.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public List<Specification> findAll() {


        List<Specification> specifications = specificationDao.selectByExample(null);

        return specifications;
    }

    @Override
    public PageResult findPages(Integer page, Integer rows) {

        //分页插件
        PageHelper.startPage(page, rows);

        Page<Specification> p = (Page<Specification>)specificationDao.selectByExample(null);

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

    @Override
    public void add(SpecEntity specEntity) {

        //1. 添加规格对象
        specificationDao.insertSelective(specEntity.getSpecification());

        //2. 添加规格选项对象
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()){
                //设置规格选项外键
                option.setSpecId(specEntity.getSpecification().getId());
                specificationOptionDao.insertSelective(option);
            }
        }
    }

    @Override
    public PageResult findPage(Specification spec, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if(spec!=null){
            if(spec.getSpecName()!=null&&!"".equals(spec.getSpecName())){
                criteria.andSpecNameLike("%"+spec.getSpecName()+"%");
            }
        }
        Page<Specification> specList =(Page<Specification>) specificationDao.selectByExample(query);
        return new PageResult(specList.getTotal(),specList.getResult());
    }

    //查询单条数据
    @Override
    public SpecEntity findOne(Long id) {

        Specification specification = specificationDao.selectByPrimaryKey(id);

        SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
        criteria.andSpecIdEqualTo(id);

        List<SpecificationOption> optionList = specificationOptionDao.selectByExample(optionQuery);

        SpecEntity specEntity = new SpecEntity();

        specEntity.setSpecification(specification);
        specEntity.setSpecificationOptionList(optionList);

        return specEntity;
    }

    @Override
    public void update(SpecEntity specEntity) {

        //修改规范
        specificationDao.updateByPrimaryKey(specEntity.getSpecification());
        //修改规范选项
        //添加数据

        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        //先清空已有数据
        specificationOptionDao.deleteByExample(specificationOptionQuery);
        //设置外键
       for( SpecificationOption option: specEntity.getSpecificationOptionList()){
           option.setSpecId(specEntity.getSpecification().getId());
           specificationOptionDao.insertSelective(option);
       }
    }

    @Override
    public void delete(Long[] ids) {

        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        for (Long id : ids) {
            specificationDao.deleteByPrimaryKey(id);
            criteria.andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(specificationOptionQuery);
        }
    }


}

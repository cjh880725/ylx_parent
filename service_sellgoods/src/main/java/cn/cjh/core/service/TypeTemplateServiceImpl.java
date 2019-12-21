package cn.cjh.core.service;

import cn.cjh.core.dao.specification.SpecificationOptionDao;
import cn.cjh.core.dao.template.TypeTemplateDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.specification.SpecificationOption;
import cn.cjh.core.pojo.specification.SpecificationOptionQuery;
import cn.cjh.core.pojo.template.TypeTemplate;
import cn.cjh.core.pojo.template.TypeTemplateQuery;
import cn.cjh.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TypeTemplateServiceImpl implements TemplateService {
    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    public void saveToRedis(){
        //查询tb-type_template
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for(TypeTemplate typeTemplate :typeTemplates){
            //取品牌列表
           List<Map>  brandList = JSON.parseArray(typeTemplate.getBrandIds(),Map.class);
           //redis 存该类型对应的所有品牌数据
            redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).put(typeTemplate.getId(),brandList);
            //查询规格
            List<Map> specList = findSpecList(typeTemplate.getId());//根据模板ID查询规格列表
            redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).put(typeTemplate.getId(),specList);
        }
    }
    //添加
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }
    //分页带条件的查询
    @Override
    public PageResult findAll(TypeTemplate typeTemplate, Integer page, Integer rows) {
        saveToRedis();
        PageHelper.startPage(page,rows);
        //模糊查询
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if(typeTemplate != null){
            if(typeTemplate.getName() != null && !"".equals(typeTemplate.getName())){
                criteria.andNameLike("%"+typeTemplate.getName()+"%");
            }
        }
        //执行查询操作
        Page<TypeTemplate> templateList = (Page<TypeTemplate>)typeTemplateDao.selectByExample(query);

        return new PageResult(templateList.getTotal(),templateList.getResult());
    }

    //修改操作之数据回显
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }
    //修改
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }
    //删除
    @Override
    public void delete(Long[] ids) {
        if(ids != null){
            for(Long id : ids){
                typeTemplateDao.deleteByPrimaryKey(id);
            }
        }
    }
    //模板列表
    @Override
    public List<Map> selectOptionList() {
        return typeTemplateDao.selectOptionList();
    }

    @Override
    public List<Map> findSpecList(Long id) {
        //查询模板
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
       if(list != null){
           for(Map map :list){
               //查询贵个选项列表
               SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
               SpecificationOptionQuery.Criteria criteria = optionQuery.createCriteria();
               criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
               List<SpecificationOption> options = specificationOptionDao.selectByExample(optionQuery);
               map.put("options",options);
           }
       }
        return list;
    }

}
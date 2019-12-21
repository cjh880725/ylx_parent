package cn.cjh.core.service;

import cn.cjh.core.dao.ad.ContentCategoryDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.ad.ContentCategory;
import cn.cjh.core.pojo.ad.ContentCategoryQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;

    //查找全部数据
    @Override
    public List<ContentCategory> findAll() {
        return contentCategoryDao.selectByExample(null);
    }

    //添加数据
    @Override
    public void add(ContentCategory contentCategory) {
        contentCategoryDao.insertSelective(contentCategory);
    }
    //查询单个数据
    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryDao.selectByPrimaryKey(id);
    }
    //修改
    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    //删除
    @Override
    public void delete(Long[] ids) {
       if(ids != null){
           for(Long id:ids){
               contentCategoryDao.deleteByPrimaryKey(id);
           }
       }
    }

    //分页查询
    @Override
    public PageResult search(ContentCategory contentCategory, Integer page, Integer rows) {

        PageHelper.startPage(page,rows);

        ContentCategoryQuery contentCategoryQuery= new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = contentCategoryQuery.createCriteria();
        if(contentCategory != null){
            if(contentCategory.getName() != null && !"".equals(contentCategory.getName())){
                criteria.andNameLike("%"+contentCategory.getName()+"%");
            }
        }
        Page<ContentCategory> p = (Page<ContentCategory>)contentCategoryDao.selectByExample(contentCategoryQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }
}

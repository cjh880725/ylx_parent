package cn.cjh.core.service;

import cn.cjh.core.dao.ad.ContentDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.ad.Content;
import cn.cjh.core.pojo.ad.ContentQuery;
import cn.cjh.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate;

    //查找全部数据
    @Override
    public List<Content> findAll() {
        return contentDao.selectByExample(null);
    }

    //添加数据
    @Override
    public void add(Content content) {
        //清除对应缓存中的数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        contentDao.insertSelective(content );
    }
    //查询单个数据
    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }
    //修改
    @Override
    public void update(Content content ) {
        Long categoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        //清除ID原来对应的缓存中的数据（getCategoryId改变之前对应的缓存）
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(categoryId);
        //清除改变之后的缓存中的数据（getCategoryId改变之后对应的缓存）
        if(categoryId != content.getCategoryId()){
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        }
        //更新数据
        contentDao.updateByPrimaryKeySelective(content );
    }

    //删除
    @Override
    public void delete(Long []ids) {
       if(ids != null){
           for(Long id:ids){
               //查找ID对应的数据库中的数据
               Content content = contentDao.selectByPrimaryKey(id);
               //删除缓存中的数据
               redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
               //删除数据库中的数据
               contentDao.deleteByPrimaryKey(id);
           }
       }
    }

    //分页查询
    @Override
    public PageResult search(Content  content , Integer page, Integer rows) {

        PageHelper.startPage(page,rows);

        ContentQuery contentQuery= new ContentQuery();
        ContentQuery.Criteria criteria = contentQuery.createCriteria();
        if(content  != null){
            if(content.getTitle() != null && !"".equals(content.getTitle())){
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        Page<Content> p = (Page<Content>)contentDao.selectByExample(contentQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {

        List<Content> list = (List<Content>)redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        if(list == null){
            System.out.println("mySQL中的数据");
            ContentQuery contentQuery = new ContentQuery();
            ContentQuery.Criteria criteria = contentQuery.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            criteria.andStatusEqualTo("1");//表示有效
            list = contentDao.selectByExample(contentQuery);
            //添加到Redis缓存
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId,list);
        }
        System.out.println("redis缓存中的数据");
        return list;
    }
}

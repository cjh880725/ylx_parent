package cn.cjh.core.service;

import cn.cjh.core.dao.item.ItemCatDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.item.ItemCat;
import cn.cjh.core.pojo.item.ItemCatQuery;
import cn.cjh.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //查询所有的item放入redis
        List<ItemCat> list = findAll();
        for(ItemCat itemCat:list){
            redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).put(itemCat.getName(),itemCat.getTypeId());
        }
        System.out.println("redis缓存成功");
        return itemCatDao.selectByExample(itemCatQuery);
    }

    @Override
    public void add(ItemCat itemCat) {

        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {
        if(ids!=null){
            for(Long id:ids){
                itemCatDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKey(itemCat);
    }

    @Override
    public PageResult search(ItemCat itemCat, Integer page, Integer rows) {

        //分页插件
        PageHelper.startPage(page,rows);

        /*ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        criteria.andParentIdEqualTo(itemCat.getParentId());*/
        Page<ItemCat> p = ( Page<ItemCat>) itemCatDao.selectByExample(null);

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public List<ItemCat> findAll() {

        return itemCatDao.selectByExample(null);
    }
}

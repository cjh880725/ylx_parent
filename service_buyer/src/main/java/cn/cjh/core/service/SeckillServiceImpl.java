package cn.cjh.core.service;

import cn.cjh.core.dao.seckill.SeckillGoodsDao;
import cn.cjh.core.pojo.seckill.SeckillGoods;
import cn.cjh.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private RedisTemplate redisTemplate;
    //返回当前正在秒杀的商品
    @Override
    public List<SeckillGoods> findList() {

        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        if(seckillGoodsList == null && seckillGoodsList.size() ==0){
            SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
            criteria.andStatusEqualTo("1");//审核通过
            criteria.andStockCountGreaterThan(0);//剩余库存大于0
            criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间等于当前时间
            criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间
            seckillGoodsList= seckillGoodsDao.selectByExample(seckillGoodsQuery);
            //将商品放入到缓存中
            System.out.println("商品类表装入缓存中");
            for(SeckillGoods seckillGoods: seckillGoodsList){
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }
        }

        return seckillGoodsList;
    }

    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        return  (SeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);
    }
}

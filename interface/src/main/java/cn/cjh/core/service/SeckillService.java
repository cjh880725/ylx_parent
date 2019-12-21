package cn.cjh.core.service;

import cn.cjh.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillService {

    public List<SeckillGoods> findList();

    //根据ID获取实体（从缓存中读取）
    public SeckillGoods findOneFromRedis(Long id);
}

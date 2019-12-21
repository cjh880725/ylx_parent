package cn.cjh.core.service;

import cn.cjh.core.dao.good.BrandDao;
import cn.cjh.core.dao.good.GoodsDao;
import cn.cjh.core.dao.good.GoodsDescDao;
import cn.cjh.core.dao.item.ItemCatDao;
import cn.cjh.core.dao.item.ItemDao;
import cn.cjh.core.dao.seller.SellerDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.good.Brand;
import cn.cjh.core.pojo.good.Goods;
import cn.cjh.core.pojo.good.GoodsDesc;
import cn.cjh.core.pojo.good.GoodsQuery;
import cn.cjh.core.pojo.item.Item;
import cn.cjh.core.pojo.item.ItemCat;
import cn.cjh.core.pojo.item.ItemQuery;
import cn.cjh.core.pojo.pojogroup.Good;
import cn.cjh.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.*;

@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Good good) {
        //新增商品需要审核，默认状态值为0
        good.getGoods().setAuditStatus("0");
        goodsDao.insert(good.getGoods());
        //商品描述
        good.getGoodsDesc().setGoodsId(good.getGoods().getId());
        goodsDescDao.insert(good.getGoodsDesc());

        insertItem(good);
    }

    //分页查找
    @Override
    public PageResult search(Goods good, Integer page, Integer rows) {

        PageHelper.startPage(page,rows);

        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        criteria.andIsDeleteIsNull();
        criteria.andIsMarketableIsNull();
        if(good.getSellerId()!=null){
            criteria.andSellerIdEqualTo(good.getSellerId());
        }
        if(good.getGoodsName()!=null){
            criteria.andGoodsNameLike("%"+good.getGoodsName()+"%");
        }
        if(good.getAuditStatus()!=null){
            criteria.andAuditStatusLike("%"+good.getAuditStatus()+"%");
        }
        /*if(good.getSellerId() != null && !"".equals(good.getSellerId()) && !"admin".equals(good.getSellerId()) && !"wc".equals(good.getSellerId())){
            criteria.andSellerIdEqualTo(good.getSellerId());
        }*/
       Page<Goods> p = (Page<Goods>)goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Good findOne(Long id) {

        Good good = new Good();
        Goods goods = goodsDao.selectByPrimaryKey(id);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        good.setGoods(goods);
        good.setGoodsDesc(goodsDesc);
        good.setItemList(items);
        return good;
    }

    @Override
    public void update(Good good) {
        //设置未审批状态
        good.getGoods().setAuditStatus("0");
        goodsDao.updateByPrimaryKey(good.getGoods());
        goodsDescDao.updateByPrimaryKey(good.getGoodsDesc());
        //删除表中原有数据
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(good.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        insertItem(good);

    }

    @Override
    public void delete(Long[] ids) {

        if(ids != null){
            for(Long id:ids) {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsDelete("1");
                goodsDao.updateByPrimaryKeySelective(goods);
            }
        }
    }

    @Override
    public void updateStatus( Long[] ids, String status) {
        if(ids != null){
            for(Long id: ids){
                //审核Goods表
                Goods goods = goodsDao.selectByPrimaryKey(id);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);
                //审核Item表
                Item item = new Item();
                item.setStatus(status);
                ItemQuery itemQuery = new ItemQuery();
                ItemQuery.Criteria criteria = itemQuery.createCriteria();
                criteria.andGoodsIdEqualTo(id);
                itemDao.updateByExampleSelective(item,itemQuery);
            }
        }
    }

    @Override
    public void commitQuit(Long[] ids, String status) {
        if (ids != null){
           for(Long id:ids){
               Goods goods = new Goods();
               goods.setId(id);
               goods.setIsMarketable(status);
               goodsDao.updateByPrimaryKeySelective(goods);
           }
        }
    }

    @Override
    public List<Item> findItemByGoodsIdandStatus(Long[] ids, String status) {

        System.out.println("ids:"+Arrays.toString(ids) +"status:"+status);
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        criteria.andStatusEqualTo("1");
        System.out.println("1111"+itemDao.selectByExample(itemQuery));
        return itemDao.selectByExample(itemQuery);
    }

    //修改规格
    public void insertItem(Good good){
        //0代表不启动规格，1代表启动规格
        if("1".equals(good.getGoods().getIsEnableSpec())){
            // 勾选复选框  有库存数据
            if(good.getItemList()!=null){
                // 遍历库存对象
                for(Item item:good.getItemList()){
                    // 标题由商品名+规格组成 供消费者搜索使用
                    String title = good.getGoods().getGoodsName();
                    String specJsonStr = item.getSpec();
                    // 将json  转成对象
                    Map speMap = JSON.parseObject(specJsonStr, Map.class);
                    // 获取speMap中的value集合
                    Collection<String> values = speMap.values();
                    for(String value:values){
                        // title=title+value   小米手机 5g版本 64g 电信版
                        title+=" "+value;
                    }
                    item.setTitle(title);
                    //  设置库存的对象的属性值
                    setItemValue(good,item);
                    itemDao.insert(item);
                }
            }
        }else {
            //  没有勾选   没有库存  但是初始化一条
            Item item = new Item();
            item.setPrice(new BigDecimal("666666666666"));
            // 库存量
            item.setNum(0);
            // 初始化规格
            item.setSpec("{}");
            //标题
            item.setTitle(good.getGoods().getGoodsName());
            // 设置库存对象的属性值
            setItemValue(good,item);
            itemDao.insert(item);

        }
    }
    private Item setItemValue(Good good,Item item){
        // 商品的id
        item.setGoodsId(good.getGoods().getId());
        //创建时间
        item.setCreateTime(new Date());
        // 更新的时间
        item.setUpdateTime(new Date());
        // 库存的状态
        item.setStatus("0");
        // 分类的id  库存分类
        item.setCategoryid(good.getGoods().getCategory3Id());
        // 分类的名称
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(good.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        // 品牌的名称
        Brand brand = brandDao.selectByPrimaryKey(good.getGoods().getBrandId());
        item.setBrand(brand.getName());
        // 卖家名称
        Seller seller = sellerDao.selectByPrimaryKey(good.getGoods().getSellerId());
        item.setSeller(seller.getName());
        item.setSellerId(seller.getSellerId());
        // 式例的图片
        String itemImages = good.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if(maps!=null&&maps.size()>0){
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }
}

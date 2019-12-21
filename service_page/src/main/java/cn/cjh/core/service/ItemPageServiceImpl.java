package cn.cjh.core.service;

import cn.cjh.core.dao.good.GoodsDao;
import cn.cjh.core.dao.good.GoodsDescDao;
import cn.cjh.core.dao.item.ItemCatDao;
import cn.cjh.core.dao.item.ItemDao;
import cn.cjh.core.pojo.good.Goods;
import cn.cjh.core.pojo.good.GoodsDesc;
import cn.cjh.core.pojo.item.Item;
import cn.cjh.core.pojo.item.ItemCat;
import cn.cjh.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService , ServletConfigAware {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freemarkerConfig ;

    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ItemDao itemDao;

    @Override
    public boolean genItemHtml(Long id) {
        try{
            Configuration configuration =freemarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map map = new HashMap();
            //加载商品表数据
            Goods goods = goodsDao.selectByPrimaryKey(id);
            map.put("goods" ,goods);
            //加载商品扩展表数据
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            map.put("goodsDesc",goodsDesc);
            //商品分类
            ItemQuery itemQuery = new ItemQuery();
            ItemQuery.Criteria criteria = itemQuery.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<Item> items = itemDao.selectByExample(itemQuery);
            if(goods != null){
                ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
                ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
                ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
                // 封装数据
                map.put("itemCat1",itemCat1.getName());
                map.put("itemCat2",itemCat2.getName());
                map.put("itemCat3",itemCat3.getName());
            }
            map.put("itemList",items);

            String path =  id +".html";
            System.out.println("========path====="+path);
            String realPath = getRealPath(path);
            Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)),"utf-8");
            template.process(map,out);
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try{
            for(Long googsId:goodsIds){
                new File(googsId+".html").delete();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        System.out.println("======Realpath======"+realPath);
        return realPath;
    }
    @Override
    public void setServletConfig(ServletConfig servletConfig) {

    }
}

package cn.cjh.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cjh.core.pojo.item.Item;
import cn.cjh.core.util.Constants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    //高亮
    @Override
    public Map<String,Object> search(Map searchMap){

        Map map = new HashMap();

        //-----------------------------高亮数据---------------------------
        //创建查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //过滤查询
        //获取点击分类条件进行判断
        if(!"".equals(searchMap.get("category")) && searchMap.get("category") != null){
            //按照关键字查询
            //创建查询条件
            Criteria category = new Criteria("item_category").is(searchMap.get("category"));
            //创建过滤对象 并将条件查询的对象放入到过滤对象中（创建空对象再将条件放入到调用addCriteria()）
            FilterQuery filterQuery = new SimpleFilterQuery(category);
            //过滤对象放入到查询对象中
            query.addFilterQuery(filterQuery);

        }
        //按品牌筛选
        //获取点击品牌条件进行判断
        if(!"".equals(searchMap.get("brand")) && searchMap.get("brand")!= null){
            //按照关键字查询
            //创建条件查询
            Criteria category = new Criteria("item_brand").is(searchMap.get("brand"));
            //创建过滤查询的对象并将查询条件放入到过滤对象中
            FilterQuery filterQuery = new SimpleFilterQuery(category);
            //将过滤对象放入到查询对象中
            query.addFilterQuery(filterQuery);
        }
        //过滤规格
        //获取点击规格条件进行判断
        if(searchMap.get("spec")!= null && "".equals(searchMap.get("spec"))){
            Map<String,String> specMap =(Map<String,String>) searchMap.get("spec");
            for(String key : specMap.keySet()){
                Criteria criteria1 = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        String price =(String)searchMap.get("price");
        // System.out.println("`````````````````"+price);
        //根据价格查询
        if(price != null && !"".equals(price)){
            //切分价格
            String[] split = price.split("-");
            //System.out.println("::"+split);
            if(split != null && split.length == 2){
                //System.out.println("1111111111");
                //说明大于等于最小值 如果第一个值为0 进不来
                if(!"0".equals(split[0])){
                    //System.out.println(split[0]);
                    //创建查询条件(greaterThanEqual区间内部)
                    Criteria category = new Criteria("item_price").greaterThanEqual(split[0]);
                    //创建过滤对象 并将条件查询的对象放入到过滤对象中（创建空对象再将条件放入到调用addCriteria()）
                    FilterQuery filterQuery = new SimpleFilterQuery(category);
                    //过滤对象放入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
                //小于等于最大值 如果最后的元素为*  *为最大值 进不来
                if( !"*".equals(split[1])){
                    //System.out.println(split[1]);
                    //创建查询条件(greaterThanEqual区间内部)
                    Criteria category = new Criteria("item_price").lessThanEqual(split[1]);
                    //创建过滤对象 并将条件查询的对象放入到过滤对象中（创建空对象再将条件放入到调用addCriteria()）
                    FilterQuery filterQuery = new SimpleFilterQuery(category);
                    //过滤对象放入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }

        //高亮属性设定
        HighlightOptions options = new HighlightOptions();
        //1.设定属性 属性名 必须 与 solr 字段名一致
        options.addField("item_title");//设置高亮的域
        //2.设定样式的前缀 后缀
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        //3放入查询对象中
        query.setHighlightOptions(options);

        //-------------------------------------------排序-----------------------------------------
        //价格升序与降序
        String sortValue =(String)searchMap.get("sort");//ASC DESC
        String sortField = (String)searchMap.get("sortField");//字段排序
        if(sortValue != null && !"".equals(sortValue)){
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        //---------------------------------------分页查询-----------------------------------------
        Integer pageNo =(Integer)searchMap.get("pageNo"); //获取页码
        Integer pageSize = (Integer)searchMap.get("pageSize"); //获取每页记录数
        //如果记录数没有 则记当前页码为1
        if(pageNo == null){
            pageNo = 1;
        }
        //如果记录数没有 默认为20
        if(pageSize == null){
            pageSize = 20;
        }
        //将数据当如查询对象中
        query.setOffset((pageNo-1)*pageSize);//从第几条数据开始查询
        query.setRows(pageSize);

        //4查询
        String keywords = (String)searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));//去除keywords字段中的空格
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(query, Item.class);
        //遍历实体
        //循环高亮入口集合
        for (HighlightEntry<Item> h:page.getHighlighted()) {
            //获取源实体类############
            Item item = h.getEntity();
            //########################
            if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                //<em style = 'color:red'>华为  </em>
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }

        map.put("rows",page.getContent());
        map.put("total",page.getTotalElements());
        map.put("totalPages",page.getTotalPages());
        //-----------------------------分类的数据---------------------------
        List categoryList = searchCategroyList(searchMap);
        map.put("categoryList",categoryList);
        //-----------------------------取品牌规格-----------------------------
        //首次进入时分类是没有选择过得即空，使用分类排序 下标为0的位置
        //如果分类已有选择则使用分类进行排序
        String categoryName =(String)searchMap.get("category");
        if("".equals(categoryName)){
            if(categoryList.size()>0){
                //首次进入
                map.putAll(searchBrandAndSpecList(categoryList.get(0)+""));
            }
        }else {

            map.putAll(searchBrandAndSpecList(categoryName));
        }

         return map;
    }

    //分类数据
    @Override
    public List searchCategroyList(Map searchMap){
        //查询结果的集合
        List list=new ArrayList();
        //创建solr查询对象
        Query query = new SimpleQuery();
        //建立查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //将条件告诉查询对象
        query.addCriteria(criteria);
        //分组查询
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        //将分组对象告诉查询对象
        query.setGroupOptions(groupOptions);
        //执行分组分页查询
        GroupPage groupPage =  solrTemplate.queryForGroupPage(query,Item.class);
        //从配置对象中去查询结果
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        //从查询结果中取首页要查询的数据
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        //将数据装入到List容器
        List<GroupEntry<Item>> content = groupEntries.getContent();
        //循环遍历结果遍历到容器中
        for(GroupEntry<Item> item:content){
            list.add(item.getGroupValue());
        }
        return list;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteGoodsById(List goodsIdList) {
        //solr查询对象
        System.out.println("删除商品ID::"+goodsIdList);
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    public Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        //从redis中取模板ID
        Long categoryId = (Long )redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(category);
        //
        if(categoryId !=null){
           List<Map> brandList= (List<Map>)redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(categoryId);
           List<Map> specList = (List<Map>)redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(categoryId);
           map.put("brandList",brandList);
           map.put("specList",specList);
        }
        return map;
    }

    public Map<String, Object> searchList(Map<String, Object> searchMap) {

        //获取查询条件
        String keywords = (String)searchMap.get("keywords");
        Integer pageNo =(Integer)searchMap.get("pageNo");
        //每页查询多少条
        Integer pageSize =(Integer)searchMap.get("pageSize");

        //封装查询对象
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询的条件放入查询的对象
        query.addCriteria(criteria);
        if(pageNo == null||pageNo<=0){
            pageNo=1;
        }
        //当前页
        Integer start = (pageNo-1) * pageSize;
        //设置第几条开始
        query.setOffset(start);
        //每页查询多少条数据
        query.setRows(pageSize);
        //去solr查询并返回结果
        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows",items.getContent());
        map.put("total",items.getTotalElements());
        map.put("totalPages",items.getTotalPages());
        return map;
    }
}

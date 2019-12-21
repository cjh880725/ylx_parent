package cn.cjh.core.service;

import cn.cjh.core.dao.item.ItemDao;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

@Service
public class SolrManagerServiceImpl implements SolrManagerService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;
}

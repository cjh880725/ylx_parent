package cn.cjh.core.service;

import cn.cjh.core.dao.seller.SellerDao;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.seller.Seller;
import cn.cjh.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class SellerSeriverImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;
    @Override
    public void add(Seller seller) {
        //设置初始化时间
        seller.setCreateTime(new Date());
        //设置审核状态
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Seller seller, Integer page, Integer rows) {

        //分页插件
        PageHelper.startPage(page,rows);
        SellerQuery sellerQuery = new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();

        if(seller !=null){
            if(seller.getName()!=null&& !"".equals(seller.getName())){
                criteria.andNameLike("%"+seller.getName()+"%");
            }
            if(seller.getNickName()!=null&&!"".equals(seller.getNickName())){
                criteria.andNickNameLike("%"+seller.getNickName()+"%");
            }
        }
        criteria.andStatusEqualTo(seller.getStatus());
        Page<Seller> p = (Page<Seller>)sellerDao.selectByExample(sellerQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKey(seller);
    }
}

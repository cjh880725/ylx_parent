package cn.cjh.core.service;

import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.seller.Seller;

public interface SellerService {
    void add(Seller seller);

    PageResult search(Seller seller, Integer page, Integer rows);

    Seller findOne(String id);

    public void updateStatus(String sellerId,String status);

}

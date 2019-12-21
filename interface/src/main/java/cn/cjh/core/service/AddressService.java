package cn.cjh.core.service;

import cn.cjh.core.pojo.address.Address;

import java.util.List;

public interface AddressService {
    //根据用户查询地址
    List<Address> findListByUserId(String userId);
}

package cn.cjh.core.service;

import cn.cjh.core.dao.address.AddressDao;
import cn.cjh.core.pojo.address.Address;
import cn.cjh.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;
    //查询用户的地址
    @Override
    public List<Address> findListByUserId(String userId) {

        AddressQuery addressQuery = new AddressQuery();
        AddressQuery.Criteria criteria = addressQuery.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return addressDao.selectByExample(addressQuery);
    }
}
